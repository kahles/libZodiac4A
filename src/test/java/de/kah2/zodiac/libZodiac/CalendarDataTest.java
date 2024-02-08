package de.kah2.zodiac.libZodiac;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CalendarDataTest {

    @Test
    public void testImportDays() {
        CalendarData days = new CalendarData();

        final List<DayStorableDataSet> dayListToImport = new LinkedList<>();

        assertEquals(0, days.size(), "Importing an empty list should do nothing");

        dayListToImport.add(new DayStorableDataSet( TestConstantsAndHelpers.SOME_DATE));

        days.importDays(dayListToImport);

        assertEquals(1, days.size(), "One day should be imported");
    }

    @Test
    public void testGet() {
        final DateRange range = new DateRange( TestConstantsAndHelpers.SOME_DATE, TestConstantsAndHelpers.SOME_DATE.plusDays(3));
        final CalendarData days = new CalendarData();

        assertNull(days.get( TestConstantsAndHelpers.SOME_DATE), "Null should be returned if day isn't available" );

        generateDays(days, range);

        final LocalDate beforeStart = range.getStart().minusDays(1);
        final LocalDate afterEnd = range.getEnd().plusDays(1);
        assertNull(days.get(beforeStart), "Should return null when date before range requested" );
        assertNull(days.get(afterEnd), "Should return null when date after range requested" );

        final String rangeStr = range.toString();
        for (final LocalDate date : range) {
            final Day day = days.get(date);
            assertNotNull(day, "Day for " + date + " contained in " + rangeStr + " should be returned");
            assertTrue(day.getDate().isEqual(date), "Should have date as requested");
        }

        // extend Calendar and see if days are now contained
        generateDays(days, new DateRange(beforeStart, afterEnd));
        assertNotNull(days.get(beforeStart), "Day for " + beforeStart + " should be returned" );
        assertNotNull(days.get(afterEnd), "Day for " + afterEnd + " should be returned" );
    }

    @Test
    public void testAll() {
        DateRange range = new DateRange( TestConstantsAndHelpers.SOME_DATE, TestConstantsAndHelpers.SOME_DATE.plusDays(3));
        final CalendarData days = new CalendarData();

        assertTrue(days.allAsList().isEmpty(), "Calendar should be empty");

        generateDays(days, range);

        this.testRangeMatchesDays(days.allAsList(), range);

        // extend and see if iterator still matches range
        range = new DateRange(range.getStart().minusDays(1), range.getEnd().plusDays(1));
        generateDays(days, range);
        this.testRangeMatchesDays(days.allAsList(), range);
    }

    private void testRangeMatchesDays(LinkedList<Day> days, DateRange expectedRange) {
        assertTrue(days.getFirst().getDate().isEqual(expectedRange.getStart()), "Start date should match");
        assertTrue(days.getLast().getDate().isEqual(expectedRange.getEnd()), "End date should match");
    }


    @Test
    public void testGetMissingDates() {
        final CalendarData days = new CalendarData();

        final DateRange rangeExpected = new DateRange( TestConstantsAndHelpers.SOME_DATE, TestConstantsAndHelpers.SOME_DATE.plusDays(4) );

        LinkedList<LocalDate> missing = days.getMissingDates(rangeExpected);
        Collections.sort(missing);

        assertEquals(rangeExpected.size(), missing.size(), "All dates should be returned");
        assertTrue(rangeExpected.getStart().isEqual( missing.getFirst() ), "First missing date should be range start" );
        assertTrue(rangeExpected.getEnd().isEqual( missing.getLast() ), "Last missing date should be range end" );

        days.insert( CalendarGeneratorStub.stubDay( TestConstantsAndHelpers.SOME_DATE.plusDays(1) ) );
        days.insert( CalendarGeneratorStub.stubDay( TestConstantsAndHelpers.SOME_DATE.plusDays(3) ) );

        missing = days.getMissingDates(rangeExpected);
        Collections.sort(missing);

        assertEquals(3, missing.size(), "Three dates should be returned");
        assertTrue(rangeExpected.getStart().isEqual( missing.getFirst() ), "First missing date should be range start" );
        assertTrue(TestConstantsAndHelpers.SOME_DATE.plusDays(2).isEqual( missing.get(1) ), "Second missing date should 2 days ahead of range start" );
        assertTrue(rangeExpected.getEnd().isEqual( missing.getLast() ), "Last missing date should be range end" );

        days.insert( CalendarGeneratorStub.stubDay( TestConstantsAndHelpers.SOME_DATE ) );
        days.insert( CalendarGeneratorStub.stubDay( TestConstantsAndHelpers.SOME_DATE.plusDays(2) ) );
        days.insert( CalendarGeneratorStub.stubDay( TestConstantsAndHelpers.SOME_DATE.plusDays(4) ) );

        missing = days.getMissingDates(rangeExpected);
        Collections.sort(missing);

        assertEquals(0, missing.size(), "No dates should be returned, if no dates are missing");
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
        assertEquals(2, removed.size(), "2 days should be deleted");

        assertTrue(removed.get(0).getDate().isEqual(initialRange.getStart()), "Should remove first");

        assertTrue(removed.get(1).getDate().isEqual(initialRange.getStart().plusDays(1)), "Should remove second");

        LinkedList<Day> days = calendarData.allAsList();

        assertTrue(days.getFirst().getDate().isEqual(laterStart),
                "Calendar should start at new range" );

        assertTrue(days.getLast().getDate().isEqual(initialRange.getEnd()), "Calendar should still end at old range");
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
        assertEquals(2, removed.size(), "2 days should be deleted");

        assertTrue(removed.get(0).getDate().isEqual( initialRange.getEnd().minusDays(1) ), "Should remove day before last" );

        assertTrue(removed.get(1).getDate().isEqual( initialRange.getEnd() ), "Should remove last" );

        LinkedList<Day> days = calendarData.allAsList();

        assertTrue(days.getFirst().getDate().isEqual( initialRange.getStart() ),
                "Calendar should still start at old range" );

        assertTrue(days.getLast().getDate().isEqual(earlierEnd), "Calendar should end at new end" );
    }

    private void generateDays(CalendarData days, DateRange rangeToGenerate) {
        for (LocalDate date : rangeToGenerate) {
            final Day day = CalendarGeneratorStub.stubDay(date);
            days.insert(day);
        }
    }
}
