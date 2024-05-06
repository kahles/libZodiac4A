package de.kah2.zodiac.libZodiac4A.example;

import java.time.LocalDate;

import de.kah2.zodiac.libZodiac4A.Calendar;
import de.kah2.zodiac.libZodiac4A.DateRange;
import de.kah2.zodiac.libZodiac4A.Day;
import de.kah2.zodiac.libZodiac4A.MunichLocationProvider;
import de.kah2.zodiac.libZodiac4A.ProgressListener;
import de.kah2.zodiac.libZodiac4A.TestConstantsAndHelpers;
import de.kah2.zodiac.libZodiac4A.planetary.LunarPhase;

/**
 * This example shows how to implement and use a {@link ProgressListener} and
 * also counts and prints lunar cycle lengths, to help improving progress
 * estimation.
 *
 * @author kahles
 */
public class ProgressListenerExample {

	static class SysOutProgressListener implements ProgressListener {

		@Override
		public void onStateChanged(final State state) {
			System.out.println("State is now: " + state);
			if (state == State.FINISHED) {
				System.out.println("Now we can use the Calendar ...");
			}
		}

		@Override
		public void onCalculationProgress(final float percent) {
			System.out.printf("Calculating: %3.2f\n", percent * 100);
		}
	}

	/**
	 * Walks through the given {@link Calendar}, prints the length of each cycle
	 * and calculates the average cycle length.
	 */
	private static void countCycleLengths(final Calendar calendar) {
		final Iterable<Day> days = calendar.getAllDays();

		int lastCount = 0;

		int cycleCount = 0;
		int lengthSum = 0;

		for (final Day day : days) {
			final LunarPhase phase = day.getPlanetaryData().getLunarPhase();
			if (phase != null && phase.isLunarExtreme()) {
				final String dayCountString;

				// at first lunar extreme we get -1 returned, because we don't
				// know the length of the last cycle
				if (lastCount > 0) {
					lengthSum += lastCount;
					cycleCount++;

					dayCountString = String.valueOf(lastCount);
				} else {
					dayCountString = "unknown";
				}
				System.out.println(day.getDate() + " (" + day.getPlanetaryData().getLunarPhase() + ")\t"
						+ "days since last extreme: " + dayCountString);
			}
			lastCount = day.getPlanetaryData().getDaysSinceLastMaxPhase();
		}
		System.out.printf("Average lunar cycle length:\t%2.2f days\n", (float) lengthSum / cycleCount);
	}

	/**
	 * Creates a {@link Calendar}.
	 */
	private static Calendar createFor(final LocalDate start, final LocalDate end) {

		System.out.println("Generating calendar from " + start + " to " + end);

		// Set up a Calendar
		final DateRange range = new DateRange(start, end);
		final Calendar calendar = new Calendar( range, Calendar.Scope.CYCLE, new MunichLocationProvider() );

		// Add a ProgressListenerAdapter
		calendar.addProgressListener(new SysOutProgressListener());

		// Generate the Calendar and watch the ProgressListenerAdapter work ...
		TestConstantsAndHelpers.generateAndWaitFor(calendar);
		countCycleLengths(calendar);

		return calendar;
	}

	/**
	 * Extends a {@link Calendar}.
	 */
	private static void extendFor(final Calendar calendar, final LocalDate start, final LocalDate end) {
		calendar.setRangeExpected(new DateRange(start, end));

		TestConstantsAndHelpers.generateAndWaitFor(calendar);
		countCycleLengths(calendar);
	}

	public static void main(final String[] args) {

		// Get the average length over one year:
		// ProgressListenerExample.createFor(TODAY, TODAY.plusYears(1));

		// Test a "normal" cycle and extend it:
		final Calendar calendar = ProgressListenerExample.createFor( TestConstantsAndHelpers.SOME_DATE, TestConstantsAndHelpers.SOME_DATES_NEXT_EXTREME.plusDays(7));
		ProgressListenerExample.extendFor(calendar, TestConstantsAndHelpers.SOME_DATE, TestConstantsAndHelpers.SOME_DATE.plusDays(15));
		//
		// When we have to extend too much in both directions, estimation is not
		// very good:
		ProgressListenerExample.extendFor(calendar, TestConstantsAndHelpers.SOME_DATE.minusDays(2), TestConstantsAndHelpers.SOME_DATE.plusDays(30));
	}
}
