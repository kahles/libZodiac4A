package de.kah2.libZodiac.interpretation;

import de.kah2.libZodiac.Day;
import de.kah2.libZodiac.planetary.PlanetaryDayData;
import de.kah2.libZodiac.zodiac.ZodiacDayData;

/**
 * <p>This is the base class for all Interpreters, which contains the logic to interpret zodiac data.</p>
 * <p>To write an own interpreter just extend this class and override {@link #getQuality()}.</p>
 */
public abstract class Interpreter extends Translatable {

	/** The possible quality a Day can have for a selected interpretation. */
	public enum Quality {
		BEST, GOOD, NEUTRAL, BAD, WORST
	}

	private Day today;

	private Quality quality;

	/**
	 * Sets the day to interpret and also resets interpretation data to ensure correctness.
	 */
	public final void setDay(final Day dayToInterpret) {
		this.today = dayToInterpret;
		this.quality = null;
	}

	/**
	 * Implement this method to do some interpretation and return the resulting {@link Quality} for this day. Use {@link #getToday()} to
	 * access the information available about the actual day. There are also shortcuts
	 */
	protected abstract Quality doInterpretation();

	/** @return The actual {@link Day} to interpret. */
	protected final Day getToday() {
		return this.today;
	}

	/** A shortcut to access {@link ZodiacDayData} of actual Day available through {@link #getToday()}. */
	protected final ZodiacDayData getZodiac() { return this.today.getZodiacData(); }

	/** A shortcut to access {@link PlanetaryDayData} of actual Day available through {@link #getToday()}. */
	protected final PlanetaryDayData getPlanetary() { return this.today.getPlanetaryData(); }

	/** Returns the interpreted {@link Quality} of this day. */
	public final Quality getQuality() {

		if (this.quality == null) {
			this.quality = this.doInterpretation();

			if (this.quality == null) {
				throw new RuntimeException("Interpreter may not return null.");
			}
		}

		return this.quality;
	}
}
