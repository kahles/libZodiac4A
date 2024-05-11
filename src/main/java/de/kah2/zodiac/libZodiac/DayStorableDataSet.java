package de.kah2.zodiac.libZodiac;

import de.kah2.zodiac.libZodiac.planetary.RiseSet;

import java.time.LocalDate;

/**
 * This interface is intended for export and import of {@link Day}-data
 * to allow persisting calculated data.
 */
public interface DayStorableDataSet {

	LocalDate getDate();

	RiseSet getSolarRiseSet();

	RiseSet getLunarRiseSet();

	/**
	 * @return the lunar visibility (value between 0 and 1)
	 */
	double getLunarVisibility();

	/**
	 * @return the lunar longitude needed to determine zodiac sign and direction
	 */
	double getLunarLongitude();
}
