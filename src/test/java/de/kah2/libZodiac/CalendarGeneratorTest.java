package de.kah2.libZodiac;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import static de.kah2.libZodiac.TestConstantsAndHelpers.*;
import static org.junit.Assert.*;

public class CalendarGeneratorTest {

    static {
        // Uncomment to have detailed output
        // TestConstantsAndHelpers.enableLogging("trace");
    }

    @Test
    public void testGenerateScopeDay() {
        final DateRange rangeExpected = new DateRange(SOME_DATE, SOME_DATE.plusDays(2));
        final CalendarGenerator generator = new CalendarStub(rangeExpected, Calendar.Scope.DAY).getGenerator();

        TestConstantsAndHelpers.generateAndWaitFor(generator);

        final LinkedList<Day> generated = generator.getNewlyGenerated();

        for (final LocalDate date : rangeExpected) {
            assertTrue("Day for date " + date + " should be generated", this.daysContainDate(generated, date));
        }
        assertFalse("date before range shouldn't be contained",
                this.daysContainDate(generated, rangeExpected.getStart().minusDays(1)));
        assertFalse("date after range shouldn't be contained",
                this.daysContainDate(generated, rangeExpected.getEnd().plusDays(1)));

        this.checkListContainsValidCalendarRange(generated);
    }

    private boolean daysContainDate(final List<Day> days, final LocalDate date) {

        for (final Day day : days) {
            if (day.getDate().isEqual(date)) {
                return true;
            }
        }
        return false;
    }

    @Test
    public void testGenerateScopePhase() {

        final DateRange rangeExpected = new DateRange(SOME_DATE, SOME_DATE);
        final CalendarGenerator generator = new CalendarStub(rangeExpected, Calendar.Scope.PHASE).getGenerator();

        TestConstantsAndHelpers.generateAndWaitFor(generator);
        final LinkedList<Day> generated = generator.getNewlyGenerated();

        assertTrue("Calendar data should start one day before expected range",
                generated.getFirst().getDate().isEqual( SOME_DATE.minusDays(1) ) );

        assertTrue("Calendar data should end one day after expected range",
                generated.getLast().getDate().isEqual( SOME_DATE.plusDays(1) ) );

        this.checkListContainsValidCalendarRange(generated);
    }

    @Test
    public void testStateChanges() {

        final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName() + "#testStateChanges>");

        log.debug("Test if state changes work, if nothing has to be generated at Scope DAY");

        DateRange rangeExpected = new DateRange(SOME_DATE, SOME_DATE);
        CalendarGenerator generator = new CalendarStub(rangeExpected, Calendar.Scope.DAY).getGenerator();

        final LastStateProgressListener listener = new LastStateProgressListener();
        generator.getProgressManager().addProgressListener(listener);

        assertNull("Initially state should be null", listener.getLastState());

        generator.importDays( CalendarGeneratorStub.stubDayStorableDataSets(rangeExpected) );

        assertEquals("After import:", ProgressListener.State.IMPORT_FINISHED, listener.getLastState());

        final int maxWaitMs = 3000;

        assertTrue("Should enter state FINISHED in less than " + maxWaitMs + "ms",
                TestConstantsAndHelpers.generateAndWaitFor(generator, maxWaitMs) );


        log.debug("Test if state changes work, if nothing has to be extended at Scope CYCLE");

        rangeExpected = new DateRange(SOME_DATES_LAST_EXTREME, SOME_DATES_NEXT_EXTREME);

        generator = new CalendarStub(rangeExpected, Calendar.Scope.CYCLE).getGenerator();

        generator.getProgressManager().addProgressListener(listener);

        generator.importDays( CalendarGeneratorStub.stubDayStorableDataSets(
                new DateRange( SOME_DATES_LAST_EXTREME.minusDays(1), SOME_DATES_NEXT_EXTREME.plusDays(1) )
        ) );

        assertEquals("After import:", ProgressListener.State.IMPORT_FINISHED, listener.getLastState());

        assertTrue("Should enter state FINISHED in less than " + maxWaitMs + "ms",
                TestConstantsAndHelpers.generateAndWaitFor(generator, maxWaitMs) );
    }

    @Test
    public void testExtendRange() {

        // Create small range and extend

        final CalendarGenerator generator = new CalendarStub(
                new DateRange( SOME_DATE, SOME_DATE.plusDays(2) ), Calendar.Scope.CYCLE).getGenerator();

        generator.importDays(
                CalendarGeneratorStub.stubDayStorableDataSets( generator.getCalendar().getRangeExpected() ) );

        this.extendAndWait(generator);

        final LinkedList<Day> extended = generator.getNewlyGenerated();

        assertFalse( "First generated should not be after last extreme.",
                extended.getFirst().getDate().isAfter(SOME_DATES_LAST_EXTREME) );
        assertFalse( "Last generated should not be before next extreme.",
                extended.getLast().getDate().isBefore(SOME_DATES_NEXT_EXTREME) );

        this.checkListContainsValidCalendarRange(generator.getDays().allAsList());
    }

    @Test
    public void testExtendRangeOnlyExtendsIfNeeded() {

        // Generate full cycle - test if it's recognized and nothing is extended
        DateRange expectedRange = new DateRange(SOME_DATES_LAST_EXTREME, SOME_DATES_NEXT_EXTREME);
        DateRange rangeToStub = new DateRange(SOME_DATES_LAST_EXTREME.minusDays(1), SOME_DATES_NEXT_EXTREME.plusDays(1));

        CalendarGenerator generator = new CalendarStub( expectedRange, Calendar.Scope.CYCLE ).getGenerator();

        generator.importDays(
                CalendarGeneratorStub.stubDayStorableDataSets( rangeToStub ) );

        this.extendAndWait(generator);

        assertEquals(
                "Nothing should be extended when we already have extremes at start and end of expected range",
                0, generator.getNewlyGenerated().size() );

        // Generate small range, extend to cycle with overhead from using eight threads
        // Test if nothing "unwanted" is extended when extending again

        generator = new CalendarStub(
                new DateRange( SOME_DATE, SOME_DATE.plusDays(2)), Calendar.Scope.CYCLE ).getGenerator();

        final int threadCount = 8;

        generator.setMaxThreadCount(threadCount);

        generator.importDays(
                CalendarGeneratorStub.stubDayStorableDataSets( generator.getCalendar().getRangeExpected() ) );

        this.extendAndWait(generator);

        final LinkedList<Day> lastGenerated = generator.getNewlyGenerated();

        assertEquals("Extending should generate two sets of days", // (1*threadCount in each direction)
                2*threadCount, lastGenerated.size() );

        this.extendAndWait(generator);

        final LinkedList<Day> after2ndExtension = generator.getNewlyGenerated();
        assertEquals("Extending a second time shouldn't generate anything",
                lastGenerated.size(), after2ndExtension.size() );
        assertTrue("Newly generated should start at same day as last time of extension",
                lastGenerated.getFirst().getDate().isEqual( after2ndExtension.getFirst().getDate() ) );
        assertTrue("Newly generated should end at same day as last time of extension",
                lastGenerated.getLast().getDate().isEqual( after2ndExtension.getLast().getDate() ) );
    }

    private void extendAndWait(CalendarGenerator generator) {

        final LastStateProgressListener listener = new LastStateProgressListener();

        generator.getProgressManager().addProgressListener(listener);

        generator.startExtending(true);

        while (listener.getLastState() != ProgressListener.State.FINISHED) {
            try {
                Thread.sleep(100);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        generator.getProgressManager().removeProgressListener(listener);
    }

    /** Checks if days have no gaps between them. */
    private void checkListContainsValidCalendarRange(final List<Day> items) {
        Day last = null;

        boolean isValid = true;

        for (final Day actual : items) {
            if (!(last == null || last.getDate().plusDays(1).isEqual(actual.getDate()))) {
                isValid = false;
            }
            last = actual;
        }
        assertTrue("Generated days should be sorted and don't contain gaps", isValid);
    }

    @Test
    public void testGenerateAfterImportRecognizesCycle() {
        final DateRange range = new DateRange(SOME_DATE, SOME_DATE);

        final LinkedList<DayStorableDataSet> daysToImport = CalendarGeneratorStub.stubDayStorableDataSets(
                new DateRange(SOME_DATES_LAST_EXTREME.minusDays(1), SOME_DATES_NEXT_EXTREME.plusDays(1)) );

        CalendarGenerator generator = new CalendarStub(range, Calendar.Scope.CYCLE).getGenerator();
        generator.importDays(daysToImport);

        TestConstantsAndHelpers.generateAndWaitFor(generator);

        final int generatedCount = generator.getNewlyGenerated().size();

        assertEquals("Nothing should be generated when cycle around expectedRange is loaded", 0,
                generatedCount);
    }

    @Test
    public void testUpdateLunarPhases() {
        final DateRange range = new DateRange( SOME_DATE, SOME_DATE.plusDays(3) );
        CalendarGenerator generator = new CalendarStub(range, Calendar.Scope.CYCLE).getGenerator();

        // Test if lunar phases are set after generate
        TestConstantsAndHelpers.generateAndWaitFor(generator);

        LinkedList<Day> days = getValidDays(generator);

        this.testAllDaysHaveLunarPhase(days);

        // Test if lunar phases are set after import
        List<DayStorableDataSet> storedDays = new LinkedList<>();

        for (Day day: days) {
            storedDays.add(new DayStorableDataSet(day));
        }

        generator = new CalendarStub(range, Calendar.Scope.CYCLE).getGenerator();
        generator.importDays(storedDays);

        days = getValidDays(generator);

        testAllDaysHaveLunarPhase(days);
    }

    private LinkedList<Day> getValidDays(CalendarGenerator generator) {

        final LinkedList<Day> days = generator.getDays().allAsList();

        // All days except first and last should be valid / have a lunar phase
        days.removeFirst();
        days.removeLast();

        return days;
    }

    private void testAllDaysHaveLunarPhase(LinkedList<Day> days) {
        for (Day day : days) {
            assertNotNull( "Day " + day.getDate() + " should have lunar phase.", day.getPlanetaryData().getLunarPhase() );
        }
    }

    @Test
    public void testCountDaysBetweenLunarVisibilityExtremes() {
        final DateRange range = new DateRange(SOME_DATE, SOME_DATE.plusDays(2));
        final CalendarGenerator generator = new CalendarStub(range, Calendar.Scope.CYCLE).getGenerator();

        TestConstantsAndHelpers.generateAndWaitFor(generator);

        LinkedList<Day> days = generator.getNewlyGenerated();

        // testing "since last" ...

        Day actualDay = days.pollFirst();

        while ( actualDay.getPlanetaryData().getLunarPhase() == null
                || !actualDay.getPlanetaryData().getLunarPhase().isLunarExtreme() ) {

            assertEquals(actualDay.getDate() + " shouldn't have daysSinceLast", -1,
                    actualDay.getPlanetaryData().getDaysSinceLastMaxPhase());

            actualDay = days.pollFirst();
        }

        int count = 0;

        while (actualDay != null) {
            assertEquals("daysSinceLast of " + actualDay.getDate() + " should be " + count,
                    count, actualDay.getPlanetaryData().getDaysSinceLastMaxPhase());

            actualDay = days.pollFirst();

            if (actualDay != null && actualDay.getDate().isEqual(SOME_DATES_NEXT_EXTREME)) {
                count = 0;
            } else {
                count++;
            }
        }

        // testing "until next" ...

        days = generator.getNewlyGenerated();

        actualDay = days.pollLast();

        while ( actualDay.getPlanetaryData().getLunarPhase() == null
                || !actualDay.getPlanetaryData().getLunarPhase().isLunarExtreme() ) {

            assertEquals(actualDay.getDate() + " shouldn't have daysUntilNext", -1,
                    actualDay.getPlanetaryData().getDaysUntilNextMaxPhase());

            actualDay = days.pollLast();
        }

        count = 0;

        while (actualDay != null) {
            assertEquals("daysUntilNext of " + actualDay.getDate() + " should be " + count,
                    count, actualDay.getPlanetaryData().getDaysUntilNextMaxPhase());

            actualDay = days.pollLast();

            if (actualDay != null && actualDay.getDate().isEqual(SOME_DATES_LAST_EXTREME)) {
                count = 0;
            } else {
                count++;
            }
        }
    }

    @Test
    public void testGetRangeNeededToCalculateForScopeDay() {

        final DateRange expectedRange = new DateRange(SOME_DATE, SOME_DATE.plusDays(3));
        final CalendarGenerator generator = new CalendarStub(expectedRange, Calendar.Scope.DAY).getGenerator();

        assertTrue("Needed range should match expected range at Scope DAY when calendar is empty",
                expectedRange.isEqual( generator.getRangeNeededToCalculate() ) );

        generator.importDays( CalendarGeneratorStub.stubDayStorableDataSets(expectedRange) );

        assertTrue("Needed range should match expected range at Scope DAY when calendar is already calculated",
                expectedRange.isEqual( generator.getRangeNeededToCalculate() ) );
    }

    @Test
    public void testGetRangeNeededToCalculateForScopePhase() {
        this.testGetRangeNeededRespectsPhase(Calendar.Scope.PHASE);
    }

    @Test
    public void testGetRangeNeededToCalculateForScopeCycle() {
        this.testGetRangeNeededRespectsPhase(Calendar.Scope.CYCLE);
    }

    private void testGetRangeNeededRespectsPhase(final Calendar.Scope scope) {
        final DateRange expectedRange = new DateRange(SOME_DATE, SOME_DATE.plusDays(3));
        final CalendarGenerator generator = new CalendarStub(expectedRange, scope).getGenerator();

        DateRange rangeShouldBe = new DateRange( expectedRange.getStart().minusDays(1), expectedRange.getEnd().plusDays(1) );

        assertTrue("Needed range should start one day before and end one day after expected range at "
                        + scope + " when calendar is empty",
                rangeShouldBe.isEqual( generator.getRangeNeededToCalculate() ) );

        generator.importDays( CalendarGeneratorStub.stubDayStorableDataSets(expectedRange) );

        assertTrue("Needed range should start one day before and end one day after expected range at Scope "
                        + scope + " when calendar is already calculated",
                rangeShouldBe.isEqual( generator.getRangeNeededToCalculate() ) );
    }

//	private void print(Day day) {
//		LocalDate date = day.getDate();
//	 	int sinceLast = day.getPlanetaryData().getDaysSinceLastMaxPhase();
//	 	int tillNext = day.getPlanetaryData().getDaysUntilNextMaxPhase();
//	 	System.out.println("++++++++++++++++++++++++++++++ " + date + " --> "
//	 		+ "since last: " + sinceLast + ", till next: " + tillNext
//	 		+ "\t" + day.getPlanetaryData().getLunarPhase());
//	 }
}
