package de.kah2.libZodiac;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.bp.LocalDate;

import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import de.kah2.libZodiac.planetary.LunarPhase;
import de.kah2.libZodiac.planetary.PlanetaryDayData;
import java9.util.concurrent.CompletableFuture;

/**
 * This class contains logic for calculation of planetary data.
 */
class CalendarGenerator implements ProgressListener {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final Calendar calendar;

    private final CalendarData days;

    private final SortedSet<Day> newlyGenerated = Collections.synchronizedSortedSet( new TreeSet<>() );

    private final ProgressManager progressManager = new ProgressManager();

    private final static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    private int maxThreadCount = 0;

    private ThreadPoolExecutor executor;

    private final LinkedList<CompletableFuture<Day>> activeCalculations = new LinkedList<>();

    private boolean isSubmittingJobs = false;

    // true during calculation to allow main thread to wait for calculation jobs
    private boolean mainThreadMustWait = false;
    private final static int THREAD_WAIT_INTERVALL_MS = 500;

    // Needed for extension:
    private SortedSet<Day> extensionCache;
    private boolean isExtendingPast;

    CalendarGenerator(Calendar calendar) {
        this.calendar = calendar;
        this.days = calendar.getDays();

        this.progressManager.addProgressListener(this);
    }

    /**
     * Main method to import data.
     * @see Calendar#importDays(List)
     */
    void importDays(final List<DayStorableDataSet> storedDays){

        this.progressManager.notifyStateChanged(State.IMPORTING);

        this.days.importDays(storedDays);

        this.updateLunarPhases( this.days.allAsList() );

        if (this.calendar.getScope() == Calendar.Scope.CYCLE) {
            this.countDaysToLunarExtremesInBothDirections();
        }

        this.progressManager.notifyStateChanged(State.IMPORT_FINISHED);
    }

    /**
     * STEP 1: Starts generation. Called by {@link Calendar#startGeneration()}.
     */
    void startGeneration() {

        this.log.trace("######## startGeneration() ########");

        this.newlyGenerated.clear();

        // Disable listeners, otherwise a state change would be triggered when initialising progressManager
        this.progressManager.reset();
        this.progressManager.estimateExtensions(this.calendar);
        this.progressManager.notifyStateChanged(State.GENERATING);

        this.executor = this.createExecutor();

        DateRange rangeNeeded = this.getRangeNeededToCalculate();

        this.enableWaiting();

        this.generateDaysNeededInExpectedRange(rangeNeeded);

        this.waitForWorkerThreads();
    }

    /**
     * Generates the days not already present within given range
     * and updates the {@link Calendar}.
     */
    private void generateDaysNeededInExpectedRange(DateRange range) {

        Collection<LocalDate> missingDates = this.days.getMissingDates( range );

        if (missingDates.size() == 0) {
            this.continueOnMainThread();
            return;
        }

        this.progressManager.addNumberOfDaysToGenerate(missingDates.size());

        this.isSubmittingJobs = true;

        for (LocalDate date : missingDates) {

            this.startDayCreationThread(date);
        }

        this.isSubmittingJobs = false;

        // workaround to ensure calculation state is checked after submitting, if all jobs are already finished
        this.onCalculationProgress(-1);
    }

    /**
     * Used for {@link #waitForWorkerThreads()} to wait until {@link #continueOnMainThread()} is called.
     */
    private void enableWaiting() {
        this.mainThreadMustWait = true;
    }

    /**
     * Used by main thread to wait for worker threads. Called by {@link #startGeneration()} and {@link #startExtending(boolean)}.
     * After {@link #enableWaiting()} was called, this method waits for {@link #continueOnMainThread()} to be called.
     */
    private void waitForWorkerThreads() {

        while (this.mainThreadMustWait) {
            try {

                log.trace("waiting " + THREAD_WAIT_INTERVALL_MS + "ms");
                Thread.sleep(THREAD_WAIT_INTERVALL_MS);

            } catch (InterruptedException e) {

                log.error("Error during sleep:", e);
            }
        }

        log.trace("waitForWorkerThreads: continuing on main thread ...");

        this.doStateChange();
    }

    /** Notifies main thread to continue at {@link #waitForWorkerThreads()}. */
    private void continueOnMainThread() {
        log.trace("setting mainThreadMustWait to false");
        this.mainThreadMustWait = false;
    }

    /** This is not needed, because states are set by this class */
    @Override
    public void onStateChanged(State state) { log.trace("onStateChanged: State changed to " + state); }

    /**
     * Checks if a calculation step is finished and calls #doStateChange - argument is ignored. Must be synchronized,
     * only one thread at a time is able to update progress and possibly trigger a state change.
     */
    @Override
    public synchronized void onCalculationProgress(float percent) {

        if ( !(this.executor == null || this.executor.isShutdown()) ) { // => it's possible we're calculating

            if ( this.areAllCalculationsDone() ) {

                if (this.progressManager.getState() == State.GENERATING) {

                    // Finished generating expected range - continue on main thread
                    this.continueOnMainThread();

                } else { // if executor is set and status isn't GENERATING, status is EXTENDING_PAST or EXTENDING_FUTURE

                    this.onExtensionBundleFinished();
                }
            }
        }
    }

    private boolean areAllCalculationsDone() {

        this.log.trace("######## areAllCalculationsDone() ########");

        if (this.isSubmittingJobs) {
            this.log.trace("\tstill submitting jobs");
            return false;
        }

        for (Future<Day> future : this.activeCalculations) {
            if (!future.isDone()) {
                this.log.trace("\tactive jobs are left");
                return false;
            }
        }
        return true;
    }

    /**
     * Checks past state and decides which state should follow.
     * Should only be run on main thread - in threads use {@link #continueOnMainThread()}, which triggers
     * {@link #waitForWorkerThreads()} to continue and call this.
     */
    private void doStateChange() {

        switch (this.progressManager.getState()) {

            case GENERATING:

                this.onGenerationFinished();

                if (this.calendar.getScope() == Calendar.Scope.CYCLE) {

                    this.startExtending(true);

                } else {
                    this.onFinished();
                }

                break;

            case EXTENDING_PAST:

                this.startExtending(false);

                break;

            case EXTENDING_FUTURE:

                this.startCounting();

                break;

            case COUNTING:

                this.onFinished();

                break;
        }
    }

    /** Saves calculation results and clears list of calculation jobs. */
    private void onGenerationFinished() {

        this.log.trace("######## onGenerationFinished() ########");

        // shut down, because the progress listener of this class only works if a executor is active,
        // to prevent state changes when nothing is done
        this.executor.shutdown();

        for (Future<Day> job : this.activeCalculations) {
            try {
                final Day result = job.get();

                this.days.insert(result);
                this.newlyGenerated.add(result);
            }
            catch (Exception e) {
                this.log.error("Error calculating day", e);
            }
        }

        this.activeCalculations.clear();

        this.updateLunarPhases( this.days.allAsList() );
    }


    /** STEP 2a: Prepare extending */
    void startExtending(boolean extendPast) {

        this.log.trace("######## startExtending() ########");

        this.isExtendingPast = extendPast;

        // We generate portions of days to be able to use multi-threading, save them to this cache and after each
        // portion is completed, we check if we found an extreme.
        this.extensionCache = Collections.synchronizedSortedSet( new TreeSet<>() );

        Day firstDayToCheck, lastDayToCheck;

        // Move complete "overhead" to cache to check for extremes outside of expected range - to avoid extending if
        // it's not needed
        if (extendPast) {

            this.progressManager.notifyStateChanged(State.EXTENDING_PAST);

            firstDayToCheck = this.days.getFirst();
            lastDayToCheck = new Day( this.calendar.getRangeExpected().getStart() ); // Data isn't relevant here - create a dummy

        } else {

            this.progressManager.notifyStateChanged(State.EXTENDING_FUTURE);

            firstDayToCheck = new Day( this.calendar.getRangeExpected().getEnd() );
            lastDayToCheck = this.days.getLast();
        }

        final LinkedList<Day> availableDaysOutsideExpectedRange = this.days.of(firstDayToCheck, lastDayToCheck);
        this.extensionCache.addAll(availableDaysOutsideExpectedRange);

        if (this.isLunarExtremeInExtensionCache()) { // We already have an extreme - nothing to do

            log.trace("Lunar extreme found in extensionCache - triggering state change");
            // We're on main thread and trigger the state change directly
            this.doStateChange();

        } else {

            this.executor = this.createExecutor();

            this.enableWaiting();

            this.extend();

            // above we started worker threads so we have to wait for them here to finish
            this.waitForWorkerThreads();
        }
    }

    /** STEP 2b: Starts extension threads for for one set of days depending on NUMBER_OF_CORES  */
    private void extend() {

        this.log.trace("######## extend() ########");

        this.isSubmittingJobs = true;

        if (isExtendingPast) {

            for (int i = 1; i <= this.getMaxThreadCount(); i++) {
                this.startDayCreationThread(this.extensionCache.first().getDate().minusDays(i));
            }

        } else {

            for (int i = 1; i <= this.getMaxThreadCount(); i++) {
                this.startDayCreationThread(this.extensionCache.last().getDate().plusDays(i));
            }
        }

        this.isSubmittingJobs = false;

        // workaround to ensure calculation state is checked after submitting, if all jobs are already finished
        this.onCalculationProgress(-1);
    }

    /** Save newly extended days to extension cache and checks if further extension is needed */
    private void onExtensionBundleFinished() {

        this.log.trace("######## onExtensionBundleFinished() ########");

        this.executor.shutdown();

        for (Future<Day> job : this.activeCalculations) {
            try {
                final Day result = job.get();

                this.extensionCache.add(result);
            }
            catch (Exception e) {
                this.log.error("Error calculating day", e);
            }
        }

        this.activeCalculations.clear();

        this.updateLunarPhases( new LinkedList<>(this.extensionCache) );

        if (this.isLunarExtremeInExtensionCache()) {

            this.onExtensionFinished();

        } else {

            this.executor = this.createExecutor();
            this.extend();
        }
    }

    /**
     * Adds extension cache to newly generated and {@link CalendarData}, counts days to lunar extremes if extension is completely finished
     * and triggers state change.
     */
    private void onExtensionFinished() {

        this.log.trace("######## onExtensionFinished() ########");

        this.saveExtensionCache();

        this.continueOnMainThread();
    }

    /**
     * Saves days of extensionCache to newlyGenerated and {@link CalendarData }if not already present in {@link CalendarData}.
     */
    private void saveExtensionCache() {

        this.log.trace("######## saveExtensionCache() ########");

        for (Day day : this.extensionCache) {

            if ( !this.days.contains(day) ) {
                this.newlyGenerated.add(day);
                this.days.insert(day);
            }
        }
    }

    private boolean isLunarExtremeInExtensionCache() {

        this.log.trace("######## isLunarExtremeInExtensionCache() ########");

        for (Day day : this.extensionCache) {

            LunarPhase phase = day.getPlanetaryData().getLunarPhase();

            if (phase != null && phase.isLunarExtreme()) {
                return true;
            }
        }

        return false;
    }

    /** STEP 3: Starts counting days to lunar extremes */
    private void startCounting() {
        // and set daysSinceLast/daysUntilNext only available at CYCLE:
        this.progressManager.notifyStateChanged(State.COUNTING);
        this.countDaysToLunarExtremesInBothDirections();
    }

    /** FINAL STEP: Notify {@link ProgressManager} */
    private void onFinished() {

        log.trace("onFinished: notifying listeners");

        this.progressManager.notifyStateChanged(State.FINISHED);
    }

    /**
     * This method contains logic to start a calculation thread for a {@link Day}.
     */
    private void startDayCreationThread(final LocalDate date) {

        final CompletableFuture<Day> result = CompletableFuture.supplyAsync(() -> {

            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

            CalendarGenerator.this.log.trace(" ++++++++ Starting calculation for " + date);

            final Day day = CalendarGenerator.this.createCalculatedDay(date);

            CalendarGenerator.this.log.trace(" -------- Calculation finished for " + date);

            return day;
        }, this.executor).exceptionally(throwable -> {
            log.error( throwable.getMessage() );
            return null;
        });

        result.thenAccept( day -> CalendarGenerator.this.getProgressManager().notifyDayCreated());

        this.activeCalculations.add(result);
    }

    /**
     * Responsible for raw Generation of a {@link Day}. Used for testing purposes to override and stub
     * calculation.
     */
    Day createCalculatedDay(final LocalDate date) {
        return Day.calculateFor(this.calendar, date);
    }

    /**
     * Walks through the whole list of days and sets
     * {@link PlanetaryDayData#setDaysSinceLastMaxPhase(int)} and
     * {@link PlanetaryDayData#setDaysUntilNextMaxPhase(int)}. Doesn't skip
     * already calculated days, because there would be no significant benefit
     */
    private void countDaysToLunarExtremesInBothDirections() {

        LinkedList<Day> days = this.days.allAsList();

        int counter = PlanetaryDayData.DAY_COUNT_NOT_CALCULATED;

        ListIterator<Day> iterator = days.listIterator();

        while (iterator.hasNext()) {

            Day day = iterator.next();

            counter = incrementDayCount(counter, day);

            day.getPlanetaryData().setDaysSinceLastMaxPhase(counter);
        }

        counter = PlanetaryDayData.DAY_COUNT_NOT_CALCULATED;
        iterator = days.listIterator( days.size() );

        while (iterator.hasPrevious()) {

            Day day = iterator.previous();

            counter = incrementDayCount(counter, day);

            day.getPlanetaryData().setDaysUntilNextMaxPhase(counter);
        }

        // We're on main thread and call this directly
        this.doStateChange();
    }

    private int incrementDayCount(int counter, Day day) {
        this.log.debug(
                "Looking for extreme: " + day.getDate() + " - " + day.getPlanetaryData().getLunarPhase() +
                        "(count: " + counter + ")");

        if (counter < PlanetaryDayData.DAY_COUNT_NOT_CALCULATED) {
            counter ++;
        }

        if (day.getPlanetaryData().getLunarPhase() != null && day.getPlanetaryData().getLunarPhase().isLunarExtreme()) {
            counter = 0;
        }

        return counter;
    }

    /** Walks through all days and updates lunar phases. List will be modified! */
    private void updateLunarPhases(LinkedList<Day> days) {

        this.log.trace("######## updateLunarPhases() ########");

        if (days.size() < 3) {

            this.log.trace("      => Size < 3, returning");
            return;
        }

        Day 	previous = days.pollFirst(),
                current = days.pollFirst(),
                next = days.pollFirst();

        do {

            current.getPlanetaryData().setLunarPhase( LunarPhase.of(previous, current, next) );

            this.log.debug("      (" + previous.getDate() + ", " + current.getDate() + ", " + next.getDate() + ") => "
                    + current.getPlanetaryData().getLunarPhase());

            previous = current;
            current = next;
            next = days.pollFirst();

        } while (next != null);
    }


    /**
     * Returns a {@link DateRange} for {@link #startGeneration()} ()} to be able to calculate enough days to satisfy expected range and
     * scope.
     */
    DateRange getRangeNeededToCalculate() {

        final Calendar.Scope scope = this.calendar.getScope();
        final DateRange rangeExpected = this.calendar.getRangeExpected();

        if ( scope == Calendar.Scope.DAY ) {

            // No special requirements
            return this.calendar.getRangeExpected();

        } else { // Scope is PHASE or CYCLE

            return new DateRange( rangeExpected.getStart().minusDays(1),
                        rangeExpected.getEnd().plusDays(1) );
        }
    }

    private ThreadPoolExecutor createExecutor() {

        final BlockingQueue<Runnable> calculatorQueue = new LinkedBlockingQueue<>();
        final int threadCount = this.getMaxThreadCount();

        return new ThreadPoolExecutor(
                threadCount, threadCount,
                1, TimeUnit.SECONDS,
                calculatorQueue);
    }

    ProgressManager getProgressManager() {
        return progressManager;
    }

    /** Needed for tests. */
    Calendar getCalendar() {
        return calendar;
    }

    /** Needed for tests. */
    CalendarData getDays() {
        return days;
    }

    /** Returns all days newly generated since last call of #startGeneration. */
    LinkedList<Day> getNewlyGenerated() {

        if (!this.areAllCalculationsDone()) {
            throw new ConcurrentModificationException("Tried to fetch days before calculation finished");
        }

        return new LinkedList<>(newlyGenerated);
    }

    private int getMaxThreadCount() {

        if (this.maxThreadCount < 1) {

            this.log.info("Setting maxThreadCount to NUMBER_OF_CORES=" + NUMBER_OF_CORES);
            this.maxThreadCount = NUMBER_OF_CORES;
        }

        return maxThreadCount;
    }

    void setMaxThreadCount(int maxThreadCount) {
        this.maxThreadCount = maxThreadCount;
    }

    /** Needed for tests to be able shut down executor externally. */
    ThreadPoolExecutor getExecutor() {
        return executor;
    }
}
