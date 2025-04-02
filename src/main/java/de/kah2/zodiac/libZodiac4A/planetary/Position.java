package de.kah2.zodiac.libZodiac4A.planetary;

import android.annotation.SuppressLint;

import de.kah2.zodiac.nova4jmt.api.LnLnlatPosn;

/**
 * This simple class is used to encapsulate geographic or ecliptic positions
 * (latitude and longitude) and provides a more comfortable way of accessing
 * these values than {@link LnLnlatPosn} from novaForJava.
 * 
 * @author kahles
 */
public class Position {

	public static final int MIN_LATITUDE = -90;
	public static final int MAX_LATITUDE = 90;
	public static final int MIN_LONGITUDE = -180;
	public static final int MAX_LONGITUDE = 180;

	private double latitude, longitude;

	/**
	 * Constructor.
	 * @param lat the latitude of the position
	 * @param lng the longitude of the position
	 * @throws IllegalArgumentException if values aren't withing allowed range - see {@link #isValid()}
	 */
	public Position(final double lat, final double lng) {
		this.latitude = lat;
		this.longitude = lng;

		if ( !this.isValid() )
			throw new IllegalArgumentException("Values aren't within allowed range: " +
					this.getLatitude() + "," + this.getLongitude());
	}

	LnLnlatPosn to_LnLnLatPosn() {
		final LnLnlatPosn lnLnLatPosn = new LnLnlatPosn();
		lnLnLatPosn.lat = this.latitude;
		lnLnLatPosn.lng = this.longitude;
		return lnLnLatPosn;
	}

	/** returns true if {@link #isValidLatitude(double)} and {@link #isValidLongitude(double)} */
	boolean isValid() {
		return isValidLatitude(this.latitude) && isValidLongitude(this.longitude);
	}

	/**
	 * @param lat a latitude value to check
	 * @return true if latitude is between MIN_LATITUDE and MAX_LATITUDE.
	 */
	public static boolean isValidLatitude(double lat) {
		return !(lat < MIN_LATITUDE || lat > MAX_LATITUDE);
	}

	/**
	 * @param lng a longitude value to check
	 * @return true if longitude is between MIN_LONGITUDE and MAX_LONGITUDE
	 */
	public static boolean isValidLongitude(double lng) {
	 return !(lng < MIN_LONGITUDE || lng > MAX_LONGITUDE );
	 }

	 /** Exports latitude and longitude separated through VALUE_SEPARATOR. */
	@SuppressLint("DefaultLocale")
	@Override
	public String toString() {
		return String.format( "Position[ %.4f, %.4f ]", this.latitude, this.longitude );
	}

	public double getLatitude() {
		return this.latitude;
	}

	public double getLongitude() {
		return this.longitude;
	}

	/**
	 * Sets latitude or throws {@link java.lang.IllegalArgumentException}, if an invalid value was given.
	 * Even if {@link #isValidLatitude(double)} is public we ensure given latitude is valid.
	 * @param lat a latitude to be set
	 */
	public void setLatitude(double lat) {

		if ( isValidLatitude(lat) ) {
			this.latitude = lat;
		} else {
			throw new IllegalArgumentException("Invalid latitude given.");
		}
	}

	/**
	 * Sets longitude or throws {@link java.lang.IllegalArgumentException}, if an invalid value was given.
	 * Even if {@link #isValidLongitude(double)} is public we ensure given longitude is valid.
	 * @param lng a longitude to be set
	 */
	public void setLongitude(double lng) {

		if ( isValidLongitude(lng) ) {
			this.longitude = lng;
		} else {
			throw new IllegalArgumentException("Invalid longitude given.");
		}
	}
}
