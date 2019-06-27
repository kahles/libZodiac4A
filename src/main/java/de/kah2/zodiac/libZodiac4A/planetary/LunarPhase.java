package de.kah2.zodiac.libZodiac4A.planetary;

import de.kah2.zodiac.libZodiac4A.Day;

/**
 * This class represents the lunar phases.
 * 
 * @author kahles
 */
public enum LunarPhase {

	NEW_MOON, INCREASING, FULL_MOON, DECREASING;

	/**
	 * @return The lunar visibility of the given day if this day knows its
	 *         "tomorrow" and "yesterday" needed for comparison. Otherwise
	 *         <code>null</code> is returned.
	 */
	public static LunarPhase of(final Day previous, final Day target, final Day next) {

		final double yesterdaysVisibility = previous.getPlanetaryData().getLunarVisibility();
		final double actualVisibility = target.getPlanetaryData().getLunarVisibility();
		final double tomorrowsVisibility = next.getPlanetaryData().getLunarVisibility();

		if (yesterdaysVisibility < actualVisibility) {
			if (actualVisibility < tomorrowsVisibility) {
				// last < actual < next
				return INCREASING;
			} else {
				// last < actual > next
				return FULL_MOON;
			}
		} else {
			if (actualVisibility < tomorrowsVisibility) {
				// last > actual < next
				return NEW_MOON;
			} else {
				// last > actual > next
				return DECREASING;
			}
		}
	}

	/**
	 * @return returns true if instance is {@link #FULL_MOON} or
	 *         {@link #NEW_MOON}
	 */
	public boolean isLunarExtreme() {
		return this == LunarPhase.NEW_MOON || this == LunarPhase.FULL_MOON;
	}
}
