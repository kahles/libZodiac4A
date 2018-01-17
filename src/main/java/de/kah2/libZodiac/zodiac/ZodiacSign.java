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
		final int zodiacSignId = (int) lunarLongitude / 30;
		ZodiacSign sign;

		switch (zodiacSignId) {
		case 0:
			sign = ARIES;
			break;
		case 1:
			sign = TAURUS;
			break;
		case 2:
			sign = GEMINI;
			break;
		case 3:
			sign = CANCER;
			break;
		case 4:
			sign = LEO;
			break;
		case 5:
			sign = VIRGO;
			break;
		case 6:
			sign = LIBRA;
			break;
		case 7:
			sign = SCORPIO;
			break;
		case 8:
			sign = SAGITTARIUS;
			break;
		case 9:
			sign = CAPRICORN;
			break;
		case 10:
			sign = AQUARIUS;
			break;
		case 11:
			sign = PISCES;
			break;
		default:
			throw new IllegalArgumentException("Lunar position out of range!");
		}

		return sign;
	}
}
