package de.kah2.zodiac.libZodiac.planetary;

import de.kah2.zodiac.libZodiac.LocationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * This class simulates basic planetary data.
 *
 * @author kahles
 */
public class PlanetaryDayDataStub extends PlanetaryDayData {

	private final static int HALF_CYCLE_LENGTH = 5;
	private final static int CYCLE_LENGTH = HALF_CYCLE_LENGTH * 2;

	private final static double FAKE_CALCULATED_LONGITUDE = 154.3;
	private final static Logger LOG = LoggerFactory.getLogger(PlanetaryDayDataStub.class);

	/**
	 * Sets simulated data.
	 */
	public static PlanetaryDayData calculateFor(final LocalDate date, final LocationProvider locationProvider) {

		final PlanetaryDayData data = new PlanetaryDayDataStub();

		data.setLunarLongitude(FAKE_CALCULATED_LONGITUDE);

		data.setLunarVisibility( getFakeLunarVisibility(date) );

		data.setLunarRiseSet( new ZonedRiseSet(
				LocalDateTime.of( date, LocalTime.of(10, 0) ),
				LocalDateTime.of( date, LocalTime.of(15,0) ) ) );
		data.setSolarRiseSet( new ZonedRiseSet(
				LocalDateTime.of( date, LocalTime.of(7, 0) ),
				LocalDateTime.of( date, LocalTime.of(19,0) ) ) );

		return data;
	}

	private static double getFakeLunarVisibility(LocalDate date) {

		final long epochDay = date.toEpochDay();

		int modulo = (int) epochDay % CYCLE_LENGTH;

		int count;

		if (modulo > HALF_CYCLE_LENGTH) {
			count = CYCLE_LENGTH - modulo;
		} else {
			count = modulo;
		}

		double visibility = (double) count / HALF_CYCLE_LENGTH;

		LOG.debug( "{} visibility: {}", date, visibility );

		return visibility;
	}
}