package de.kah2.zodiac.libZodiac;

import org.junit.Test;
import org.threeten.bp.LocalDate;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class CalendarDataTest {

    @Test
    public void testImportDays() {
        CalendarData days = new CalendarData();

        final List<DayStorableDataSet> dayListToImport = new LinkedList<>();

        assertEquals("Importing an empty list should do nothing", 0, days.size());

        dayListToImport.add(new DayStorableDataSet( TestConstantsAndHelpers.SOME_DATE));

        days.importDays(dayListToImport);

        assertEquals("One day should be imported", 1, days.size());
    }

    @Test
    public void testGet() {
        final DateRange range = new DateRange( TestConstantsAndHelpers.SOME_DATE, TestConstantsAndHelpers.SOME_DATE.plusDays(3));
        final CalendarData days = new CalendarData();

        assertNull("Null should be returned if day isn't available", days.get( TestConstantsAndHelpers.SOME_DATE) );

        generateDays(days, range);

        final LocalDate beforeStart = range.getStart().minusDays(1);
        final LocalDate afterEnd = range.getEnd().plusDays(1);
        assertNull("Should return null when date before range requested", days.get(beforeStart) );
        assertNull("Should return null when date after range requested", days.get(afterEnd) );

        final String rangeStr = range.toString();
        for (final LocalDate date : range) {
            final Day day = days.get(date);
            assertNotNull("Day for " + date + " contained in " + rangeStr + " should be returned", day);
            assertTrue("Should have date as requested", day.getDate().isEqual(date));
        }

        // extend Calendar and see if days are now contained
        generateDays(days, new DateRange(beforeStart, afterEnd));
        assertNotNull("Day for " + beforeStart + " should be returned", days.get(beforeStart) );
        assertNotNull("Day for " + afterEnd + " should be returned", days.get(afterEnd) );
    }

    @Test
    public void testAll() {
        DateRange range = new DateRange( TestConstantsAndHelpers.SOME_DATE, TestConstantsAndHelpers.SOME_DATE.plusDays(3));
        final CalendarData days = new CalendarData();

        assertTrue("Calendar should be empty", days.allAsList().isEmpty());

        generateDays(days, range);

        this.testRangeMatchesDays(days.allAsList(), range);

        // extend and see if iterator still matches range
        range = new DateRange(range.getStart().minusDays(1), range.getEnd().plusDays(1));
        generateDays(days, range);
        this.testRangeMatchesDays(days.allAsList(), range);
    }

    private void testRangeMatchesDays(LinkedList<Day> days, DateRange expectedRange) {
        assertTrue("Start date should match", days.getFirst().getDate().isEqual(expectedRange.getStart()));
        assertTrue("End date should match", days.getLast().getDate().isEqual(expectedRange.getEnd()));
    }


    @Test
    public void testGetMissingDates() {
        final CalendarData days = new CalendarData();

        final DateRange rangeExpected = new DateRange( TestConstantsAndHelpers.SOME_DATE, TestConstantsAndHelpers.SOME_DATE.plusDays(4) );

        LinkedList<LocalDate> missing = days.getMissingDates(rangeExpected);
        Collections.sort(missing);

        assertEquals("All dates should be returned", rangeExpected.size(), missing.size());
        assertTrue("First missing date should be range start", rangeExpected.getStart().isEqual( missing.getFirst() ) );
        assertTrue("Last missing date should be range end", rangeExpected.getEnd().isEqual( missing.getLast() ) );

        days.insert( CalendarGeneratorStub.stubDay( TestConstantsAndHelpers.SOME_DATE.plusDays(1) ) );
        days.insert( CalendarGeneratorStub.stubDay( TestConstantsAndHelpers.SOME_DATE.plusDays(3) ) );

        missing = days.getMissingDates(rangeExpected);
        Collections.sort(missing);

        assertEquals("Three dates should be returned", 3, missing.size());
        assertTrue("First missing date should be range start", rangeExpected.getStart().isEqual( missing.getFirst() ) );
        assertTrue("Second missing date should 2 days ahead of range start", TestConstantsAndHelpers.SOME_DATE.plusDays(2).isEqual( missing.get(1) ) );
        assertTrue("Last missing date should be range end", rangeExpected.getEnd().isEqual( missing.getLast() ) );

        days.insert( CalendarGeneratorStub.stubDay( TestConstantsAndHelpers.SOME_DATE ) );
        days.insert( CalendarGeneratorStub.stubDay( TestConstantsAndHelpers.SOME_DATE.plusDays(2) ) );
        days.insert( CalendarGeneratorStub.stubDay( TestConstantsAndHelpers.SOME_DATE.plusDays(4) ) );

        missing = days.getMissingDates(rangeExpected);
        Collections.sort(missing);

        assertEquals("No dates should be returned, if no dates are missing", 0, missing.size());
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
        assertEquals("2 days should be deleted", 2, removed.size());

        assertTrue("Should remove first", removed.get(0).getDate().isEqual(initialRange.getStart()));

        assertTrue("Should remove second", removed.get(1).getDate().isEqual(initialRange.getStart().plusDays(1)));

        LinkedList<Day> days = calendarData.allAsList();

        assertTrue("Calendar should start at new range",
                days.getFirst().getDate().isEqual(laterStart) );

        assertTrue("Calendar should still end at old range", days.getLast().getDate().isEqual(initialRange.getEnd()));
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
        assertEquals("2 days should be deleted", 2, removed.size());

        assertTrue("Should remove day before last", removed.get(0).getDate().isEqual( initialRange.getEnd().minusDays(1) ) );

        assertTrue("Should remove last", removed.get(1).getDate().isEqual( initialRange.getEnd() ) );

        LinkedList<Day> days = calendarData.allAsList();

        assertTrue("Calendar should still start at old range",
                days.getFirst().getDate().isEqual( initialRange.getStart() ) );

        assertTrue("Calendar should end at new end", days.getLast().getDate().isEqual(earlierEnd) );
    }

    private void generateDays(CalendarData days, DateRange rangeToGenerate) {
        for (LocalDate date : rangeToGenerate) {
            final Day day = CalendarGeneratorStub.stubDay(date);
            days.insert(day);
        }
    }
}
