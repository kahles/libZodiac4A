package de.kah2.libZodiac;

import org.threeten.bp.LocalDate;
import java.util.LinkedList;

import de.kah2.libZodiac.planetary.PlanetaryDayData;
import de.kah2.libZodiac.planetary.PlanetaryDayDataStub;

import static de.kah2.libZodiac.TestConstantsAndHelpers.LOCATION_PROVIDER;

/**
 * This is a stub of {@link CalendarGenerator}, which overrides Day creation to allow
 * stubbing of novaforjava.
 * Number of threads is limited to 2 to avoid generation of too much overhead.
 * 
 * @author kahles
 */
class CalendarGeneratorStub extends CalendarGenerator {

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
	static Day stubDay(final LocationProvider locationProvider, final LocalDate date) {

		return new Day(date, PlanetaryDayDataStub.calculateFor(date, locationProvider));
	}

	/**
	 * This creates a day and stubs calculation of {@link PlanetaryDayData}. Uses a default {@link LocationProvider}.
	 */
	static Day stubDay(final LocalDate date) {
		return stubDay(LOCATION_PROVIDER, date);
	}

	static LinkedList<DayStorableDataSet> stubDayStorableDataSets(DateRange range) {

		LinkedList<DayStorableDataSet> days = new LinkedList<>();

		for (LocalDate date: range) {
			days.add( new DayStorableDataSet( stubDay(date) ) );
		}

		return days;
	}
}
