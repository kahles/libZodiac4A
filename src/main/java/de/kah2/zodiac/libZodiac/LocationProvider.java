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

	ZoneId getTimeZoneId();

	// TODO add missing docs
	Position getObserverPosition();
}
