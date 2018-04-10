package de.kah2.libZodiac;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.bp.LocalDate;

import de.kah2.libZodiac.interpretation.Interpreter;
import de.kah2.libZodiac.planetary.PlanetaryDayData;
import de.kah2.libZodiac.zodiac.ZodiacDayData;


/**
 * This class is the "glue" between elements of this framework: <br />
 * It knows its neighbours and keeps the objects containing the actual data and
 * knows about its neighbours.
 *
 * @author kahles
 */
public class Day implements Comparable<Day>{

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final LocalDate date;

	private final PlanetaryDayData planetaryData;
	private final ZodiacDayData zodiacData;

	private Interpreter interpreter;

	/** This constructor is used to create dummy objects for {@link CalendarData#getMissingDates(DateRange)} */
	Day(final LocalDate date) {
		this.date = date;
		this.planetaryData = null;
		this.zodiacData = null;
	}

	/**
	 * This constructor is only for testing purposes not private.
	 * see CalendarStub#stubDay
	 */
	Day(final LocalDate date, final PlanetaryDayData planetaryData) {
		this.date = date;
		this.planetaryData = planetaryData;
		this.zodiacData = new ZodiacDayData(this.planetaryData);
	}

	/**
	 * Creates a Day object and calculates all data.
	 * 
	 * @param locationProvider
	 *            provides information about observer position and time zone
	 *            needed for calculation of rise and set times.
	 * @param date
	 *            the date of the Day to generate
	 */
	public static Day calculateFor(final LocationProvider locationProvider, final LocalDate date) {

		return new Day(date, PlanetaryDayData.calculateFor(date, locationProvider));
	}

	/**
	 * Creates a Day object, imports planetary data and calculates Zodiac data.
	 * 
	 * @param storedData
	 *            a {@link DayStorableDataSet} containing the data to import.
	 */
	public static Day importFrom(final DayStorableDataSet storedData) {

		return new Day(storedData.getDate(), PlanetaryDayData.importFrom(storedData));
	}

	/**
	 * @return {@link ZodiacDayData} containing basic zodiac calendar data
	 *         without interpretations.
	 */
	public ZodiacDayData getZodiacData() {
		return this.zodiacData;
	}

	/**
	 * @return {@link PlanetaryDayData} containing information about e.g. lunar
	 *         phase or solar rise and set.
	 */
	public PlanetaryDayData getPlanetaryData() {
		return this.planetaryData;
	}

	/**
	 * @return the date of the day
	 */
	public LocalDate getDate() {
		return this.date;
	}

	void setInterpreterClass(Class<? extends Interpreter> interpreterClass) {

		try {

			this.interpreter = interpreterClass.newInstance();
			this.interpreter.setDay(this);

		} catch (final Exception e) {

			this.log.error("Couldn't set interpreter: " + interpreterClass.getName(), e);
		}
	}

	/**
	 * @return The active {@link Interpreter} subclass or null, if none was set.
	 */
	public Interpreter getInterpreter() {
		return this.interpreter;
	}

	/**
	 * Compares the dates of two days.
	 * @return 0 if they are equal, -1 if this day is before or 1 if this day is after the other day
	 */
	@Override
	public int compareTo(final Day other) {
		if (this.getDate().isEqual(other.getDate())) {
			return 0;
		} else if (this.getDate().isBefore(other.getDate())) {
			return -1;
		} else {
			return 1;
		}
	}
}
