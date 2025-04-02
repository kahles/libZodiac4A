package de.kah2.zodiac.libZodiac4A;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class CalendarDataTest {

    @Test
    public void testImportDays() {
        CalendarData days = new CalendarData();

        final List<DayStorableDataSet> dayListToImport = new LinkedList<>();

		assertThat(days.size()).as("Importing an empty list should do nothing").isEqualTo(0);

        dayListToImport.add(new DayStorableDataSetPojo( TestConstantsAndHelpers.SOME_DATE));

        days.importDays(dayListToImport);

		assertThat(days.size()).as("One day should be imported").isEqualTo(1);
    }

    @Test
    public void testGet() {
        final DateRange range = new DateRange( TestConstantsAndHelpers.SOME_DATE, TestConstantsAndHelpers.SOME_DATE.plusDays(3));
        final CalendarData days = new CalendarData();

		assertThat(days.get(TestConstantsAndHelpers.SOME_DATE)).as("Null should be returned if day isn't available").isNull();

        generateDays(days, range);

        final LocalDate beforeStart = range.getStart().minusDays(1);
        final LocalDate afterEnd = range.getEnd().plusDays(1);
		assertThat(days.get(beforeStart)).as("Should return null when date before range requested").isNull();
		assertThat(days.get(afterEnd)).as("Should return null when date after range requested").isNull();

        final String rangeStr = range.toString();
        for (final LocalDate date : range) {
            final Day day = days.get(date);
			assertThat(day).as("Day for " + date + " contained in " + rangeStr + " should be returned").isNotNull();
			assertThat(day.getDate().isEqual(date)).as("Should have date as requested").isTrue();
        }

        // extend Calendar and see if days are now contained
        generateDays(days, new DateRange(beforeStart, afterEnd));
		assertThat(days.get(beforeStart)).as("Day for " + beforeStart + " should be returned").isNotNull();
		assertThat(days.get(afterEnd)).as("Day for " + afterEnd + " should be returned").isNotNull();
    }

    @Test
    public void testAll() {
        DateRange range = new DateRange( TestConstantsAndHelpers.SOME_DATE, TestConstantsAndHelpers.SOME_DATE.plusDays(3));
        final CalendarData days = new CalendarData();

		assertThat(days.allAsList().isEmpty()).as("Calendar should be empty").isTrue();

        generateDays(days, range);

        this.testRangeMatchesDays(days.allAsList(), range);

        // extend and see if iterator still matches range
        range = new DateRange(range.getStart().minusDays(1), range.getEnd().plusDays(1));
        generateDays(days, range);
        this.testRangeMatchesDays(days.allAsList(), range);
    }

    private void testRangeMatchesDays(LinkedList<Day> days, DateRange expectedRange) {
		assertThat(days.getFirst().getDate().isEqual(expectedRange.getStart())).as("Start date should match").isTrue();
		assertThat(days.getLast().getDate().isEqual(expectedRange.getEnd())).as("End date should match").isTrue();
    }


    @Test
    public void testGetMissingDates() {
        final CalendarData days = new CalendarData();

        final DateRange rangeExpected = new DateRange( TestConstantsAndHelpers.SOME_DATE, TestConstantsAndHelpers.SOME_DATE.plusDays(4) );

        LinkedList<LocalDate> missing = days.getMissingDates(rangeExpected);
        Collections.sort(missing);

		assertThat(missing.size()).as("All dates should be returned").isEqualTo(rangeExpected.size());
		assertThat(rangeExpected.getStart().isEqual(missing.getFirst())).as("First missing date should be range start").isTrue();
		assertThat(rangeExpected.getEnd().isEqual(missing.getLast())).as("Last missing date should be range end").isTrue();

        days.insert( CalendarGeneratorStub.stubDay( TestConstantsAndHelpers.SOME_DATE.plusDays(1) ) );
        days.insert( CalendarGeneratorStub.stubDay( TestConstantsAndHelpers.SOME_DATE.plusDays(3) ) );

        missing = days.getMissingDates(rangeExpected);
        Collections.sort(missing);

		assertThat(missing.size()).as("Three dates should be returned").isEqualTo(3);
		assertThat(rangeExpected.getStart().isEqual(missing.getFirst())).as("First missing date should be range start").isTrue();
		assertThat(TestConstantsAndHelpers.SOME_DATE.plusDays(2).isEqual(missing.get(1))).as("Second missing date should 2 days ahead of range start").isTrue();
		assertThat(rangeExpected.getEnd().isEqual(missing.getLast())).as("Last missing date should be range end").isTrue();

        days.insert( CalendarGeneratorStub.stubDay( TestConstantsAndHelpers.SOME_DATE ) );
        days.insert( CalendarGeneratorStub.stubDay( TestConstantsAndHelpers.SOME_DATE.plusDays(2) ) );
        days.insert( CalendarGeneratorStub.stubDay( TestConstantsAndHelpers.SOME_DATE.plusDays(4) ) );

        missing = days.getMissingDates(rangeExpected);
        Collections.sort(missing);

		assertThat(missing.size()).as("No dates should be returned, if no dates are missing").isEqualTo(0);
    }

    @Test
    public void testRemoveBefore() {

		/*
		 * Test removing past overhead
		 */
        final DateRange initialRange = new DateRange( TestConstantsAndHelpers.SOME_DATE, TestConstantsAndHelpers.SOME_DATE.plusDays(7));
        CalendarData calendarData = new CalendarData();
        generateDays(calendarData, initialRange);

        // remove only past days outside range
        final LocalDate laterStart = TestConstantsAndHelpers.SOME_DATE.plusDays(2);

        List<Day> removed = calendarData.removeBefore(laterStart);

        Collections.sort(removed);
		assertThat(removed.size()).as("2 days should be deleted").isEqualTo(2);

		assertThat(removed.get(0).getDate().isEqual(initialRange.getStart())).as("Should remove first").isTrue();

		assertThat(removed.get(1).getDate().isEqual(initialRange.getStart().plusDays(1))).as("Should remove second").isTrue();

        LinkedList<Day> days = calendarData.allAsList();

		assertThat(days.getFirst().getDate().isEqual(laterStart)).as("Calendar should start at new range").isTrue();

		assertThat(days.getLast().getDate().isEqual(initialRange.getEnd())).as("Calendar should still end at old range").isTrue();
    }
    
    @Test
    public void testRemoveAfter() {

		/*
		 * Test removing past overhead
		 */
        final DateRange initialRange = new DateRange( TestConstantsAndHelpers.SOME_DATE, TestConstantsAndHelpers.SOME_DATE.plusDays(7));
        CalendarData calendarData = new CalendarData();
        generateDays(calendarData, initialRange);

        // remove only past days outside range
        LocalDate earlierEnd = TestConstantsAndHelpers.SOME_DATE.plusDays(5);

        List<Day> removed = calendarData.removeAfter(earlierEnd);

        Collections.sort(removed);
		assertThat(removed.size()).as("2 days should be deleted").isEqualTo(2);

		assertThat(removed.get(0).getDate().isEqual(initialRange.getEnd().minusDays(1))).as("Should remove day before last").isTrue();

		assertThat(removed.get(1).getDate().isEqual(initialRange.getEnd())).as("Should remove last").isTrue();

        LinkedList<Day> days = calendarData.allAsList();

		assertThat(days.getFirst().getDate().isEqual(initialRange.getStart())).as("Calendar should still start at old range").isTrue();

		assertThat(days.getLast().getDate().isEqual(earlierEnd)).as("Calendar should end at new end").isTrue();
    }

    private void generateDays(CalendarData days, DateRange rangeToGenerate) {
        for (LocalDate date : rangeToGenerate) {
            final Day day = CalendarGeneratorStub.stubDay(date);
            days.insert(day);
        }
    }
}
