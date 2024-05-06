package de.kah2.zodiac.libZodiac.planetary;

import de.kah2.zodiac.libZodiac.LocationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

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

		data.setLunarRiseSet(
			new ZonedRiseSet(
				date.atStartOfDay( locationProvider.getTimeZoneId() ).plusHours( 10 ).toInstant(),
				date.atStartOfDay( locationProvider.getTimeZoneId() ).plusHours( 15 ).toInstant()
			)
		);

		data.setSolarRiseSet(
			new ZonedRiseSet(
				date.atStartOfDay( locationProvider.getTimeZoneId() ).plusHours( 7 ).toInstant(),
				date.atStartOfDay( locationProvider.getTimeZoneId() ).plusHours( 19 ).toInstant()
			)
		);

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