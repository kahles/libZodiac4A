package de.kah2.libZodiac.zodiac;

/**
 * This class represents the lunar direction within the zodiac circle.
 * 
 * @author kahles
 */
public enum ZodiacDirection {

	ASCENDING, DESCENDING;

	public static ZodiacDirection of(final double lunarLongitude) {
		// Moon is descending from second half of cancer to first half of
		// capricorn.
		if (lunarLongitude >= 105 && lunarLongitude < 285) {
			return DESCENDING;
		} else {
			return ASCENDING;
		}
	}
}
