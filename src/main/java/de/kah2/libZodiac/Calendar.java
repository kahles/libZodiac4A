package de.kah2.libZodiac;

import de.kah2.libZodiac.interpretation.Interpreter;
import de.kah2.libZodiac.planetary.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.bp.ZoneId;

import org.threeten.bp.LocalDate;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

/**
 * <p>This is the "main" class of libZodiac. It contains the main logic of managing calendar data like ranges, scope, etc.</p>
 * <ul>
 *     <li>Logic of generating the real data is kept in {@link CalendarGenerator}.</li>
 *     <li>Data is contained and managed in {@link CalendarData}, but public access is only through {@link Calendar}.</li>
 * </ul>
 * 
 * @see de.kah2.libZodiac.example
 * @author kahles
 */
public class Calendar implements LocationProvider {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	// This is the range the Calendar shall contain.
	private DateRange rangeExpected;

	private final CalendarData days = new CalendarData();

	private CalendarGenerator generator = new CalendarGenerator(this);

	private Class<? extends Interpreter> interpreterClass;

	/**
	 * Tells the calendar, how much data is needed / how much overhead to
	 * produce
	 * @author kahles
	 */
	public enum Scope {
		/**
		 * Calculate only for the given days - lunar phase calculation not possible for every day
		 */
		DAY,

		/**
		 * Allow lunar phase calculation through calculating one day more in
		 * each direction as needed - every requested day has its neighbors.
		 */
		PHASE,

		/**
		 * The default. Calculate to one day after the next lunar phase extremes
		 * - allows also counting days until next full/new moon
		 */
		CYCLE
	}

	private final Scope scope;

	private final ZoneId timeZoneId;
	private final Position observerPosition;

	/**
	 * Creates an empty Calendar with "CYCLE" as default for
	 * {@link Scope} and using system default {@link ZoneId}.
	 *
	 * @param observerPosition
	 *            The position of the observer needed for rise and set
	 *            calculation.
	 * @param expectedRange
	 *            the range defining start date and end date of the
	 *            {@link Calendar} instance.
	 *
	 */
	public Calendar(final Position observerPosition, final DateRange expectedRange) {
		this(observerPosition, expectedRange, Scope.CYCLE);
	}

	/**
	 * Creates an empty Calendar with "CYCLE" as default for
	 * {@link Scope}.
	 *
	 * @param observerPosition
	 *            The position of the observer needed for rise and set
	 *            calculation.
	 * @param zoneId
	 *            the time zone of the observer needed for rise and set times of
	 *            sun and moon
	 * @param expectedRange
	 *            the range defining start date and end date of the
	 *            {@link Calendar} instance.
	 *
	 */
	public Calendar(final Position observerPosition, final ZoneId zoneId, final DateRange expectedRange) {
		this(observerPosition, zoneId, expectedRange, Scope.CYCLE);
	}

	/**
	 * Creates an empty Calendar using system default {@link ZoneId}.
	 *
	 * @param observerPosition
	 *            The position of the observer needed for rise and set
	 *            calculation.
	 * @param expectedRange
	 *            the range defining start date and end date of the
	 *            {@link Calendar} instance.
	 * @param scope
	 *            Allows to set the scope manually
	 */
	public Calendar(final Position observerPosition, final DateRange expectedRange, final Scope scope) {
		this(observerPosition, ZoneId.systemDefault(), expectedRange, scope);
	}

	/**
	 * Creates an empty Calendar.
	 *
	 * @param observerPosition
	 *            The position of the observer needed for rise and set
	 *            calculation.
	 * @param zoneId
	 *            the time zone of the observer needed for rise and set times of
	 *            sun and moon
	 * @param expectedRange
	 *            the range defining start date and end date of the
	 *            {@link Calendar} instance.
	 * @param scope
	 *            Allows to set the scope manually
	 */
	public Calendar(final Position observerPosition, final ZoneId zoneId, final DateRange expectedRange,
			final Scope scope) {

		this.timeZoneId = zoneId;
		this.observerPosition = observerPosition;

		this.rangeExpected = expectedRange;
		this.scope = scope;
	}

	/**
	 * This allows e.g. loading a {@link Calendar} from some kind of database.
	 *
	 * @param storedDays
	 *            a {@link List} of {@link DayStorableDataSet}-objects of
	 *            already calculated days.
	 */
	public void importDays(final List<DayStorableDataSet> storedDays){
		this.generator.importDays(storedDays);
	}

	/**
	 * <p>Initializes the calendar and calculates the (missing) data between and
	 * around {@link Calendar#getRangeExpected()} regarding the chosen
	 * {@link Calendar.Scope}.</p>
	 * <p>NOTE: After importing data or changing the expected range, there might be gaps, so there wouldn't be valid data after calling
	 * generate(). Use {@link Calendar#fixRangeExpectedToIncludeExistingDays()} and/or {@link Calendar#removeOverhead(boolean)} to fix
	 * existing data.</p>
	 * <p>Set a {@link ProgressListener} via {@link #addProgressListener(ProgressListener)} to gent notified when calculation is finished.
	 * If you try fetching data before calculation is finished, you will get a {@link ConcurrentModificationException}.</p>
	 */
	public void startGeneration() {

		this.generator.startGeneration();
	}

	/**
	 * Registers a {@link ProgressListener}.
	 */
	public void addProgressListener(final ProgressListener progressListener) {
		generator.getProgressManager().addProgressListener(progressListener);
	}

	/**
	 * Unregisters a {@link ProgressListener}
	 */
	public void removeProgressListener(final ProgressListener progressListener) {
		generator.getProgressManager().removeProgressListener(progressListener);
	}
	/**
	 * Sets the expectedRange to include already generated/loaded days.
	 */
	public void fixRangeExpectedToIncludeExistingDays() {
		LocalDate expectedStart = this.getRangeExpected().getStart();
		LocalDate expectedEnd = this.getRangeExpected().getEnd();

		final LinkedList<Day> allDays = this.getAllDays();

		if (!this.days.isEmpty()) {
			if (allDays.getFirst().getDate().isBefore(expectedStart)) {
				this.log.debug("Fixing start of expected range: " + allDays.getFirst().getDate() + " => " + expectedStart);
				expectedStart = allDays.getFirst().getDate();
			}

			if (allDays.getLast().getDate().isAfter(expectedEnd)) {
				this.log.debug("Fixing end of expected range: " + allDays.getLast().getDate() + " => " + expectedEnd);
				expectedEnd = allDays.getLast().getDate();
			}

			this.setRangeExpected(new DateRange(expectedStart, expectedEnd));
		}
	}

	/**
	 * Removes days which are outside of {@link #getRangeExpected()}.
	 * <ul>
	 *     <li>DAY: keeps exactly expected range</li>
	 *     <li>PHASE: keeps one day more in each direction of expected range</li>
	 *     <li>CYCLE: keeps all days until next lunar extremes</li>
	 * </ul>
	 *
	 * @param alsoDeleteFutureDays
	 *            if set to false, days after expectedRange are kept. Gaps may
	 *            remain - call {@link #fixRangeExpectedToIncludeExistingDays()} afterwards.
	 * @return The deleted days to be able to also delete them from storage or
	 *         null if nothing was removed.
	 */
	public LinkedList<Day> removeOverhead(final boolean alsoDeleteFutureDays) {

		DateRange rangeToKeep;

		switch (this.scope) {

			case PHASE:
				rangeToKeep = new DateRange( this.getRangeExpected().getStart().minusDays(1), this.getRangeExpected().getEnd().plusDays(1) );
				break;

			case CYCLE:
				rangeToKeep = this.getRangeNeededToKeepCycle(alsoDeleteFutureDays);
				break;

			default: // DAY:
				rangeToKeep = this.getRangeExpected();
				break;
		}

		LinkedList<Day> removed = this.days.removeBefore( rangeToKeep.getStart() );

		if (alsoDeleteFutureDays) {
			removed.addAll( this.days.removeAfter( rangeToKeep.getEnd() ) );
		}

		return removed;
	}

	/**
	 * Needed for {@link #removeOverhead(boolean)} in case scope is set to CYCLE.
	 */
	private DateRange getRangeNeededToKeepCycle(boolean alsoCheckFuture) {

		if ( this.days.isEmpty() ) {
			// we could return anything - there's nothing to delete
			return this.getRangeExpected();
		}

		final TreeSet<Day> days = this.days.allAsTreeSet();

		final LocalDate start = findNextLunarExtreme(days, this.getRangeExpected().getStart(), false);
		final LocalDate end;

		if (alsoCheckFuture) {
			end = findNextLunarExtreme(days, this.getRangeExpected().getEnd(), true);
		} else {
			end = this.getRangeExpected().getEnd().plusDays(1);
		}

		return new DateRange(start, end);
	}

	private LocalDate findNextLunarExtreme(TreeSet<Day> days, LocalDate start, boolean isDirectionForward) {

		// Get first
		Day current = days.ceiling( new Day(start) );

		if ( current == null || !current.getDate().isEqual(start) ) {
			// Requested days isn't contained - we're done
			return start;
		}

		// When we reach end of days :o) we need a backup to still have the last date
		Day backup;

		do {

			backup = current;

			if (isDirectionForward) {
				current = days.higher(current);
			} else {
				current = days.lower(current);
			}

		} while ( current != null
				&& current.getPlanetaryData().getLunarPhase() != null
				&& !current.getPlanetaryData().getLunarPhase().isLunarExtreme() );

		if (current == null) {
			current = backup;
		}

		if ( current.getPlanetaryData().getLunarPhase() == null ) {
			// => We didn't find lunar extreme and are at end
			return current.getDate();
		} else {
			// current is lunar extreme - we need one day further
			if (isDirectionForward) {
				return current.getDate().plusDays(1);
			} else {
				return current.getDate().minusDays(1);
			}
		}
	}

	@Override
	public ZoneId getTimeZoneId() {
		return this.timeZoneId;
	}

	@Override
	public Position getObserverPosition() {
		return this.observerPosition;
	}

	/**
	 * Gets the range which the {@link Calendar} shall contain.
	 */
	public DateRange getRangeExpected() {
		return this.rangeExpected;
	}

	/**
	 * Sets the range which the {@link Calendar} shall contain.
	 */
	public void setRangeExpected(final DateRange range) {
		this.rangeExpected = range;
	}

	/**
	 * Proxy method for {@link CalendarData#get(LocalDate)}.
	 * @return The requested {@link Day} or null, if date is out of
	 *         {@link Calendar} range.
	 */
	public Day get(LocalDate date) {
		return this.days.get(date);
	}

	/**
	 * Returns all days contained. Consistency isn't guaranteed - if no days are available, an empty list is returned.
	 * @throws ConcurrentModificationException If calculation isn't finished.
	 */
	public LinkedList<Day> getAllDays() {

		this.throwExceptionIfInProgress();

		return this.days.allAsList();
	}

	/**
	 * Returns all days if Scope is set to DAY, or all days containing lunar phase if other scope is selected.
	 * @return All days satisfying scope requirements or null, if data isn't available or contains gaps.
	 * @throws ConcurrentModificationException If calculation isn't finished.
	 */
	public LinkedList<Day> getValidDays() {

		this.throwExceptionIfInProgress();

		if (this.days.isEmpty()) {
			return new LinkedList<>();
		}

		if ( !this.days.isComplete() ) {
			// Gaps exist ...
			return null;
		}

		if (this.scope == Scope.DAY) {

			return this.days.allAsList();

		} else {
			final DateRange allDaysRange = this.days.getRangeOfAllContainedDays();
			final DateRange rangeNeededForLunarPhases =
					new DateRange( allDaysRange.getStart().plusDays(1), allDaysRange.getEnd().minusDays(1) );

			return this.days.of(
						new Day(rangeNeededForLunarPhases.getStart()),
						new Day(rangeNeededForLunarPhases.getEnd()));
		}
	}

	/** Checks if calendar contains all days of expected range. */
	public boolean isComplete() {
		return this.days.isComplete( this.getRangeExpected() );
	}

	private void throwExceptionIfInProgress() {

		final ProgressListener.State state = this.generator.getProgressManager().getState();

		if ( state == ProgressListener.State.IMPORTING
				|| state == ProgressListener.State.GENERATING
				|| state == ProgressListener.State.EXTENDING_PAST
				|| state == ProgressListener.State.EXTENDING_FUTURE
				|| state == ProgressListener.State.COUNTING ) {
			throw new ConcurrentModificationException("Tried to fetch days during operation: State is " + state);
		}
	}

	/**
	 * @return Days that were created by last generation process to be able to add them to a database.
	 * @throws ConcurrentModificationException If calculation isn't finished.
	 */
	public LinkedList<Day> getNewlyGenerated() {
		return this.generator.getNewlyGenerated();
	}

	/** Returns the {@link Scope} of this instance. */
	public Scope getScope() {
		return scope;
	}

	/** Needed for tests to allow stubbing the generator. */
	void setGenerator(CalendarGenerator generator) {
		this.generator = generator;
	}

	/** Needed for tests to allow testing the generator. */
	CalendarGenerator getGenerator() {
		return generator;
	}

	/** Needed for CalendarStub. */
	CalendarData getDays() {
		return days;
	}

	/** Simple getter for actual selected interpretation. */
	public Class<? extends Interpreter> getInterpreterClass() {
		return interpreterClass;
	}

	/**
	 * This method is intended to activate an interpretation. You should set an interpreter after generating possibly missing data - if
	 * data of expected range is missing, a {@link RuntimeException} will be thrown.
	 * @param interpreterClass a class extending {@link Interpreter} or null to unset
	 */
	public void setInterpreterClass(Class<? extends Interpreter> interpreterClass) {

		this.interpreterClass = interpreterClass;

		this.updateInterpreters();
	}

	void updateInterpreters() {

		final List<Day> days = this.getValidDays();

		if ( days != null && this.interpreterClass != null) {

			for (Day day : days) {
				day.setInterpreterClass(this.interpreterClass);

			}
		}
	}
}
