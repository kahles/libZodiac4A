package de.kah2.zodiac.libZodiac;

import de.kah2.zodiac.libZodiac.planetary.Position;

import java.time.ZoneId;

/**
 * This interface allows us to stub the functionality {@link Calendar} provides
 * for {@link Day} objects.
 * 
 * @author kahles
 */
public interface LocationProvider {

	/**
	 * The time zone needed to calculate exact day start times
	 */
	ZoneId getTimeZoneId();

	/**
	 * The geolocation needed for calculating rise and set times
	 */
	Position getObserverPosition();
}
