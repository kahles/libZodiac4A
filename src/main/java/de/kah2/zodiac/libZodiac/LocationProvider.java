package de.kah2.zodiac.libZodiac;

import java.time.ZoneId;

import de.kah2.zodiac.libZodiac.planetary.Position;

/**
 * This interface allows us to stub the functionality {@link Calendar} provides
 * for {@link Day} objects.
 * 
 * @author kahles
 */
public interface LocationProvider {

	ZoneId getTimeZoneId();

	Position getObserverPosition();
}
