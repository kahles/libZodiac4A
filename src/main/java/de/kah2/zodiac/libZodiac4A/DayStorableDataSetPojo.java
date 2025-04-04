package de.kah2.zodiac.libZodiac4A;

import java.time.LocalDate;

import de.kah2.zodiac.libZodiac4A.planetary.PlanetaryDayData;
import de.kah2.zodiac.libZodiac4A.planetary.RiseSet;

/**
 * This class is a basic POJO-implementation of {@link DayStorableDataSet}.
 *
 * @author kahles
 */
public class DayStorableDataSetPojo implements DayStorableDataSet {

	private LocalDate date;
	private RiseSet solarRiseSet;
	private RiseSet lunarRiseSet;

	private double lunarVisibility;
	private double lunarLongitude;

	/**
	 * Empty constructor to allow instantiating this class with database data
	 */
	protected DayStorableDataSetPojo() {
		super();
	}

	/**
	 * Constructor to transform calculated data to storable data
	 * @param day the {@link Day}-object containing the data to be serialized
	 */
	public DayStorableDataSetPojo(final Day day) {
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
	DayStorableDataSetPojo(final LocalDate date) {
		this.date = date;
	}

	@Override
	public LocalDate getDate() {
		return date;
	}

	@Override
	public RiseSet getSolarRiseSet() {
		return this.solarRiseSet;
	}

	@Override
	public RiseSet getLunarRiseSet() {
		return this.lunarRiseSet;
	}

	@Override
	public double getLunarVisibility() {
		return this.lunarVisibility;
	}

	@Override
	public double getLunarLongitude() {
		return this.lunarLongitude;
	}

	protected void setDate( final LocalDate date ) {
		this.date = date;
	}

	protected void setSolarRiseSet( RiseSet solarRiseSet ) {

		this.solarRiseSet = solarRiseSet;
	}

	protected void setLunarRiseSet( RiseSet lunarRiseSet ) {

		this.lunarRiseSet = lunarRiseSet;
	}

	protected void setLunarVisibility( double lunarVisibility ) {

		this.lunarVisibility = lunarVisibility;
	}

	protected void setLunarLongitude( double lunarLongitude ) {

		this.lunarLongitude = lunarLongitude;
	}
}