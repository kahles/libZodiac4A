package de.kah2.zodiac.libZodiac;

import java.time.LocalDate;

import de.kah2.zodiac.libZodiac.planetary.PlanetaryDayData;
import de.kah2.zodiac.libZodiac.planetary.ZonedRiseSet;

/**
 * This class contains a basic set of essential data defining a {@link Day} and
 * its properties. It is intended to extend this class with functionality for
 * serializing the contained data.
 *
 * @author kahles
 */
public class DayStorableDataSet {

	private LocalDate date;

	private ZonedRiseSet solarRiseSet;
	private ZonedRiseSet lunarRiseSet;
	private double lunarVisibility;
	private double lunarLongitude;

	/**
	 * Empty constructor to allow instantiating this class with database data
	 */
	protected DayStorableDataSet() {
		super();
	}

	/**
	 * Constructor to transform calculated data to storable data
	 * @param day the {@link Day}-object containing the data to be serialized
	 */
	public DayStorableDataSet(final Day day) {
		this.date = day.getDate();
		final PlanetaryDayData data = day.getPlanetaryData();
		this.solarRiseSet = data.getSolarRiseSet();
		this.lunarRiseSet = data.getLunarRiseSet();
		this.lunarVisibility = data.getLunarVisibility();
		this.lunarLongitude = data.getLunarLongitude();
	}

	/**
	 * Constructor for testing purposes.
	 */
	DayStorableDataSet(final LocalDate date) {
		this.date = date;
	}

	public LocalDate getDate() {
		return date;
	}

	public ZonedRiseSet getSolarRiseSet() {
		return this.solarRiseSet;
	}

	public ZonedRiseSet getLunarRiseSet() {
		return this.lunarRiseSet;
	}

	/**
	 * @return the lunar visibility (value between 0 and 1)
	 */
	public double getLunarVisibility() {
		return this.lunarVisibility;
	}

	/**
	 * @return the lunar longitude needed to determine zodiac sign and direction
	 */
	public double getLunarLongitude() {
		return this.lunarLongitude;
	}

	protected void setDate(final LocalDate date) {
		this.date = date;
	}

	protected void setSolarRiseSet(final ZonedRiseSet solarRiseSet) {
		this.solarRiseSet = solarRiseSet;
	}

	protected void setLunarRiseSet(final ZonedRiseSet lunarRiseSet) {
		this.lunarRiseSet = lunarRiseSet;
	}

	protected void setLunarVisibility(final double lunarVisibility) {
		this.lunarVisibility = lunarVisibility;
	}

	protected void setLunarLongitude(final double lunarLongitude) {
		this.lunarLongitude = lunarLongitude;
	}
}