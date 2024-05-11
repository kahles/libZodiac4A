package de.kah2.zodiac.libZodiac.planetary;

import de.kah2.zodiac.nova4jmt.JulianDay;
import de.kah2.zodiac.nova4jmt.api.LnDate;
import de.kah2.zodiac.nova4jmt.api.LnRstTime;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * This class contains rise- and set-times managed as {@link Instant}s.
 *
 * @author kahles
 */
public class RiseSet {

	private final Instant rise, set;

	/**
	 * This constructor is used when rise and set times are calculated by
	 * libNova, which returns a {@link LnRstTime} object containing times in
	 * UTC.
	 */
	RiseSet(final LnRstTime lnRstTime) {

		this.rise = this.getInstantFromJulian(lnRstTime.rise);
		this.set = this.getInstantFromJulian(lnRstTime.set);
	}

	/**
	 * This constructor instantiates this class based on {@link Instant}s.
	 * No time zone transformations will be done.
	 * Only needed for testing purposes.
	 * @param rise an UTC rise time
	 * @param set an UTC set time
	 */
	public RiseSet(final Instant rise, final Instant set) {
		this.rise = rise;
		this.set = set;
	}

	public Instant getRise() {
		return this.rise;
	}

	public Instant getSet() {
		return this.set;
	}

	private Instant getInstantFromJulian(final double julianDay) {

		final LnDate date = new LnDate();
		JulianDay.ln_get_date(julianDay, date);

		final ZonedDateTime utcZonedTime = LocalDateTime
				.of(date.years, date.months, date.days, date.hours, date.minutes)
				.atZone(PlanetaryDayData.TIME_ZONE_ID_LIBNOVA);
		return utcZonedTime.toInstant();
	}

	@Override
	public String toString() {
		return "RiseSet[ ^%s, v%s ]".formatted( rise, set );
	}
}
