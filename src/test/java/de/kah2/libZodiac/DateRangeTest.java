package de.kah2.libZodiac;

import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DateRangeTest {

	private static final LocalDate SOME_DATE = LocalDate.of(2016, Month.OCTOBER, 10);

	@Test
	public void testEndIsAfterStart() {
		final LocalDate firstDate = LocalDate.of(2016, Month.OCTOBER, 10);
		final LocalDate dateAfterFirst = firstDate.plusDays(3);

		DateRange range = new DateRange(firstDate, dateAfterFirst);
		assertTrue(range.getStart().isBefore(range.getEnd()));

		range = new DateRange(dateAfterFirst, firstDate);
		assertTrue(range.getStart().isBefore(range.getEnd()));
	}

	@Test
	public void testContainsDay() {
		final DateRange range = new DateRange(SOME_DATE, SOME_DATE.plusDays(3));

		assertFalse("Should return false when date before range requested",
				range.contains(range.getStart().minusDays(1)));
		assertFalse("Should return false when date after range requested", range.contains(range.getEnd().plusDays(1)));

		final String rangeStr = range.toString();
		for (final LocalDate date : range) {
			assertTrue("Day " + date + " should be contained in " + rangeStr, range.contains(date));
		}
	}

	@Test
	public void testContains() {
		// equal ranges
		final DateRange a = new DateRange(SOME_DATE, SOME_DATE.plusDays(7));
		DateRange b = a;
		assertTrue(a.contains(b));
		assertTrue(b.contains(a));

		// a fully contains b
		b = new DateRange(a.getStart().plusDays(1), a.getEnd().minusDays(1));
		assertTrue(a.contains(b));
		assertFalse(b.contains(a));

		// a partially contains b
		b = new DateRange(a.getStart().plusDays(1), a.getEnd().plusDays(1));
		assertFalse(a.contains(b));
		assertFalse(b.contains(a));

		// a complete outside of b
		b = new DateRange(a.getEnd().plusDays(1), a.getEnd().plusDays(5));
		assertFalse(a.contains(b));
		assertFalse(b.contains(a));
	}


}
