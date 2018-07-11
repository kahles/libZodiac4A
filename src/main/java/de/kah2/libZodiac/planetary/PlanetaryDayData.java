package de.kah2.libZodiac.planetary;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;

import de.kah2.libZodiac.Calendar;
import de.kah2.libZodiac.DayStorableDataSet;
import de.kah2.libZodiac.LocationProvider;
import net.sourceforge.novaforjava.JulianDay;
import net.sourceforge.novaforjava.api.LnDate;
import net.sourceforge.novaforjava.api.LnLnlatPosn;
import net.sourceforge.novaforjava.api.LnRstTime;
import net.sourceforge.novaforjava.solarsystem.Lunar;
import net.sourceforge.novaforjava.solarsystem.Solar;

/**
 * <p>
 * This class calculates basic planetary data based on novaforjava framework.
 * </p>
 * <p>
 * Data contained in this class is:
 * </p>
 * <ul>
 * <li>{@link Position} lunarPosition, double lunarVisibility,
 * {@link ZonedRiseSet} lunarRiseSet and {@link ZonedRiseSet} solarRiseSet:<br/>
 * This is the basic data calculated in this class, they are calculated by
 * #calculate() and afterwards #isCalculated returns true.</li>
 * <li>{@link LunarPhase} lunarPhase, int daysSinceLastMaxPhase, int
 * daysUntilNextMaxPhase:<br/>
 * These values depend on the "neighbor"-days:<br/>
 * lunar phase is automatically calculated when the neighbors second missing
 * neighbor is set at Day#setNext/Previous<br/>
 * the DAY counters are set by Calendar, when a lunar (half-)CYCLE is
 * initialized.<br/>
 * If these three values are calculated {@link #isComplete()} returns true.</li>
 * </ul>
 *
 * @author kahles
 */
public class PlanetaryDayData {

	/**
	 * Since checks are normally like <code>if (daysSince/Until < ...)</code>, we use {@link Integer#MAX_VALUE} to prevent wrong
	 * interpretations.
	 */
	public static final int DAY_COUNT_NOT_CALCULATED = Integer.MAX_VALUE;

	final static ZoneId TIME_ZONE_ID_LIBNOVA = ZoneId.of("UTC");

	/**
	 * These values are only needed for calculation of the other values. => They
	 * get not persisted.
	 */
	private double julianDateAtDayStart, julianDateAtNoon;

	private double lunarVisibility;
	private ZonedRiseSet lunarRiseSet, solarRiseSet;

	private double lunarLongitude;
	private LunarPhase lunarPhase = null;

	private int daysSinceLastMaxPhase = DAY_COUNT_NOT_CALCULATED;
	private int daysUntilNextMaxPhase = DAY_COUNT_NOT_CALCULATED;

	PlanetaryDayData() {
	}

	public static PlanetaryDayData importFrom(final DayStorableDataSet storedData) {
		final PlanetaryDayData data = new PlanetaryDayData();

		data.setSolarRiseSet(storedData.getSolarRiseSet());
		data.setLunarRiseSet(storedData.getLunarRiseSet());
		data.setLunarVisibility(storedData.getLunarVisibility());
		data.setLunarLongitude(storedData.getLunarLongitude());

		return data;
	}

	/**
	 * This is used to calculate the data for a given date.
	 */
	public static PlanetaryDayData calculateFor(final LocalDate date, final LocationProvider locationProvider) {
		final PlanetaryDayData data = new PlanetaryDayData();

		final ZoneId zoneId = locationProvider.getTimeZoneId();
		final Position observerPosition = locationProvider.getObserverPosition();

		data.calculateJulianDateAtDayStart(date, zoneId);
		data.calculateJulianDateAtNoon(date, zoneId);

		data.calculateSolarRiseSetFor(observerPosition, zoneId);
		data.calculateLunarRiseSetFor(observerPosition, zoneId);
		data.calculateLunarVisibility();
		data.calculateLunarLongitude();

		return data;
	}

	private void calculateJulianDateAtDayStart(final LocalDate date, final ZoneId zoneId) {
		final LocalDateTime dayStart = date.atStartOfDay();
		this.julianDateAtDayStart = localDateToJulianDate(dayStart, zoneId);
	}

	private void calculateJulianDateAtNoon(final LocalDate date, final ZoneId zoneId) {
		final LocalDateTime noon = LocalDateTime.of(date, LocalTime.NOON);
		this.julianDateAtNoon = localDateToJulianDate(noon, zoneId);
	}

	private static double localDateToJulianDate(final LocalDateTime date, final ZoneId zoneId) {

		final ZonedDateTime localZonedDateTime = ZonedDateTime.of(date, zoneId);
		final Instant utcDate = localZonedDateTime.withZoneSameInstant(PlanetaryDayData.TIME_ZONE_ID_LIBNOVA)
				.toInstant();

		final LnDate lnDate = new LnDate();
		JulianDay.ln_get_date_from_UTC_milliseconds(lnDate, utcDate.toEpochMilli());

		return JulianDay.ln_get_julian_day(lnDate);
	}

	/** depends on {@link #julianDateAtNoon} */
	private void calculateLunarLongitude() {
		final LnLnlatPosn position = new LnLnlatPosn();

		Lunar.ln_get_lunar_ecl_coords(this.julianDateAtNoon, position, 0.01);

		this.lunarLongitude = position.lng;
	}

	/** depends on {@link #julianDateAtNoon} */
	private void calculateLunarVisibility() {
		this.lunarVisibility = Lunar.ln_get_lunar_disk(this.julianDateAtNoon);
	}

	/** depends on {@link #julianDateAtDayStart} */
	private void calculateLunarRiseSetFor(final Position position, final ZoneId zoneId) {
		final LnLnlatPosn observerPos = position.to_LnLnLatPosn();

		final LnRstTime lnRstTimes = new LnRstTime();

		// static method: calculates lnRstTimes
		final int resultCode = Lunar.ln_get_lunar_rst(this.julianDateAtDayStart, observerPos, lnRstTimes);

		if (resultCode == 1) {
			// moon is circumpolar
			this.lunarRiseSet = null;
		} else {
			this.lunarRiseSet = new ZonedRiseSet(lnRstTimes, zoneId);
		}
	}

	private void calculateSolarRiseSetFor(final Position position, final ZoneId zoneId) {
		final LnLnlatPosn observerPos = position.to_LnLnLatPosn();

		final LnRstTime lnRstTimes = new LnRstTime();

		// static method: calculates lnRstTimes
		final int resultCode = Solar.ln_get_solar_rst(this.julianDateAtDayStart, observerPos, lnRstTimes);

		if (resultCode == 1) {
			// Sun is circumpolar
			this.solarRiseSet = null;
		} else {
			this.solarRiseSet = new ZonedRiseSet(lnRstTimes, zoneId);
		}
	}

	/**
	 * @return The ecliptic longitude of the Moon.
	 */
	public final double getLunarLongitude() {
		return this.lunarLongitude;
	}

	/**
	 * @return The lunar visibility, which is between 0 (new moon) and 1 (full
	 *         moon).
	 */
	public final double getLunarVisibility() {
		return this.lunarVisibility;
	}

	/**
	 * @return Rise and set of the Moon or null if it is circumpolar and doesn't
	 *         rise/set.
	 */

	public final ZonedRiseSet getLunarRiseSet() {
		return this.lunarRiseSet;
	}

	/**
	 * @return Rise and set of the sun or null if it is circumpolar and doesn't
	 *         rise/set.
	 */
	public final ZonedRiseSet getSolarRiseSet() {
		return this.solarRiseSet;
	}

	/**
	 * Here it's best to be set this from external, because calculation for one
	 * day depends on its previous and next day.
	 */
	public final void setLunarPhase(final LunarPhase phase) {
		this.lunarPhase = phase;
	}

	/**
	 * This is only available if this instance has both neighbors.
	 *
	 * @return The {@link LunarPhase} of this DAY or null, if it can't be
	 *         calculated.
	 */
	public final LunarPhase getLunarPhase() {
		return this.lunarPhase;
	}

	/**
	 * @return Days since last full or new moon or {@link #DAY_COUNT_NOT_CALCULATED}/{@link Integer#MAX_VALUE} if it isn't calculated so
	 *         far.
	 * @see #DAY_COUNT_NOT_CALCULATED !
	 */
	public final int getDaysSinceLastMaxPhase() {
		return this.daysSinceLastMaxPhase;
	}

	/**
	 * This should only be called from {@link Calendar}!
	 *
	 * @param daysSinceLastMaxPhase
	 *            Days since last full or new moon
	 */
	public final void setDaysSinceLastMaxPhase(final int daysSinceLastMaxPhase) {
		this.daysSinceLastMaxPhase = daysSinceLastMaxPhase;
	}

	/**
	 * @return Days until next full or new moon or {@link #DAY_COUNT_NOT_CALCULATED}/{@link Integer#MAX_VALUE} if it isn't calculated so
	 *         far.
	 * @see #DAY_COUNT_NOT_CALCULATED !
	 */
	public final int getDaysUntilNextMaxPhase() {
		return this.daysUntilNextMaxPhase;
	}

	/**
	 * This should only be called from {@link Calendar}!
	 *
	 * @param daysUntilNextMaxPhase
	 *            Days until next full or new moon
	 */
	public final void setDaysUntilNextMaxPhase(final int daysUntilNextMaxPhase) {
		this.daysUntilNextMaxPhase = daysUntilNextMaxPhase;
	}

	/**
	 * @return true if the data depending on neighbors is also calculated. See
	 *         class docs for more information.
	 */
	public boolean isComplete() {
		return this.lunarPhase != null && this.getDaysSinceLastMaxPhase() != -1
				&& this.getDaysUntilNextMaxPhase() != DAY_COUNT_NOT_CALCULATED;
	}

	/** For importing data and testing */
	void setLunarLongitude(final double lunarLongitude) {
		this.lunarLongitude = lunarLongitude;
	}

	/** For importing data and testing */
	void setLunarVisibility(final double lunarVisibility) {
		this.lunarVisibility = lunarVisibility;
	}

	/** For importing data and testing */
	void setLunarRiseSet(final ZonedRiseSet lunarRiseSet) {
		this.lunarRiseSet = lunarRiseSet;
	}

	/** For importing data and testing */
	void setSolarRiseSet(final ZonedRiseSet solarRiseSet) {
		this.solarRiseSet = solarRiseSet;
	}
}
