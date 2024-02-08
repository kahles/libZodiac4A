package de.kah2.zodiac.libZodiac;

import de.kah2.zodiac.libZodiac.Calendar.Scope;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CalendarTest {

    static {
    	// Uncomment to have detailed output
        // TestConstantsAndHelpers.enableLogging("trace");
    }

	@Test
	public void testGetValidDaysReturnsRightRangeForScopeDay() {

		final DateRange range = new DateRange( TestConstantsAndHelpers.SOME_DATE, TestConstantsAndHelpers.SOME_DATE.plusDays(2) );
		Calendar calendar = new CalendarStub(range, Scope.DAY);

		assertEquals(0, calendar.getValidDays().size(), "Should return empty list if calendar is empty." );

		calendar.importDays( CalendarGeneratorStub.stubDayStorableDataSets(range) );

		assertEquals(3, calendar.getValidDays().size(), "Should return all three days" );
	}

	@Test
	public void testGetValidDaysReturnsRightRangeForScopePhase() {

		final DateRange expectedRange = new DateRange( TestConstantsAndHelpers.SOME_DATE, TestConstantsAndHelpers.SOME_DATE.plusDays(2) );
        final DateRange rangeToGenerate = new DateRange( TestConstantsAndHelpers.SOME_DATE.minusDays(1), TestConstantsAndHelpers.SOME_DATE.plusDays(3) );

		final Calendar calendar = new CalendarStub(expectedRange, Scope.PHASE);

		assertEquals(0, calendar.getValidDays().size(), "Should return empty list if calendar is empty." );

		calendar.importDays( CalendarGeneratorStub.stubDayStorableDataSets(rangeToGenerate) );

		final LinkedList<Day> validDays = calendar.getValidDays();

		assertEquals(3, validDays.size(), "Should return three days" );
		assertTrue(validDays.getFirst().getDate().isEqual( expectedRange.getStart() ), "Should return first of expected range" );
        assertNotNull(validDays.getFirst().getPlanetaryData().getLunarPhase(),
                "First should have lunar phase" );
		assertTrue(validDays.getLast().getDate().isEqual( expectedRange.getEnd() ), "Should return last of expected range" );
        assertNotNull(validDays.getLast().getPlanetaryData().getLunarPhase(),
                "Last should have lunar phase" );
	}

	@Test
	public void testGetValidDaysReturnsRightRangeForScopeCycle() {

		final DateRange expectedRange = new DateRange( TestConstantsAndHelpers.SOME_DATE, TestConstantsAndHelpers.SOME_DATE.plusDays(2) );
		final DateRange rangeToGenerate = new DateRange( TestConstantsAndHelpers.SOME_DATES_LAST_EXTREME.minusDays(1), TestConstantsAndHelpers.SOME_DATES_NEXT_EXTREME.plusDays(1) );

		Calendar calendar = new CalendarStub(expectedRange, Scope.CYCLE);

		assertEquals(0, calendar.getValidDays().size(), "Should return empty list if calendar is empty." );

        calendar.importDays( CalendarGeneratorStub.stubDayStorableDataSets(rangeToGenerate) );

		LinkedList<Day> validDays = calendar.getValidDays();

		assertTrue(validDays.getFirst().getDate().isEqual( TestConstantsAndHelpers.SOME_DATES_LAST_EXTREME), "Should start at last extreme" );

		assertNotNull(validDays.getFirst().getPlanetaryData().getLunarPhase(), "First should have lunar phase" );

		assertTrue(validDays.getFirst().getPlanetaryData().getLunarPhase().isLunarExtreme(), "First should be lunar extreme");

		Assertions.assertEquals(0, validDays.getFirst().getPlanetaryData().getDaysSinceLastMaxPhase(), "First should have daysSinceLast");

		Assertions.assertEquals(0, validDays.getFirst().getPlanetaryData().getDaysUntilNextMaxPhase(), "First should have daysUntilNext");

		assertTrue(validDays.getLast().getDate().isEqual( TestConstantsAndHelpers.SOME_DATES_NEXT_EXTREME), "Should end at next extreme" );

		assertNotNull(validDays.getLast().getPlanetaryData().getLunarPhase(), "Last should have lunar phase" );

		assertTrue(validDays.getLast().getPlanetaryData().getLunarPhase().isLunarExtreme(), "Last should be lunar extreme");

		Assertions.assertEquals(0, validDays.getLast().getPlanetaryData().getDaysSinceLastMaxPhase(), "Last should have daysSinceLast");

		Assertions.assertEquals(0, validDays.getLast().getPlanetaryData().getDaysUntilNextMaxPhase(), "Last should have daysUntilNext");

		// test what happens, when more valid days exist
		final LinkedList<DayStorableDataSet> largerThanCycle = new LinkedList<>();

		// add some data before last extreme, ...

		// (needed for lunar phase)
		largerThanCycle.add( new DayStorableDataSet( CalendarGeneratorStub.stubDay( TestConstantsAndHelpers.SOME_DATES_LAST_EXTREME.minusDays(2) ) ) );

		// (this should be the first returned)
		final Day firstValid = CalendarGeneratorStub.stubDay( TestConstantsAndHelpers.SOME_DATES_LAST_EXTREME.minusDays(1) );
		largerThanCycle.add( new DayStorableDataSet( firstValid ) );

		// ... the complete cycle ...
		for ( LocalDate date : new DateRange( TestConstantsAndHelpers.SOME_DATES_LAST_EXTREME, TestConstantsAndHelpers.SOME_DATES_NEXT_EXTREME) ) {
			largerThanCycle.add( new DayStorableDataSet(CalendarGeneratorStub.stubDay(date)) );
		}

		// (this should be the last returned)
		final Day lastValid = CalendarGeneratorStub.stubDay( TestConstantsAndHelpers.SOME_DATES_NEXT_EXTREME.plusDays(1) );
		largerThanCycle.add( new DayStorableDataSet( lastValid ) );

		// needed for lunar phase:
		largerThanCycle.add( new DayStorableDataSet( CalendarGeneratorStub.stubDay( TestConstantsAndHelpers.SOME_DATES_NEXT_EXTREME.plusDays(2) ) ) );


		// a minimal range within old cycle
		final DateRange minimalRange = new DateRange( TestConstantsAndHelpers.SOME_DATE, TestConstantsAndHelpers.SOME_DATE.plusDays(1) );

		calendar = new CalendarStub(minimalRange, Scope.CYCLE);
		calendar.importDays(largerThanCycle);

		validDays = calendar.getValidDays();

		assertTrue(validDays.getFirst().getDate().isEqual( firstValid.getDate() ),
				"Should start at first valid day" );
		assertTrue(validDays.getLast().getDate().isEqual( lastValid.getDate() ),
				"Should end at last valid day" );
	}

	@Test
	public void testGetValidDaysDoesNotReturnInvalidData() {

		final DateRange range1 = new DateRange( TestConstantsAndHelpers.SOME_DATE.minusDays(5), TestConstantsAndHelpers.SOME_DATE.minusDays(3));
		final DateRange range2 = new DateRange( TestConstantsAndHelpers.SOME_DATE.plusDays(3), TestConstantsAndHelpers.SOME_DATE.plusDays(5));

		Calendar calendar = new CalendarStub(range1, Scope.DAY);
        calendar.importDays( CalendarGeneratorStub.stubDayStorableDataSets(range1) );

		calendar.setRangeExpected(range2);
        calendar.importDays( CalendarGeneratorStub.stubDayStorableDataSets(range2) );

		// Now we have a calendar containing a gap ...

		assertNull(calendar.getValidDays(), "Null should be returned if calendar contains invalid data.");
	}


	@Test
	public void testRemoveOverhead() {

		// Test what happens when nothing is to delete
		this.testRemoveOverheadRemovesNothing(Calendar.Scope.DAY);
		this.testRemoveOverheadRemovesNothing(Calendar.Scope.PHASE);
		this.testRemoveOverheadRemovesNothing(Calendar.Scope.CYCLE);

		// Test that days needed for scope calculation aren't deleted
		this.testRemoveOverheadLeavesPhaseIntact(Scope.DAY);
		this.testRemoveOverheadLeavesPhaseIntact(Scope.PHASE);
		this.testRemoveOverheadLeavesPhaseIntact(Scope.CYCLE);

		// Test that all gets removed (and nothing crashes) if existing data is completely outside expected data
		this.testRemoveOverheadRemovesAllIfOutsideExpectedRange(Scope.DAY, false);
		this.testRemoveOverheadRemovesAllIfOutsideExpectedRange(Scope.PHASE, false);
		this.testRemoveOverheadRemovesAllIfOutsideExpectedRange(Scope.CYCLE, false);

		this.testRemoveOverheadRemovesAllIfOutsideExpectedRange(Scope.DAY, true);
		this.testRemoveOverheadRemovesAllIfOutsideExpectedRange(Scope.PHASE, true);
		this.testRemoveOverheadRemovesAllIfOutsideExpectedRange(Scope.CYCLE, true);
	}

	private void testRemoveOverheadRemovesNothing(final Calendar.Scope scope) {

		final DateRange initialRange = new DateRange( TestConstantsAndHelpers.SOME_DATE, TestConstantsAndHelpers.SOME_DATE.plusDays(2));
        DateRange rangeToGenerate=null;

        switch (scope) {
            case DAY:
                rangeToGenerate = initialRange;
                break;
            case PHASE:
                rangeToGenerate = new DateRange(
                        initialRange.getStart().minusDays(1),
                        initialRange.getEnd().plusDays(1) );
                break;
            case CYCLE:
                rangeToGenerate = new DateRange(
                        TestConstantsAndHelpers.SOME_DATES_LAST_EXTREME.minusDays(1),
                        TestConstantsAndHelpers.SOME_DATES_NEXT_EXTREME.plusDays(1) );
                break;
        }

		final Calendar calendar = new CalendarStub(initialRange, scope);

		// empty calendar shouldn't throw exception or remove anything
		List<Day> removed = calendar.removeOverhead(true);
		assertEquals(0, removed.size(), "Nothing should be returned");

        calendar.importDays( CalendarGeneratorStub.stubDayStorableDataSets(rangeToGenerate) );

		// when nothing is to delete, it shouldn't throw an exception or remove
		// anything
		removed = calendar.removeOverhead(true);
		assertEquals(0, removed.size(), "Nothing should be returned");

		// should not throw exception or remove anything when expected range is
		// bigger than calculated/imported range
		final DateRange biggerRange = new DateRange(initialRange.getStart().minusDays(1),
				initialRange.getEnd().plusDays(1));
		calendar.setRangeExpected(biggerRange);

		removed = calendar.removeOverhead(true);

		assertEquals(0, removed.size(), "Nothing should be returned");
	}

	private void testRemoveOverheadLeavesPhaseIntact(final Scope scope) {

		final DateRange initialRange = new DateRange( TestConstantsAndHelpers.SOME_DATES_LAST_EXTREME.minusDays(2), TestConstantsAndHelpers.SOME_DATES_NEXT_EXTREME.plusDays(2));

		final DateRange smallerRange = new DateRange( TestConstantsAndHelpers.SOME_DATES_LAST_EXTREME.plusDays(2), TestConstantsAndHelpers.SOME_DATES_NEXT_EXTREME.minusDays(2));

		Calendar calendar = new CalendarStub(initialRange, scope);

        calendar.importDays( CalendarGeneratorStub.stubDayStorableDataSets(initialRange) );

		calendar.setRangeExpected(smallerRange);
		calendar.removeOverhead(true);

		LinkedList<Day> days = calendar.getAllDays();

		switch (scope) {

			case DAY:
				assertTrue(days.getFirst().getDate().isEqual( smallerRange.getStart() ),
						"DAY: Calendar should start at expected range" );
				assertTrue(days.getLast().getDate().isEqual( smallerRange.getEnd() ),
						"DAY: Calendar should end at expected range" );
				break;

			case PHASE:
				assertTrue(days.getFirst().getDate().isEqual( smallerRange.getStart().minusDays(1) ),
						"PHASE: Calendar should start one day before expected range" );
				assertTrue(days.getLast().getDate().isEqual( smallerRange.getEnd().plusDays(1) ),
						"PHASE: Calendar should end one day after expected range" );
				break;

			case CYCLE:
				assertTrue(days.getFirst().getDate().isEqual( TestConstantsAndHelpers.SOME_DATES_LAST_EXTREME.minusDays(1) ),
						"CYCLE: Calendar should start one day before last lunar extreme" );
				assertTrue(days.getLast().getDate().isEqual( TestConstantsAndHelpers.SOME_DATES_NEXT_EXTREME.plusDays(1) ),
						"PHASE: Calendar should end one day after next lunar extreme" );
				break;
		}
	}

	private void testRemoveOverheadRemovesAllIfOutsideExpectedRange(Scope scope, boolean alsoDeleteFutureDays) {

		final DateRange oldRange = new DateRange( TestConstantsAndHelpers.SOME_DATES_LAST_EXTREME.minusDays(1), TestConstantsAndHelpers.SOME_DATES_NEXT_EXTREME.plusDays(1) );

		DateRange expectedRange;

		if (alsoDeleteFutureDays) {
			// New range somewhere before old range
			expectedRange = new DateRange( TestConstantsAndHelpers.SOME_DATES_LAST_EXTREME.minusDays(5), TestConstantsAndHelpers.SOME_DATES_LAST_EXTREME.minusDays(4) );
		} else {
			// New range somewhere after old range
			expectedRange = new DateRange( TestConstantsAndHelpers.SOME_DATES_NEXT_EXTREME.plusDays(4), TestConstantsAndHelpers.SOME_DATES_NEXT_EXTREME.plusDays(5) );
		}

		Calendar calendar = new CalendarStub(oldRange, scope);

		calendar.importDays( CalendarGeneratorStub.stubDayStorableDataSets(oldRange) );

		calendar.setRangeExpected(expectedRange);
		calendar.removeOverhead(alsoDeleteFutureDays);

		assertEquals(0, calendar.getAllDays().size(), "Calendar should be empty, when overhead is removed");
	}

	@Test
	public void testFixRangeExpected() {
		final DateRange oldRange = new DateRange( TestConstantsAndHelpers.SOME_DATE, TestConstantsAndHelpers.SOME_DATE.plusDays(3));
		final Calendar calendar = new CalendarStub(oldRange, Scope.DAY);
        calendar.importDays( CalendarGeneratorStub.stubDayStorableDataSets(oldRange) );

		// new range is before old range
		DateRange newRange = new DateRange(oldRange.getStart().minusDays(3), oldRange.getStart().minusDays(2));
		calendar.setRangeExpected(newRange);
		calendar.fixRangeExpectedToIncludeExistingDays();
		assertTrue(calendar.getRangeExpected().getStart().isEqual(newRange.getStart()),
				"expectedRange should start at new range's start");
		assertTrue(calendar.getRangeExpected().getEnd().isEqual(oldRange.getEnd()),
				"expectedRange should end at old range's end");

		// new range is after old range
		newRange = new DateRange(oldRange.getEnd().plusDays(2), oldRange.getEnd().plusDays(3));
		calendar.setRangeExpected(newRange);
		calendar.fixRangeExpectedToIncludeExistingDays();
		assertTrue(calendar.getRangeExpected().getStart().isEqual(oldRange.getStart()),
				"expectedRange should start at old range's start");
		assertTrue(calendar.getRangeExpected().getEnd().isEqual(newRange.getEnd()),
				"expectedRange should end at new range's end");

		// new range is contained in old Range
		newRange = new DateRange(oldRange.getStart().plusDays(1), oldRange.getEnd().minusDays(1));
		calendar.setRangeExpected(newRange);
		calendar.fixRangeExpectedToIncludeExistingDays();
		assertTrue(calendar.getRangeExpected().getStart().isEqual(oldRange.getStart()),
				"expectedRange should start at old range's start");
		assertTrue(calendar.getRangeExpected().getEnd().isEqual(oldRange.getEnd()),
				"expectedRange should end at old range's end");
	}


}
