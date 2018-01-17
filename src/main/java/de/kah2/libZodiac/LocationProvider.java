package de.kah2.libZodiac;

import org.threeten.bp.ZoneId;

import de.kah2.libZodiac.planetary.Position;

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
