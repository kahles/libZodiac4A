package de.kah2.zodiac.libZodiac.interpretation;

import java.util.EnumSet;

import de.kah2.zodiac.libZodiac.Day;
import de.kah2.zodiac.libZodiac.planetary.PlanetaryDayData;
import de.kah2.zodiac.libZodiac.zodiac.ZodiacDayData;

/**
 * <p>This is the base class for all Interpreters, which contains the logic to interpret zodiac data.</p>
 * <p>To write an own interpreter just extend this class and override {@link #getQuality()}.</p>
 * <p>To get an interpretation, just create the desired Interpreter-Object and pass a calculated {@link Day}-instance to
 * {@link #setDayAndInterpret(Day)}.</p>
 */
public abstract class Interpreter<T extends Enum<T>> {

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

	private EnumSet<T> annotations;

	/**
	 * Sets the day to interpret and runs interpretation.
	 */
	public final void setDayAndInterpret(final Day dayToInterpret) {

		this.today = dayToInterpret;

		this.quality = this.doInterpretation();

		if (this.quality == null) {
			throw new RuntimeException("Bad interpreter - interpreter must not return null.");
		}
	}

	/**
	 * <p>Implement this method to do some interpretation and return the resulting {@link Quality} for this day. Use {@link #getToday()} to
	 * access the information available about the actual day. There are also shortcuts {@link #getPlanetary()} and {@link #getZodiac()} for
	 * actual data.</p>
	 * <p>By calling {@link #addAnnotation(Enum)} it is possible to add additional information. For an example, see
	 * {@link Gardening.SowPlantInterpreter}.</p>
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
	 * Adds an annotation for interpreted quality, like e.g. {@link Gardening.Plants}.
	 */
	protected final void addAnnotation(T annotation) {

		// Since we first know the contained type when we get an object of this type, we initialize the EnumSet here.
		if (this.annotations == null) {
			this.annotations = EnumSet.of(annotation);
		} else {
			this.annotations.add(annotation);
		}
	}

	/**
	 * @return Number of annotations set
	 */
	protected final int getAnnotationCount() {

		if (this.annotations == null)
			return 0;
		else
			return annotations.size();
	}

	/**
	 * @return a copy of the selected annotations to avoid modification from outside
	 */
	public final EnumSet<T> getAnnotations(Class<T> enumClass) {

		if (annotations == null) {
			return EnumSet.noneOf(enumClass);
		}

		return EnumSet.copyOf( this.annotations );
	}

	/**
	 * @return the annotations as Strings
	 */
	public final String[] getAnnotationsAsStringArray() {

		if (annotations == null) {
			return new String[0];
		}

		final String[] result = new String[this.annotations.size()];

		int index=0;
		for (Object o : this.annotations) {
			result[index] = o.toString();
			index++;
		}

		return result;
	}

	/** Only for testing purposes */
	final EnumSet<T> getContainedAnnotations() {
		return this.annotations;
	}
}