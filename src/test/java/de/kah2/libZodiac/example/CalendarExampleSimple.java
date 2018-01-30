package de.kah2.libZodiac.example;

import de.kah2.libZodiac.Calendar;
import de.kah2.libZodiac.DateRange;
import de.kah2.libZodiac.Day;
import de.kah2.libZodiac.TestConstantsAndHelpers;
import de.kah2.libZodiac.interpretation.Interpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.kah2.libZodiac.interpretation.Interpreter.Quality.*;

import java.time.LocalDate;

/**
 * This class shows basic usage of this framework.
 * 
 * @author kahles
 */
public class CalendarExampleSimple {

	static {
		// Uncomment to have detailed output:
		// TestConstantsAndHelpers.enableLogging("trace");
	}

	private final static Logger LOG = LoggerFactory.getLogger(CalendarExampleSimple.class);

	/**
	 * Runs the example.
	 */
	public static void run() {
		/* First step: create a Calendar */

		// This is the way to calculate all data for today and the next two
		// days:
		// In order to know how far it is until next lunar extreme (full and new
		// moon), all days since last and until next lunar extreme get
		// calculated.
		final LocalDate today = LocalDate.now();
		final DateRange range = new DateRange(today, today.plusDays(2));
		final Calendar calendar = new Calendar(TestConstantsAndHelpers.POSITION_MUNICH, range);

		// If we don't need to know how far away next/previous lunar extreme is,
		// we could reduce the Scope (and calculation time):
		// Only 3 days get calculated: actual, previous and next
		// final Calendar calendar = new Calendar(TestConstantsAndHelpers.POSITION_MUNICH, range, Calendar.Scope.PHASE);

		// And if we don't even need lunar PHASE:
		// final Calendar calendar = new Calendar(TestConstantsAndHelpers.POSITION_MUNICH, range, Calendar.Scope.DAY);

		/*
		 * Second step: Initialize its "main" content. This could take some time
		 * for larger Calendars - so this could be run by a background thread
		 */

		LOG.info("Generating Calendar for DateRange: " + range);
		TestConstantsAndHelpers.generateAndWaitFor(calendar);

		calendar.setInterpreterClass(VisibilityInterpreter.class);

		final CalendarDataStringBuilder converter = new CalendarDataStringBuilder();

		converter.appendCalendarData(calendar);

		for ( final Day day : calendar.getValidDays() ) {
			converter.appendAllDayData(day);
		}

		LOG.info("Result:\n" + converter.toString());
	}

	/**
	 * This is a simple example for an {@link Interpreter}.
	 */
	public static class VisibilityInterpreter extends Interpreter {

		@Override
		protected Quality doInterpretation() {
			final double lunarVisibility = getToday().getPlanetaryData().getLunarVisibility();

			if (lunarVisibility < .1) {
				return WORST;
			} else if (lunarVisibility <.3) {
				return BAD;
			} else if (lunarVisibility >.9) {
				return BEST;
			} else if (lunarVisibility >.7) {
				return GOOD;
			} else {
				return NEUTRAL;
			}
		}
	}

	public static void main(final String[] args) {
		run();
	}
}
