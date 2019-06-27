package de.kah2.zodiac.libZodiac4A.zodiac;

/**
 * This class represents the lunar direction within the zodiac circle.
 *
 * TODO_later allow different interpretations of zodiac directions?
 * + https://www.astro.com/astrowiki/de/Maria_Thun#Aufsteigender_und_absteigender_Mond
 * + https://www.astro.com/astrowiki/de/Siderischer_Tierkreis
 *
 * @author kahles
 */
public enum ZodiacDirection {

	ASCENDING, DESCENDING;

	public static ZodiacDirection of(final ZodiacSign zodiacSign) {

		ZodiacDirection direction = null;

		switch (zodiacSign) {

			// Moon is ascending from capricorn to taurus
			case CAPRICORN:
			case AQUARIUS:
			case PISCES:
			case ARIES:
			case TAURUS:
			case GEMINI:
				direction = ASCENDING;
				break;

			// Moon is descending from cancer to scorpio
			case CANCER:
			case LEO:
			case VIRGO:
			case LIBRA:
			case SCORPIO:
			case SAGITTARIUS:
				direction = DESCENDING;
				break;
		}

		return direction;
	}
}
