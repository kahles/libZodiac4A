package de.kah2.libZodiac.planetary;

import net.sourceforge.novaforjava.api.LnLnlatPosn;

/**
 * This simple class is used to encapsulate geographic or ecliptic positions
 * (latitude and longitude) and provides a more comfortable way of accessing
 * these values than {@link LnLnlatPosn} from novaForJava.
 * 
 * @author kahles
 */
public class Position {

	private final double latitude, longitude;

	public Position(final double lat, final double lng) {
		this.latitude = lat;
		this.longitude = lng;
	}

	public LnLnlatPosn to_LnLnLatPosn() {
		final LnLnlatPosn lnLnLatPosn = new LnLnlatPosn();
		lnLnLatPosn.lat = this.latitude;
		lnLnLatPosn.lng = this.longitude;
		return lnLnLatPosn;
	}

	public double getLatitude() {
		return this.latitude;
	}

	public double getLongitude() {
		return this.longitude;
	}

	public boolean isValid() {
		return !(this.getLatitude() < -90 || this.getLatitude() > 90 || this.getLongitude() < -180
				|| this.getLongitude() > 180);
	}

	@Override
	public String toString() {
		return "lat" + this.latitude + " lng" + this.longitude;
	}
}
