package de.kah2.zodiac.libZodiac4A.zodiac;

/**
 * This class represents the zodiac signs, which are calculated from the lunar
 * longitude.
 *
 * @author kahles
 */
public enum ZodiacSign {

	/** Ger. Widder */
	ARIES,

	/** Ger. Stier */
	TAURUS,

	/** Ger. Zwillinge */
	GEMINI,

	/** Ger. Krebs */
	CANCER,

	/** Ger. Löwe */
	LEO,

	/** Ger. Jungfrau */
	VIRGO,

	/** Ger. Waage */
	LIBRA,

	/** Ger. Skorpion */
	SCORPIO,

	/** Ger. Schütze */
	SAGITTARIUS,

	/** Ger. Steinbock */
	CAPRICORN,

	/** Ger. Wassermann */
	AQUARIUS,

	/** Ger. Fische */
	PISCES;

	/**
	 * Instantiates a {@link ZodiacSign}-object depending on the lunar longitude.
	 * @throws IllegalArgumentException if longitude isn't between 0 and 360 degrees
	 */
	public static ZodiacSign of(final double lunarLongitude) throws IllegalArgumentException {

		if ( lunarLongitude < 0 || lunarLongitude > 360 ) {
			throw new IllegalArgumentException("Lunar position out of range!");
		}

		final int zodiacSignId = (int) lunarLongitude / 30;
		ZodiacSign sign = switch ( zodiacSignId ) {
			case 0 -> // 0 - 30
					ARIES;
			case 1 -> // 30 - 60
					TAURUS;
			case 2 -> // 60 - 90
					GEMINI;
			case 3 -> // 90 - 120
					CANCER;
			case 4 -> // 120 - 150
					LEO;
			case 5 -> // 150 - 180
					VIRGO;
			case 6 -> // 180 - 210
					LIBRA;
			case 7 -> // 210 - 240
					SCORPIO;
			case 8 -> // 240 - 270
					SAGITTARIUS;
			case 9 -> // 270 - 300
					CAPRICORN;
			case 10 -> // 300 - 330
					AQUARIUS;
			case 11 -> // 330 - 360
					PISCES;
			default -> null;
		};

		return sign;
	}
}
