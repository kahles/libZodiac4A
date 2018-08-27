package de.kah2.libZodiac.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.bp.LocalDate;

import de.kah2.libZodiac.Calendar;
import de.kah2.libZodiac.DateRange;
import de.kah2.libZodiac.Day;
import de.kah2.libZodiac.TestConstantsAndHelpers;
import de.kah2.libZodiac.interpretation.Gardening;
import de.kah2.libZodiac.interpretation.Interpreter;


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
	public static void run(Class <? extends Interpreter> interpreterClass, LocalDate startDate, int days) {
		/* First step: create a Calendar */

		// This is the way to calculate all data for a specific range:
		// In order to know how far it is until next lunar extreme (full and new
		// moon), all days since last and until next lunar extreme get
		// calculated.
		final DateRange range = new DateRange(startDate, startDate.plusDays(days));
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

		calendar.setInterpreterClass(interpreterClass);

		final CalendarDataStringBuilder converter = new CalendarDataStringBuilder();

		converter.appendCalendarData(calendar);

		for ( final Day day : calendar.getValidDays() ) {
			converter.appendAllDayData(day);
		}

		LOG.info("Result:\n" + converter.toString());
	}

	public static void main(final String[] args) {
		run( Gardening.TrimInterpreter.class, LocalDate.now(), 2 );
	}
}
