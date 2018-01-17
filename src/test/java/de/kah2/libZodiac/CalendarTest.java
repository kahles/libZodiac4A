package de.kah2.libZodiac;

import de.kah2.libZodiac.Calendar.Scope;
import de.kah2.libZodiac.interpretation.Interpreter;
import de.kah2.libZodiac.interpretation.StubInterpreter;
import org.junit.Test;

import org.threeten.bp.LocalDate;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static de.kah2.libZodiac.TestConstantsAndHelpers.SOME_DATE;
import static de.kah2.libZodiac.TestConstantsAndHelpers.SOME_DATES_LAST_EXTREME;
import static de.kah2.libZodiac.TestConstantsAndHelpers.SOME_DATES_NEXT_EXTREME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class CalendarTest {

    static {
    	// Uncomment to have detailed output
        // TestConstantsAndHelpers.enableLogging("trace");
    }

	@Test
	public void testGetValidDaysReturnsRightRangeForScopeDay() {

		final DateRange range = new DateRange( SOME_DATE, SOME_DATE.plusDays(2) );
		Calendar calendar = new CalendarStub(range, Scope.DAY);

		assertEquals("Should return empty list if calendar is empty.", 0, calendar.getValidDays().size() );

		calendar.importDays( CalendarGeneratorStub.stubDayStorableDataSets(range) );

		assertEquals("Should return all three days", 3, calendar.getValidDays().size() );
	}

	@Test
	public void testGetValidDaysReturnsRightRangeForScopePhase() {

		final DateRange expectedRange = new DateRange( SOME_DATE, SOME_DATE.plusDays(2) );
        final DateRange rangeToGenerate = new DateRange( SOME_DATE.minusDays(1), SOME_DATE.plusDays(3) );

		final Calendar calendar = new CalendarStub(expectedRange, Scope.PHASE);

		assertEquals("Should return empty list if calendar is empty.", 0, calendar.getValidDays().size() );

		calendar.importDays( CalendarGeneratorStub.stubDayStorableDataSets(rangeToGenerate) );

		final LinkedList<Day> validDays = calendar.getValidDays();

		assertEquals("Should return three days", 3, validDays.size() );
		assertTrue("Should return first of expected range", validDays.getFirst().getDate().isEqual( expectedRange.getStart() ) );
        assertNotNull("First should have lunar phase",
                validDays.getFirst().getPlanetaryData().getLunarPhase() );
		assertTrue("Should return last of expected range", validDays.getLast().getDate().isEqual( expectedRange.getEnd() ) );
        assertNotNull("Last should have lunar phase",
                validDays.getLast().getPlanetaryData().getLunarPhase() );
	}

	@Test
	public void testGetValidDaysReturnsRightRangeForScopeCycle() {

		final DateRange expectedRange = new DateRange( SOME_DATE, SOME_DATE.plusDays(2) );
		final DateRange rangeToGenerate = new DateRange( SOME_DATES_LAST_EXTREME.minusDays(1), SOME_DATES_NEXT_EXTREME.plusDays(1) );

		Calendar calendar = new CalendarStub(expectedRange, Scope.CYCLE);

		assertEquals("Should return empty list if calendar is empty.", 0, calendar.getValidDays().size() );

        calendar.importDays( CalendarGeneratorStub.stubDayStorableDataSets(rangeToGenerate) );

		LinkedList<Day> validDays = calendar.getValidDays();

		assertTrue("Should start at last extreme",
				validDays.getFirst().getDate().isEqual(SOME_DATES_LAST_EXTREME) );

		assertNotNull("First should have lunar phase",
				validDays.getFirst().getPlanetaryData().getLunarPhase() );

		assertTrue("Should end at next extreme",
				validDays.getLast().getDate().isEqual(SOME_DATES_NEXT_EXTREME) );

		assertNotNull("Last should have lunar phase",
				validDays.getLast().getPlanetaryData().getLunarPhase() );

		// test what happens, when more valid days exist
		final LinkedList<DayStorableDataSet> largerThanCycle = new LinkedList<>();

		// add some data before last extreme, ...

		// (needed for lunar phase)
		largerThanCycle.add( new DayStorableDataSet( CalendarGeneratorStub.stubDay( SOME_DATES_LAST_EXTREME.minusDays(2) ) ) );

		// (this should be the first returned)
		final Day firstValid = CalendarGeneratorStub.stubDay( SOME_DATES_LAST_EXTREME.minusDays(1) );
		largerThanCycle.add( new DayStorableDataSet( firstValid ) );

		// ... the complete cycle ...
		for ( LocalDate date : new DateRange(SOME_DATES_LAST_EXTREME, SOME_DATES_NEXT_EXTREME) ) {
			largerThanCycle.add( new DayStorableDataSet(CalendarGeneratorStub.stubDay(date)) );
		}

		// (this should be the last returned)
		final Day lastValid = CalendarGeneratorStub.stubDay( SOME_DATES_NEXT_EXTREME.plusDays(1) );
		largerThanCycle.add( new DayStorableDataSet( lastValid ) );

		// needed for lunar phase:
		largerThanCycle.add( new DayStorableDataSet( CalendarGeneratorStub.stubDay( SOME_DATES_NEXT_EXTREME.plusDays(2) ) ) );


		// a minimal range within old cycle
		final DateRange minimalRange = new DateRange( SOME_DATE, SOME_DATE.plusDays(1) );

		calendar = new CalendarStub(minimalRange, Scope.CYCLE);
		calendar.importDays(largerThanCycle);

		validDays = calendar.getValidDays();

		assertTrue("Should start at first valid day",
				validDays.getFirst().getDate().isEqual( firstValid.getDate() ) );
		assertTrue("Should end at last valid day",
				validDays.getLast().getDate().isEqual( lastValid.getDate() ) );
	}

	@Test
	public void testGetValidDaysDoesNotReturnInvalidData() {

		final DateRange range1 = new DateRange(SOME_DATE.minusDays(5), SOME_DATE.minusDays(3));
		final DateRange range2 = new DateRange(SOME_DATE.plusDays(3), SOME_DATE.plusDays(5));

		Calendar calendar = new CalendarStub(range1, Scope.DAY);
        calendar.importDays( CalendarGeneratorStub.stubDayStorableDataSets(range1) );

		calendar.setRangeExpected(range2);
        calendar.importDays( CalendarGeneratorStub.stubDayStorableDataSets(range2) );

		// Now we have a calendar containing a gap ...

		assertNull("Null should be returned if calendar contains invalid data.", calendar.getValidDays());
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
	}

	private void testRemoveOverheadRemovesNothing(final Calendar.Scope scope) {

		final DateRange initialRange = new DateRange(SOME_DATE, SOME_DATE.plusDays(2));
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
                        SOME_DATES_LAST_EXTREME.minusDays(1),
                        SOME_DATES_NEXT_EXTREME.plusDays(1) );
                break;
        }

		final Calendar calendar = new CalendarStub(initialRange, scope);

		// empty calendar shouldn't throw exception or remove anything
		List<Day> removed = calendar.removeOverhead(true);
		assertEquals("Nothing should be returned", 0, removed.size());

        calendar.importDays( CalendarGeneratorStub.stubDayStorableDataSets(rangeToGenerate) );

		// when nothing is to delete, it shouldn't throw an exception or remove
		// anything
		removed = calendar.removeOverhead(true);
		assertEquals("Nothing should be returned", 0, removed.size());

		// should not throw exception or remove anything when expected range is
		// bigger than calculated/imported range
		final DateRange biggerRange = new DateRange(initialRange.getStart().minusDays(1),
				initialRange.getEnd().plusDays(1));
		calendar.setRangeExpected(biggerRange);

		removed = calendar.removeOverhead(true);

		assertEquals("Nothing should be returned", 0, removed.size());
	}

	private void testRemoveOverheadLeavesPhaseIntact(final Scope scope) {

		final DateRange initialRange = new DateRange(SOME_DATES_LAST_EXTREME.minusDays(2), SOME_DATES_NEXT_EXTREME.plusDays(2));

		final DateRange smallerRange = new DateRange(SOME_DATES_LAST_EXTREME.plusDays(2), SOME_DATES_NEXT_EXTREME.minusDays(2));

		Calendar calendar = new CalendarStub(initialRange, scope);

        calendar.importDays( CalendarGeneratorStub.stubDayStorableDataSets(initialRange) );

		calendar.setRangeExpected(smallerRange);
		calendar.removeOverhead(true);

		LinkedList<Day> days = calendar.getAllDays();

		switch (scope) {

			case DAY:
				assertTrue("DAY: Calendar should start at expected range",
						days.getFirst().getDate().isEqual( smallerRange.getStart() ) );
				assertTrue("DAY: Calendar should end at expected range",
						days.getLast().getDate().isEqual( smallerRange.getEnd() ) );
				break;

			case PHASE:
				assertTrue("PHASE: Calendar should start one day before expected range",
						days.getFirst().getDate().isEqual( smallerRange.getStart().minusDays(1) ) );
				assertTrue("PHASE: Calendar should end one day after expected range",
						days.getLast().getDate().isEqual( smallerRange.getEnd().plusDays(1) ) );
				break;

			case CYCLE:
				assertTrue("CYCLE: Calendar should start one day before last lunar extreme",
						days.getFirst().getDate().isEqual( SOME_DATES_LAST_EXTREME.minusDays(1) ) );
				assertTrue("PHASE: Calendar should end one day after next lunar extreme",
						days.getLast().getDate().isEqual( SOME_DATES_NEXT_EXTREME.plusDays(1) ) );
				break;
		}
	}


	@Test
	public void testFixRangeExpected() {
		final DateRange oldRange = new DateRange(SOME_DATE, SOME_DATE.plusDays(3));
		final Calendar calendar = new CalendarStub(oldRange, Scope.DAY);
        calendar.importDays( CalendarGeneratorStub.stubDayStorableDataSets(oldRange) );

		// new range is before old range
		DateRange newRange = new DateRange(oldRange.getStart().minusDays(3), oldRange.getStart().minusDays(2));
		calendar.setRangeExpected(newRange);
		calendar.fixRangeExpectedToIncludeExistingDays();
		assertTrue("expectedRange should start at new range's start",
				calendar.getRangeExpected().getStart().isEqual(newRange.getStart()));
		assertTrue("expectedRange should end at old range's end",
				calendar.getRangeExpected().getEnd().isEqual(oldRange.getEnd()));

		// new range is after old range
		newRange = new DateRange(oldRange.getEnd().plusDays(2), oldRange.getEnd().plusDays(3));
		calendar.setRangeExpected(newRange);
		calendar.fixRangeExpectedToIncludeExistingDays();
		assertTrue("expectedRange should start at old range's start",
				calendar.getRangeExpected().getStart().isEqual(oldRange.getStart()));
		assertTrue("expectedRange should end at new range's end",
				calendar.getRangeExpected().getEnd().isEqual(newRange.getEnd()));

		// new range is contained in old Range
		newRange = new DateRange(oldRange.getStart().plusDays(1), oldRange.getEnd().minusDays(1));
		calendar.setRangeExpected(newRange);
		calendar.fixRangeExpectedToIncludeExistingDays();
		assertTrue("expectedRange should start at old range's start",
				calendar.getRangeExpected().getStart().isEqual(oldRange.getStart()));
		assertTrue("expectedRange should end at old range's end",
				calendar.getRangeExpected().getEnd().isEqual(oldRange.getEnd()));
	}

	@Test
	public void testAddAndRemoveInterpreter() {

		DateRange range = new DateRange(SOME_DATE, SOME_DATE.plusDays(2));

		final Calendar calendar = new CalendarStub(range, Scope.DAY);
        calendar.importDays( CalendarGeneratorStub.stubDayStorableDataSets(range) );

		assertEquals("Interpreters should be empty when Calendar is initialized", 0,
				calendar.getActiveInterpreterClasses().size());

		// Interpreters should only be stored once
		calendar.addInterpreter(StubInterpreter.class);
		calendar.addInterpreter(StubInterpreter.class);
		int numberOfInterpretersSet = 1;
		assertEquals("One Interpreter should be set to Calendar", numberOfInterpretersSet,
				calendar.getActiveInterpreterClasses().size());

		this.checkInterpreterCount("Interpreters should get inserted to all days", numberOfInterpretersSet,
				calendar.getAllDays() );

		// extend the Calendar:

		// extend it and check interpreters
		range = new DateRange(range.getStart().minusDays(1), range.getEnd().plusDays(1));
		calendar.setRangeExpected(range);

		TestConstantsAndHelpers.generateAndWaitFor(calendar);

		this.checkInterpreterCount("Interpreters should also be set to newly generated days", numberOfInterpretersSet,
				calendar.getAllDays() );

		calendar.removeInterpreter(StubInterpreter.class);
		numberOfInterpretersSet = 0;
		assertEquals("Interpreters should get removed from Calendar", numberOfInterpretersSet,
				calendar.getActiveInterpreterClasses().size());

		this.checkInterpreterCount("Interpreters should get removed from all days", numberOfInterpretersSet,
				calendar.getAllDays() );
	}

	private void checkInterpreterCount(final String message, final int expectedCount, final Iterable<Day> days) {
		for (final Day day : days) {
			final Collection<Interpreter> interpreters = day.getInterpretationData().getInterpreters();
			assertEquals(message, expectedCount, interpreters.size());
		}
	}
}
