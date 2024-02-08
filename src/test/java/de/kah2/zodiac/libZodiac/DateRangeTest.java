package de.kah2.zodiac.libZodiac;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

public class DateRangeTest {

	private static final LocalDate SOME_DATE = LocalDate.of(2016, Month.OCTOBER, 10);

	@Test
	public void testEndIsAfterStart() {
		final LocalDate firstDate = LocalDate.of(2016, Month.OCTOBER, 10);
		final LocalDate dateAfterFirst = firstDate.plusDays(3);

		DateRange range = new DateRange(firstDate, dateAfterFirst);
		assertThat(range.getStart().isBefore(range.getEnd())).isTrue();

		range = new DateRange(dateAfterFirst, firstDate);
		assertThat(range.getStart().isBefore(range.getEnd())).isTrue();
	}

	@Test
	public void testContainsDay() {
		final DateRange range = new DateRange(SOME_DATE, SOME_DATE.plusDays(3));

		assertThat(range.contains(range.getStart().minusDays(1))).as("Should return false when date before range requested").isFalse();
		assertThat(range.contains(range.getEnd().plusDays(1))).as("Should return false when date after range requested").isFalse();

		final String rangeStr = range.toString();
		for (final LocalDate date : range) {
			assertThat(range.contains(date)).as("Day " + date + " should be contained in " + rangeStr).isTrue();
		}
	}

	@Test
	public void testContains() {
		// equal ranges
		final DateRange a = new DateRange(SOME_DATE, SOME_DATE.plusDays(7));
		DateRange b = a;
		assertThat(a.contains(b)).isTrue();
		assertThat(b.contains(a)).isTrue();

		// a fully contains b
		b = new DateRange(a.getStart().plusDays(1), a.getEnd().minusDays(1));
		assertThat(a.contains(b)).isTrue();
		assertThat(b.contains(a)).isFalse();

		// a partially contains b
		b = new DateRange(a.getStart().plusDays(1), a.getEnd().plusDays(1));
		assertThat(a.contains(b)).isFalse();
		assertThat(b.contains(a)).isFalse();

		// a complete outside of b
		b = new DateRange(a.getEnd().plusDays(1), a.getEnd().plusDays(5));
		assertThat(a.contains(b)).isFalse();
		assertThat(b.contains(a)).isFalse();
	}

	@Test
	public void testSize() {

		assertThat(new DateRange(SOME_DATE, SOME_DATE).size()).as("DateRange of one day should have size 1").isEqualTo(1);
		assertThat(new DateRange(SOME_DATE, SOME_DATE.plusDays(2)).size()).as("DateRange of three days should have size 3").isEqualTo(3);
	}
}
