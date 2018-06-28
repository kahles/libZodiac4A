package de.kah2.libZodiac.interpretation;

import de.kah2.libZodiac.Day;
import de.kah2.libZodiac.planetary.PlanetaryDayData;
import de.kah2.libZodiac.zodiac.ZodiacDayData;

/**
 * <p>This is the base class for all Interpreters, which contains the logic to interpret zodiac data.</p>
 * <p>To write an own interpreter just extend this class and override {@link #getQuality()}.</p>
 */
public abstract class Interpreter {

	/** The possible quality a Day can have for a selected interpretation. */
	public enum Quality {
		BEST, GOOD, NEUTRAL, BAD, WORST
	}

	private Day today;

	private boolean isNotInterpreted = true;

	private Quality quality;
	private Enum<?> category;

	/**
	 * Sets the day to interpret and also resets interpretation data to ensure correctness.
	 */
	public final void setDay(final Day dayToInterpret) {
		this.today = dayToInterpret;
		this.quality = null;
	}

	/**
	 * <p>Implement this method to do some interpretation and return the resulting {@link Quality} for this day. Use {@link #getToday()} to
	 * access the information available about the actual day. There are also shortcuts {@link #getPlanetary()} and {@link #getZodiac()} for
	 * actual data.</p>
	 * <p>By calling {@link #setCategory(Enum)} it is possible to add additional information. For an example, see
	 * {@link de.kah2.libZodiac.interpretation.Gardening.SowPlantInterpreter}.</p>
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

	/**
	 * Allows to set an additional category for interpreted quality, like e.g. a {@link de.kah2.libZodiac.zodiac.ZodiacElement.PlantPart}.
	 */
	protected final void setCategory(Enum<?> category) {
		this.category = category;
	}

	private void interpretIfNecessary() {

		if (this.isNotInterpreted) {

			this.quality = this.doInterpretation();
			this.isNotInterpreted = false;
		}
	}

	/** Returns the interpreted {@link Quality} of this day. */
	public final Quality getQuality() {

		this.interpretIfNecessary();

		if (this.quality == null) {
			throw new RuntimeException("Interpreter may not return null.");
		}

		return this.quality;
	}

	/** May return additional information for interpreted quality or <code>null</code>, if none is set. */
	public final Enum<?> getCategory() {

		this.interpretIfNecessary();

		return this.category;
	}
}
