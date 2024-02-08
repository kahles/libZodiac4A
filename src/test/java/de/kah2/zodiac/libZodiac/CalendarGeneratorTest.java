package de.kah2.zodiac.libZodiac;

import de.kah2.zodiac.libZodiac.planetary.PlanetaryDayData;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CalendarGeneratorTest {

    static {
//        Uncomment to have detailed output:
//        TestConstantsAndHelpers.enableLogging("trace");
    }

    @Test
    public void testGenerateScopeDay() {
        final DateRange rangeExpected = new DateRange( TestConstantsAndHelpers.SOME_DATE, TestConstantsAndHelpers.SOME_DATE.plusDays(2));
        final CalendarGenerator generator = new CalendarStub(rangeExpected, Calendar.Scope.DAY).getGenerator();

        TestConstantsAndHelpers.generateAndWaitFor(generator);

        final LinkedList<Day> generated = generator.getNewlyGenerated();

        for (final LocalDate date : rangeExpected) {
            assertTrue(this.daysContainDate(generated, date), "Day for date " + date + " should be generated");
        }
        assertFalse(this.daysContainDate(generated, rangeExpected.getStart().minusDays(1)),
                "date before range shouldn't be contained");
        assertFalse(this.daysContainDate(generated, rangeExpected.getEnd().plusDays(1)),
                "date after range shouldn't be contained");

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

        final DateRange rangeExpected = new DateRange( TestConstantsAndHelpers.SOME_DATE, TestConstantsAndHelpers.SOME_DATE);
        final CalendarGenerator generator = new CalendarStub(rangeExpected, Calendar.Scope.PHASE).getGenerator();

        TestConstantsAndHelpers.generateAndWaitFor(generator);
        final LinkedList<Day> generated = generator.getNewlyGenerated();

        assertTrue(generated.getFirst().getDate().isEqual( TestConstantsAndHelpers.SOME_DATE.minusDays(1) ),
                "Calendar data should start one day before expected range" );

        assertTrue(generated.getLast().getDate().isEqual( TestConstantsAndHelpers.SOME_DATE.plusDays(1) ),
                "Calendar data should end one day after expected range" );

        this.checkListContainsValidCalendarRange(generated);
    }

    @Test
    public void testStateChanges() {

        final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName() + "#testStateChanges>");

        log.debug("Test if state changes work, if nothing has to be generated at Scope DAY");

        DateRange rangeExpected = new DateRange( TestConstantsAndHelpers.SOME_DATE, TestConstantsAndHelpers.SOME_DATE);
        CalendarGenerator generator = new CalendarStub(rangeExpected, Calendar.Scope.DAY).getGenerator();

        final TestConstantsAndHelpers.LastStateProgressListener listener = new TestConstantsAndHelpers.LastStateProgressListener();
        generator.getProgressManager().addProgressListener(listener);

        assertNull(listener.getLastState(), "Initially state should be null");

        generator.importDays( CalendarGeneratorStub.stubDayStorableDataSets(rangeExpected) );

        assertEquals(ProgressListener.State.IMPORT_FINISHED, listener.getLastState(), "After import:");

        final int maxWaitMs = 3000;

        assertTrue(TestConstantsAndHelpers.generateAndWaitFor(generator, maxWaitMs),
                "Should enter state FINISHED in less than " + maxWaitMs + "ms" );


        log.debug("Test if state changes work, if nothing has to be extended at Scope CYCLE");

        rangeExpected = new DateRange( TestConstantsAndHelpers.SOME_DATES_LAST_EXTREME, TestConstantsAndHelpers.SOME_DATES_NEXT_EXTREME);

        generator = new CalendarStub(rangeExpected, Calendar.Scope.CYCLE).getGenerator();

        generator.getProgressManager().addProgressListener(listener);

        generator.importDays( CalendarGeneratorStub.stubDayStorableDataSets(
                new DateRange( TestConstantsAndHelpers.SOME_DATES_LAST_EXTREME.minusDays(1), TestConstantsAndHelpers.SOME_DATES_NEXT_EXTREME.plusDays(1) )
        ) );

        assertEquals(ProgressListener.State.IMPORT_FINISHED, listener.getLastState(), "After import:");

        assertTrue(TestConstantsAndHelpers.generateAndWaitFor(generator, maxWaitMs),
                "Should enter state FINISHED in less than " + maxWaitMs + "ms" );
    }

    @Test
    public void testExtendRange() {

        // Create small range and extend

        final CalendarGenerator generator = new CalendarStub(
                new DateRange( TestConstantsAndHelpers.SOME_DATE, TestConstantsAndHelpers.SOME_DATE.plusDays(2) ), Calendar.Scope.CYCLE).getGenerator();

        generator.importDays(
                CalendarGeneratorStub.stubDayStorableDataSets( generator.getCalendar().getRangeExpected() ) );

        this.extendAndWait(generator);

        final LinkedList<Day> extended = generator.getNewlyGenerated();

        assertFalse( extended.getFirst().getDate().isAfter( TestConstantsAndHelpers.SOME_DATES_LAST_EXTREME),
                "First generated should not be after last extreme." );
        assertFalse( extended.getLast().getDate().isBefore( TestConstantsAndHelpers.SOME_DATES_NEXT_EXTREME),
                "Last generated should not be before next extreme." );

        this.checkListContainsValidCalendarRange(generator.getDays().allAsList());
    }

    @Test
    public void testExtendRangeOnlyExtendsIfNeeded() {

        // Generate full cycle - test if it's recognized and nothing is extended
        DateRange expectedRange = new DateRange( TestConstantsAndHelpers.SOME_DATES_LAST_EXTREME, TestConstantsAndHelpers.SOME_DATES_NEXT_EXTREME);
        DateRange rangeToStub = new DateRange( TestConstantsAndHelpers.SOME_DATES_LAST_EXTREME.minusDays(1), TestConstantsAndHelpers.SOME_DATES_NEXT_EXTREME.plusDays(1));

        CalendarGenerator generator = new CalendarStub( expectedRange, Calendar.Scope.CYCLE ).getGenerator();

        generator.importDays(
                CalendarGeneratorStub.stubDayStorableDataSets( rangeToStub ) );

        this.extendAndWait(generator);

        assertEquals(
                0, generator.getNewlyGenerated().size(), "Nothing should be extended when we already have extremes at start and end of expected range" );

        // Generate small range, extend to cycle with overhead from using eight threads
        // Test if nothing "unwanted" is extended when extending again

        generator = new CalendarStub(
                new DateRange( TestConstantsAndHelpers.SOME_DATE, TestConstantsAndHelpers.SOME_DATE.plusDays(2)), Calendar.Scope.CYCLE ).getGenerator();

        final int threadCount = 8;

        generator.setMaxThreadCount(threadCount);

        generator.importDays(
                CalendarGeneratorStub.stubDayStorableDataSets( generator.getCalendar().getRangeExpected() ) );

        this.extendAndWait(generator);

        final LinkedList<Day> lastGenerated = generator.getNewlyGenerated();

        assertEquals(2*threadCount, lastGenerated.size(), "Extending should generate two sets of days" );

        this.extendAndWait(generator);

        final LinkedList<Day> after2ndExtension = generator.getNewlyGenerated();
        assertEquals(lastGenerated.size(), after2ndExtension.size(), "Extending a second time shouldn't generate anything" );
        assertTrue(lastGenerated.getFirst().getDate().isEqual( after2ndExtension.getFirst().getDate() ),
                "Newly generated should start at same day as last time of extension" );
        assertTrue(lastGenerated.getLast().getDate().isEqual( after2ndExtension.getLast().getDate() ),
                "Newly generated should end at same day as last time of extension" );
    }

    private void extendAndWait(CalendarGenerator generator) {

        final TestConstantsAndHelpers.LastStateProgressListener listener = new TestConstantsAndHelpers.LastStateProgressListener();

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
        assertTrue(isValid, "Generated days should be sorted and don't contain gaps");
    }

    @Test
    public void testGenerateAfterImportRecognizesCycle() {

        final DateRange range = new DateRange( TestConstantsAndHelpers.SOME_DATE, TestConstantsAndHelpers.SOME_DATE);

        final LinkedList<DayStorableDataSet> daysToImport = CalendarGeneratorStub.stubDayStorableDataSets(
                new DateRange( TestConstantsAndHelpers.SOME_DATES_LAST_EXTREME.minusDays(1), TestConstantsAndHelpers.SOME_DATES_NEXT_EXTREME.plusDays(1)) );

        CalendarGenerator generator = new CalendarStub(range, Calendar.Scope.CYCLE).getGenerator();
        generator.importDays(daysToImport);

        TestConstantsAndHelpers.generateAndWaitFor(generator);

        final int generatedCount = generator.getNewlyGenerated().size();

        assertEquals(0,
                generatedCount,
                "Nothing should be generated when cycle around expectedRange is loaded");
    }

    @Test
    public void testUpdateLunarPhases() {
        final DateRange range = new DateRange( TestConstantsAndHelpers.SOME_DATE, TestConstantsAndHelpers.SOME_DATE.plusDays(3) );
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
            assertNotNull( day.getPlanetaryData().getLunarPhase(), "Day " + day.getDate() + " should have lunar phase." );
        }
    }

    @Test
    public void testCountDaysBetweenLunarVisibilityExtremes() {
        final DateRange range = new DateRange( TestConstantsAndHelpers.SOME_DATE, TestConstantsAndHelpers.SOME_DATE.plusDays(2));
        final CalendarGenerator generator = new CalendarStub(range, Calendar.Scope.CYCLE).getGenerator();

        TestConstantsAndHelpers.generateAndWaitFor(generator);

        LinkedList<Day> days = generator.getNewlyGenerated();

        // testing "since last" ...

        Day actualDay = days.pollFirst();

        while ( actualDay.getPlanetaryData().getLunarPhase() == null
                || !actualDay.getPlanetaryData().getLunarPhase().isLunarExtreme() ) {

            assertEquals(PlanetaryDayData.DAY_COUNT_NOT_CALCULATED,
                    actualDay.getPlanetaryData().getDaysSinceLastMaxPhase(),
                    actualDay.getDate() + " shouldn't have daysSinceLast");

            actualDay = days.pollFirst();
        }

        int count = 0;

        while (actualDay != null) {
            assertEquals(count, actualDay.getPlanetaryData().getDaysSinceLastMaxPhase(), "daysSinceLast of " + actualDay.getDate() + " should be " + count);

            actualDay = days.pollFirst();

            if (actualDay != null && actualDay.getDate().isEqual( TestConstantsAndHelpers.SOME_DATES_NEXT_EXTREME)) {
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

            assertEquals(PlanetaryDayData.DAY_COUNT_NOT_CALCULATED,
                    actualDay.getPlanetaryData().getDaysUntilNextMaxPhase(),
                    actualDay.getDate() + " shouldn't have daysUntilNext");

            actualDay = days.pollLast();
        }

        count = 0;

        while (actualDay != null) {
            assertEquals(count, actualDay.getPlanetaryData().getDaysUntilNextMaxPhase(), "daysUntilNext of " + actualDay.getDate() + " should be " + count);

            actualDay = days.pollLast();

            if (actualDay != null && actualDay.getDate().isEqual( TestConstantsAndHelpers.SOME_DATES_LAST_EXTREME)) {
                count = 0;
            } else {
                count++;
            }
        }
    }

    @Test
    public void testGetRangeNeededToCalculateForScopeDay() {

        final DateRange expectedRange = new DateRange( TestConstantsAndHelpers.SOME_DATE, TestConstantsAndHelpers.SOME_DATE.plusDays(3));
        final CalendarGenerator generator = new CalendarStub(expectedRange, Calendar.Scope.DAY).getGenerator();

        assertTrue(expectedRange.isEqual( generator.getRangeNeededToCalculate() ),
                "Needed range should match expected range at Scope DAY when calendar is empty" );

        generator.importDays( CalendarGeneratorStub.stubDayStorableDataSets(expectedRange) );

        assertTrue(expectedRange.isEqual( generator.getRangeNeededToCalculate() ),
                "Needed range should match expected range at Scope DAY when calendar is already calculated" );
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
        final DateRange expectedRange = new DateRange( TestConstantsAndHelpers.SOME_DATE, TestConstantsAndHelpers.SOME_DATE.plusDays(3));
        final CalendarGenerator generator = new CalendarStub(expectedRange, scope).getGenerator();

        DateRange rangeShouldBe = new DateRange( expectedRange.getStart().minusDays(1), expectedRange.getEnd().plusDays(1) );

        assertTrue(rangeShouldBe.isEqual( generator.getRangeNeededToCalculate() ),
                "Needed range should start one day before and end one day after expected range at "
                        + scope + " when calendar is empty" );

        generator.importDays( CalendarGeneratorStub.stubDayStorableDataSets(expectedRange) );

        assertTrue(rangeShouldBe.isEqual( generator.getRangeNeededToCalculate() ),
                "Needed range should start one day before and end one day after expected range at Scope "
                        + scope + " when calendar is already calculated" );
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
