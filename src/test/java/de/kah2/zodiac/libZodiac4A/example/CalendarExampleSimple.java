package de.kah2.zodiac.libZodiac4A.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;

import de.kah2.zodiac.libZodiac4A.Calendar;
import de.kah2.zodiac.libZodiac4A.DateRange;
import de.kah2.zodiac.libZodiac4A.Day;
import de.kah2.zodiac.libZodiac4A.LocationProvider;
import de.kah2.zodiac.libZodiac4A.MunichLocationProvider;
import de.kah2.zodiac.libZodiac4A.TestConstantsAndHelpers;
import de.kah2.zodiac.libZodiac4A.interpretation.Gardening;
import de.kah2.zodiac.libZodiac4A.interpretation.Interpreter;

/**
 * This class shows basic usage of this framework.
 *
 * @author kahles
 */
public class CalendarExampleSimple {

	private final static Logger LOG = LoggerFactory.getLogger(CalendarExampleSimple.class);

	/**
	 * Runs the example.
	 */
	public static void run(Class <? extends Interpreter<?>> interpreterClass, LocalDate startDate, int days)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		/* First step: create a Calendar */

		// This is the way to calculate all data for a specific range:
		// In order to know how far it is until next lunar extreme (full and new
		// moon), all days since last and until next lunar extreme get
		// calculated.
		final DateRange range = new DateRange(startDate, startDate.plusDays(days));
		final LocationProvider locationProvider = new MunichLocationProvider();
		final Calendar calendar = new Calendar( range, Calendar.Scope.CYCLE, locationProvider );

		// If we don't need to know how far away next/previous lunar extreme is,
		// we could reduce the Scope (and calculation time):
		// Only 3 days get calculated: actual, previous and next
		// final Calendar calendar = new Calendar( range, Calendar.Scope.PHASE, locationProvider );

		// And if we don't even need lunar PHASE:
		// final Calendar calendar = new Calendar( range, Calendar.Scope.DAY, locationProvider )

		/*
		 * Second step: Initialize its "main" content. This could take some time
		 * for larger Calendars - so this could be run by a background thread
		 */

		LOG.info( "Generating Calendar for DateRange: {}", range );
		TestConstantsAndHelpers.generateAndWaitFor(calendar);

		final CalendarDataStringBuilder builder = new CalendarDataStringBuilder();

		builder.appendCalendarData( locationProvider );

		for ( final Day day : calendar.getValidDays() ) {

			final boolean isToday = day.getDate().isEqual(LocalDate.now());
			if (isToday) {
				builder.appendLine("************************** TODAY **************************");
			}
			builder.appendLine("Date:\t\t\t\t\t" + day.getDate().getDayOfWeek() + ", " + day.getDate());
			builder.appendPlanetaryData(day.getPlanetaryData());
			builder.appendZodiacData(day.getZodiacData());

			final Interpreter<?> interpreter = interpreterClass.getDeclaredConstructor().newInstance();
			interpreter.setDayAndInterpret(day);
			builder.appendInterpretation(interpreter);

			if (isToday) {
				builder.appendLine("***********************************************************");
			}

			builder.appendLine("");
		}

		LOG.info( "Result:\n{}", builder );
	}

	public static void main(final String[] args) {
		try {
			run( Gardening.TrimInterpreter.class, LocalDate.now(), 2 );
		}
		catch (Exception e) {
			// This shouldn't happen
			LOG.error( "Unexpcected error", e );
		}
	}
}
