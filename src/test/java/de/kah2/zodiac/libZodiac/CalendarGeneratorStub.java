package de.kah2.zodiac.libZodiac;

import de.kah2.zodiac.libZodiac.planetary.PlanetaryDayData;
import de.kah2.zodiac.libZodiac.planetary.PlanetaryDayDataStub;

import java.time.LocalDate;
import java.util.LinkedList;

/**
 * This is a stub of {@link CalendarGenerator}, which overrides Day creation to allow
 * stubbing of novaforjava.
 * Number of threads is limited to 2 to avoid generation of too much overhead.
 * 
 * @author kahles
 */
public class CalendarGeneratorStub extends CalendarGenerator {

	CalendarGeneratorStub(Calendar calendar) {
		super(calendar);
		this.setMaxThreadCount(2);
	}

	/**
	 * Replaces {@link PlanetaryDayData} with {@link PlanetaryDayDataStub} to
	 * stub libnova.
	 */
	@Override
	Day createCalculatedDay(final LocalDate date) {
		return stubDay(this.getCalendar(), date);
	}

	/**
	 * This creates a day and stubs calculation of {@link PlanetaryDayData}.
	 */
	private static Day stubDay(final LocationProvider locationProvider, final LocalDate date) {

		return new Day(date, PlanetaryDayDataStub.calculateFor(date, locationProvider));
	}

	/**
	 * This creates a day and stubs calculation of {@link PlanetaryDayData}. Uses a default {@link LocationProvider}.
	 */
	public static Day stubDay(final LocalDate date) {
		return stubDay( TestConstantsAndHelpers.LOCATION_PROVIDER, date);
	}

	static LinkedList<DayStorableDataSet> stubDayStorableDataSets(DateRange range) {

		LinkedList<DayStorableDataSet> days = new LinkedList<>();

		for (LocalDate date: range) {
			days.add( new DayStorableDataSet( stubDay(date) ) );
		}

		return days;
	}
}
