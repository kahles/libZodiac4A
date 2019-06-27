package de.kah2.zodiac.libZodiac4A;

import org.threeten.bp.ZoneId;

import de.kah2.zodiac.libZodiac4A.planetary.Position;

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
