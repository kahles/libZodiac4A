package de.kah2.zodiac.libZodiac.zodiac;

/**
 * This class represents the lunar direction within the zodiac circle.
 *
 * @author kahles
 */
public enum ZodiacDirection {

	ASCENDING, DESCENDING;

	public static ZodiacDirection of(final ZodiacSign zodiacSign) {

		ZodiacDirection direction = switch ( zodiacSign ) {

			// Moon is ascending from capricorn to taurus
			case CAPRICORN, AQUARIUS, PISCES, ARIES, TAURUS, GEMINI -> ASCENDING;

			// Moon is descending from cancer to scorpio
			case CANCER, LEO, VIRGO, LIBRA, SCORPIO, SAGITTARIUS -> DESCENDING;
		};

		return direction;
	}
}
