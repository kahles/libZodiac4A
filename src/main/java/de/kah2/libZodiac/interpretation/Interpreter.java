package de.kah2.libZodiac.interpretation;

import de.kah2.libZodiac.Day;
import de.kah2.libZodiac.planetary.PlanetaryDayData;
import de.kah2.libZodiac.zodiac.ZodiacDayData;

import java.util.HashSet;

/**
 * <p>This is the base class for all Interpreters, which contains the logic to interpret zodiac data.</p>
 * <p>To write an own interpreter just extend this class and override {@link #getQuality()}.</p>
 */
public abstract class Interpreter {

	/** The possible quality a Day can have for a selected interpretation. */
	public enum Quality {

		WORST,
		BAD,
		NEUTRAL,
		GOOD,
		BEST;

		public boolean isBetterThan(Quality other) {
			return this.compareTo(other) > 0;
		}

		public boolean isWorseThan(Quality other) {
			return this.compareTo(other) < 0;
		}
	}

	private Day today;

	private Quality quality = null;

	// Since it seems unnecessarily complicated to store different Enum values in a Set, we keep them as strings.
	private final HashSet<String> annotations = new HashSet<>();

	/**
	 * Sets the day to interpret and runs interpretation.
	 */
	public final void setDayAndInterpret(final Day dayToInterpret) {

		this.today = dayToInterpret;

		this.quality = this.doInterpretation();

		if (this.quality == null) {
			throw new RuntimeException("Interpreter may not return null.");
		}
	}

	/**
	 * <p>Implement this method to do some interpretation and return the resulting {@link Quality} for this day. Use {@link #getToday()} to
	 * access the information available about the actual day. There are also shortcuts {@link #getPlanetary()} and {@link #getZodiac()} for
	 * actual data.</p>
	 * <p>By calling {@link #addAnnotation(Enum)} it is possible to add additional information. For an example, see
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

	/** Returns the interpreted {@link Quality} of this day. */
	public final Quality getQuality() {
		return this.quality;
	}

	/**
	 * Adds an annotation for interpreted quality, like e.g. a {@link de.kah2.libZodiac.interpretation.Gardening.Plants}.
	 * To guarantee a consistent set of annotations and make it easier to translate them, they should be managed in {@link Enum}s.
	 */
	protected final void addAnnotation(Enum<?> annotation) {
		this.annotations.add( annotation.toString() );
	}

	/**
	 * @return Number of active annotations
	 */
	protected final int getAnnotationCount() { return annotations.size(); }

	/**
	 * <p>Annotations are interpreted (additional) information besides {@link Quality}</p>.
	 */
	public final HashSet<String> getAnnotations() {
		return new HashSet<>( this.annotations );
	}
}