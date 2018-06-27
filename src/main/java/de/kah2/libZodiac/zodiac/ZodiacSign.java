package de.kah2.libZodiac.zodiac;

/**
 * This class represents the zodiac signs, which are calculated from the lunar
 * longitude.
 *
 * @author kahles
 */
public enum ZodiacSign {

	ARIES, TAURUS, GEMINI, CANCER, LEO, VIRGO, LIBRA, SCORPIO, SAGITTARIUS, CAPRICORN, AQUARIUS, PISCES;

	public static ZodiacSign of(final double lunarLongitude) throws IllegalArgumentException {

		if ( lunarLongitude < 0 || lunarLongitude > 360 ) {
			throw new IllegalArgumentException("Lunar position out of range!");
		}

		final int zodiacSignId = (int) lunarLongitude / 30;
		ZodiacSign sign = null;

		switch (zodiacSignId) {
			case 0: // 0 - 30
				sign = ARIES;
				break;
			case 1: // 30 - 60
				sign = TAURUS;
				break;
			case 2: // 60 - 90
				sign = GEMINI;
				break;
			case 3: // 90 - 120
				sign = CANCER;
				break;
			case 4: // 120 - 150
				sign = LEO;
				break;
			case 5: // 150 - 180
				sign = VIRGO;
				break;
			case 6: // 180 - 210
				sign = LIBRA;
				break;
			case 7: // 210 - 240
				sign = SCORPIO;
				break;
			case 8: // 240 - 270
				sign = SAGITTARIUS;
				break;
			case 9: // 270 - 300
				sign = CAPRICORN;
				break;
			case 10: // 300 - 330
				sign = AQUARIUS;
				break;
			case 11: // 330 - 360
				sign = PISCES;
				break;
		}

		return sign;
	}
}
