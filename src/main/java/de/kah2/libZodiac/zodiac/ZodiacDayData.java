package de.kah2.libZodiac.zodiac;

import de.kah2.libZodiac.planetary.PlanetaryDayData;

/**
 * This class calculates zodiac data based on {@link PlanetaryDayData}.
 *
 * @author kahles
 */
public class ZodiacDayData {

	private final ZodiacSign sign;
	private final ZodiacDirection direction;
	private final ZodiacElement element;

	/**
	 * Calculates the data provided by this class if necessary.
	 */
	public ZodiacDayData(final PlanetaryDayData srcData) {
		this.sign = ZodiacSign.of(srcData.getLunarLongitude());
		this.element = ZodiacElement.of(this.sign);
		this.direction = ZodiacDirection.of(this.sign);
	}

	/**
	 * @return The zodiac sign at noon.
	 */
	public final ZodiacSign getSign() {
		return this.sign;
	}

	/**
	 * @return The zodiac direction (ascending/descending) at noon.
	 */
	public final ZodiacDirection getDirection() {
		return this.direction;
	}

	/**
	 * @return The element (earth, water, fire, air) of t)he zodiac sign.
	 */
	public final ZodiacElement getElement() {
		return this.element;
	}
}