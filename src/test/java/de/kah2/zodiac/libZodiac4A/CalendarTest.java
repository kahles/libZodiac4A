package de.kah2.zodiac.libZodiac4A;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import de.kah2.zodiac.libZodiac4A.Calendar.Scope;

public class CalendarTest {

	@Test
	public void testGetValidDaysReturnsRightRangeForScopeDay() {

		final DateRange range = new DateRange( TestConstantsAndHelpers.SOME_DATE, TestConstantsAndHelpers.SOME_DATE.plusDays(2) );
		Calendar calendar = new CalendarStub(range, Scope.DAY);

		assertThat(calendar.getValidDays().size()).as("Should return empty list if calendar is empty.").isEqualTo(0);

		calendar.importDays( CalendarGeneratorStub.stubDayStorableDataSets(range) );

		assertThat(calendar.getValidDays().size()).as("Should return all three days").isEqualTo(3);
	}

	@Test
	public void testGetValidDaysReturnsRightRangeForScopePhase() {

		final DateRange expectedRange = new DateRange( TestConstantsAndHelpers.SOME_DATE, TestConstantsAndHelpers.SOME_DATE.plusDays(2) );
        final DateRange rangeToGenerate = new DateRange( TestConstantsAndHelpers.SOME_DATE.minusDays(1), TestConstantsAndHelpers.SOME_DATE.plusDays(3) );

		final Calendar calendar = new CalendarStub(expectedRange, Scope.PHASE);

		assertThat(calendar.getValidDays().size()).as("Should return empty list if calendar is empty.").isEqualTo(0);

		calendar.importDays( CalendarGeneratorStub.stubDayStorableDataSets(rangeToGenerate) );

		final LinkedList<Day> validDays = calendar.getValidDays();

		assertThat(validDays.size()).as("Should return three days").isEqualTo(3);
		assertThat(validDays.getFirst().getDate().isEqual(expectedRange.getStart())).as("Should return first of expected range").isTrue();
		assertThat(validDays.getFirst().getPlanetaryData().getLunarPhase()).as("First should have lunar phase").isNotNull();
		assertThat(validDays.getLast().getDate().isEqual(expectedRange.getEnd())).as("Should return last of expected range").isTrue();
		assertThat(validDays.getLast().getPlanetaryData().getLunarPhase()).as("Last should have lunar phase").isNotNull();
	}

	@Test
	public void testGetValidDaysReturnsRightRangeForScopeCycle() {

		final DateRange expectedRange = new DateRange( TestConstantsAndHelpers.SOME_DATE, TestConstantsAndHelpers.SOME_DATE.plusDays(2) );
		final DateRange rangeToGenerate = new DateRange( TestConstantsAndHelpers.SOME_DATES_LAST_EXTREME.minusDays(1), TestConstantsAndHelpers.SOME_DATES_NEXT_EXTREME.plusDays(1) );

		Calendar calendar = new CalendarStub(expectedRange, Scope.CYCLE);

		assertThat(calendar.getValidDays().size()).as("Should return empty list if calendar is empty.").isEqualTo(0);

        calendar.importDays( CalendarGeneratorStub.stubDayStorableDataSets(rangeToGenerate) );

		LinkedList<Day> validDays = calendar.getValidDays();

		assertThat(validDays.getFirst().getDate().isEqual(TestConstantsAndHelpers.SOME_DATES_LAST_EXTREME)).as("Should start at last extreme").isTrue();

		assertThat(validDays.getFirst().getPlanetaryData().getLunarPhase()).as("First should have lunar phase").isNotNull();

		assertThat(validDays.getFirst().getPlanetaryData().getLunarPhase().isLunarExtreme()).as("First should be lunar extreme").isTrue();

		assertThat(validDays.getFirst().getPlanetaryData().getDaysSinceLastMaxPhase()).as("First should have daysSinceLast").isEqualTo(0);

		assertThat(validDays.getFirst().getPlanetaryData().getDaysUntilNextMaxPhase()).as("First should have daysUntilNext").isEqualTo(0);

		assertThat(validDays.getLast().getDate().isEqual(TestConstantsAndHelpers.SOME_DATES_NEXT_EXTREME)).as("Should end at next extreme").isTrue();

		assertThat(validDays.getLast().getPlanetaryData().getLunarPhase()).as("Last should have lunar phase").isNotNull();

		assertThat(validDays.getLast().getPlanetaryData().getLunarPhase().isLunarExtreme()).as("Last should be lunar extreme").isTrue();

		assertThat(validDays.getLast().getPlanetaryData().getDaysSinceLastMaxPhase()).as("Last should have daysSinceLast").isEqualTo(0);

		assertThat(validDays.getLast().getPlanetaryData().getDaysUntilNextMaxPhase()).as("Last should have daysUntilNext").isEqualTo(0);

		// test what happens, when more valid days exist
		final LinkedList<DayStorableDataSet> largerThanCycle = new LinkedList<>();

		// add some data before last extreme, ...

		// (needed for lunar phase)
		largerThanCycle.add( new DayStorableDataSetPojo( CalendarGeneratorStub.stubDay( TestConstantsAndHelpers.SOME_DATES_LAST_EXTREME.minusDays(2) ) ) );

		// (this should be the first returned)
		final Day firstValid = CalendarGeneratorStub.stubDay( TestConstantsAndHelpers.SOME_DATES_LAST_EXTREME.minusDays(1) );
		largerThanCycle.add( new DayStorableDataSetPojo( firstValid ) );

		// ... the complete cycle ...
		for ( LocalDate date : new DateRange( TestConstantsAndHelpers.SOME_DATES_LAST_EXTREME, TestConstantsAndHelpers.SOME_DATES_NEXT_EXTREME) ) {
			largerThanCycle.add( new DayStorableDataSetPojo(CalendarGeneratorStub.stubDay(date)) );
		}

		// (this should be the last returned)
		final Day lastValid = CalendarGeneratorStub.stubDay( TestConstantsAndHelpers.SOME_DATES_NEXT_EXTREME.plusDays(1) );
		largerThanCycle.add( new DayStorableDataSetPojo( lastValid ) );

		// needed for lunar phase:
		largerThanCycle.add( new DayStorableDataSetPojo( CalendarGeneratorStub.stubDay( TestConstantsAndHelpers.SOME_DATES_NEXT_EXTREME.plusDays(2) ) ) );


		// a minimal range within old cycle
		final DateRange minimalRange = new DateRange( TestConstantsAndHelpers.SOME_DATE, TestConstantsAndHelpers.SOME_DATE.plusDays(1) );

		calendar = new CalendarStub(minimalRange, Scope.CYCLE);
		calendar.importDays(largerThanCycle);

		validDays = calendar.getValidDays();

		assertThat(validDays.getFirst().getDate().isEqual(firstValid.getDate())).as("Should start at first valid day").isTrue();
		assertThat(validDays.getLast().getDate().isEqual(lastValid.getDate())).as("Should end at last valid day").isTrue();
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

		assertThat(calendar.getValidDays()).as("Null should be returned if calendar contains invalid data.").isNull();
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
        DateRange rangeToGenerate = switch ( scope ) {
			case DAY -> initialRange;
			case PHASE -> new DateRange(
					initialRange.getStart().minusDays( 1 ),
					initialRange.getEnd().plusDays( 1 ) );
			case CYCLE -> new DateRange(
					TestConstantsAndHelpers.SOME_DATES_LAST_EXTREME.minusDays( 1 ),
					TestConstantsAndHelpers.SOME_DATES_NEXT_EXTREME.plusDays( 1 ) );
		};

		final Calendar calendar = new CalendarStub(initialRange, scope);

		// empty calendar shouldn't throw exception or remove anything
		List<Day> removed = calendar.removeOverhead(true);
		assertThat(removed.size()).as("Nothing should be returned").isEqualTo(0);

        calendar.importDays( CalendarGeneratorStub.stubDayStorableDataSets(rangeToGenerate) );

		// when nothing is to delete, it shouldn't throw an exception or remove
		// anything
		removed = calendar.removeOverhead(true);
		assertThat(removed.size()).as("Nothing should be returned").isEqualTo(0);

		// should not throw exception or remove anything when expected range is
		// bigger than calculated/imported range
		final DateRange biggerRange = new DateRange(initialRange.getStart().minusDays(1),
				initialRange.getEnd().plusDays(1));
		calendar.setRangeExpected(biggerRange);

		removed = calendar.removeOverhead(true);

		assertThat(removed.size()).as("Nothing should be returned").isEqualTo(0);
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
				assertThat(days.getFirst().getDate().isEqual(smallerRange.getStart())).as("DAY: Calendar should start at expected range").isTrue();
				assertThat(days.getLast().getDate().isEqual(smallerRange.getEnd())).as("DAY: Calendar should end at expected range").isTrue();
				break;

			case PHASE:
				assertThat(days.getFirst().getDate().isEqual(smallerRange.getStart().minusDays(1))).as("PHASE: Calendar should start one day before expected range").isTrue();
				assertThat(days.getLast().getDate().isEqual(smallerRange.getEnd().plusDays(1))).as("PHASE: Calendar should end one day after expected range").isTrue();
				break;

			case CYCLE:
				assertThat(days.getFirst().getDate().isEqual(TestConstantsAndHelpers.SOME_DATES_LAST_EXTREME.minusDays(1))).as("CYCLE: Calendar should start one day before last lunar extreme").isTrue();
				assertThat(days.getLast().getDate().isEqual(TestConstantsAndHelpers.SOME_DATES_NEXT_EXTREME.plusDays(1))).as("PHASE: Calendar should end one day after next lunar extreme").isTrue();
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

		assertThat(calendar.getAllDays().size()).as("Calendar should be empty, when overhead is removed").isEqualTo(0);
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
		assertThat(calendar.getRangeExpected().getStart().isEqual(newRange.getStart())).as("expectedRange should start at new range's start").isTrue();
		assertThat(calendar.getRangeExpected().getEnd().isEqual(oldRange.getEnd())).as("expectedRange should end at old range's end").isTrue();

		// new range is after old range
		newRange = new DateRange(oldRange.getEnd().plusDays(2), oldRange.getEnd().plusDays(3));
		calendar.setRangeExpected(newRange);
		calendar.fixRangeExpectedToIncludeExistingDays();
		assertThat(calendar.getRangeExpected().getStart().isEqual(oldRange.getStart())).as("expectedRange should start at old range's start").isTrue();
		assertThat(calendar.getRangeExpected().getEnd().isEqual(newRange.getEnd())).as("expectedRange should end at new range's end").isTrue();

		// new range is contained in old Range
		newRange = new DateRange(oldRange.getStart().plusDays(1), oldRange.getEnd().minusDays(1));
		calendar.setRangeExpected(newRange);
		calendar.fixRangeExpectedToIncludeExistingDays();
		assertThat(calendar.getRangeExpected().getStart().isEqual(oldRange.getStart())).as("expectedRange should start at old range's start").isTrue();
		assertThat(calendar.getRangeExpected().getEnd().isEqual(oldRange.getEnd())).as("expectedRange should end at old range's end").isTrue();
	}


}
