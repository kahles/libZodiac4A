package de.kah2.libZodiac.interpretation;

import java.util.LinkedHashSet;
import java.util.Set;

import de.kah2.libZodiac.Day;
import de.kah2.libZodiac.planetary.PlanetaryDayData;

/**
 * This is the base class for all Interpreters, which contains the logic to
 * interpret zodiac data.<br/>
 * To write an own interpreter just extend this class and override
 * {@link #doInterpretations()} - see documentation of this method.<br/>
 * <strong>Note:</strong> To make the logic of adding activities easier,
 * {@link #addBad(String)} (and {@link #addGood(String)}) only adds activities,
 * when they were not already added via {@link #addWorst(String)} (or
 * {@link #addBest(String)}). If an activity is added via
 * {@link #addBest(String)} and was already added via {@link #addGood(String)}
 * it will be removed from the "good-bucket". (Same with
 * {@link #addWorst(String)} and {@link #addBad(String)}).
 * 
 * @author kahles
 */
// TODO 51 restructure this to improve usability - make this activity-oriented?
public abstract class Interpreter {

	private final LinkedHashSet<String> best, good, bad, worst;

	private final Day today;

	private boolean calculated = false;

	/**
	 * Constructs the interpreter.
	 * 
	 * @param dayToInterpret
	 *            The {@link PlanetaryDayData} object on which the
	 *            interpretations are based.
	 */
	public Interpreter(final Day dayToInterpret) {
		this.today = dayToInterpret;
		this.best = new LinkedHashSet<>();
		this.good = new LinkedHashSet<>();
		this.bad = new LinkedHashSet<>();
		this.worst = new LinkedHashSet<>();
	}

	/**
	 * Implement this and add interpretation code here.<br/>
	 * E.g.: <br/>
	 * <code>
	 * if ({@link #getToday()}.getZodiacData.getSomeValue == ...)
	 *		{@link #addBest(String)}
	 * </code>
	 */
	public abstract void doInterpretations();

	/**
	 * Runs {@link #doInterpretations()} if not done so far and if ALL zodiac
	 * data is calculated including lunar PHASE and DAY-counts.
	 * TODO 30 improve calculation check?
	 */
	public final void run() {
		if (!this.isCalculated() && this.today.getPlanetaryData().isComplete()) {
			this.doInterpretations();
			this.calculated = true;
		}
	}

	/**
	 * @return true, if interpretations are complete.
	 */
	public final boolean isCalculated() {
		return this.calculated;
	}

	/**
	 * Implementations of this class should use this to add very good activities
	 * for the given DAY.
	 * 
	 * @param key
	 *            a string key
	 */
	protected final void addBest(final String key) {
		if (this.good.contains(key)) {
			this.good.remove(key);
		}
		this.best.add(key);
	}

	/**
	 * Implementations of this class should use this to add good activities for
	 * the given DAY.
	 * 
	 * @param key
	 *            a string key
	 */
	protected final void addGood(final String key) {
		if (!this.best.contains(key)) {
			this.good.add(key);
		}
	}

	/**
	 * Implementations of this class should use this to add bad activities for
	 * the given DAY.
	 * 
	 * @param key
	 *            a string key
	 */
	protected final void addBad(final String key) {
		if (!this.worst.contains(key)) {
			this.bad.add(key);
		}
	}

	/**
	 * Implementations of this class should use this to add very bad activities
	 * for the given DAY.
	 * 
	 * @param key
	 *            a string key
	 */
	protected final void addWorst(final String key) {
		if (this.bad.contains(key)) {
			this.bad.remove(key);
		}
		this.worst.add(key);
	}

	/**
	 * @return A copy of the internal list containing the string keys of the
	 *         best activities for the given DAY.
	 */
	@SuppressWarnings("unchecked")
	public final Set<String> getBest() {
		return (Set<String>) this.best.clone();
	}

	/**
	 * @return A copy of the internal list containing the string keys of the
	 *         good activities for the given DAY.
	 */
	@SuppressWarnings("unchecked")
	public final Set<String> getGood() {
		return (Set<String>) this.good.clone();
	}

	/**
	 * @return A copy of the internal list containing the string keys of the bad
	 *         activities for the given DAY.
	 */
	@SuppressWarnings("unchecked")
	public final Set<String> getBad() {
		return (Set<String>) this.bad.clone();
	}

	/**
	 * @return A copy of the internal list containing the string keys of the
	 *         worst activities for the given DAY.
	 */
	@SuppressWarnings("unchecked")
	public final Set<String> getWorst() {
		return (Set<String>) this.worst.clone();
	}

	/**
	 * @return The {@link PlanetaryDayData} object on which the interpretations
	 *         are based.
	 */
	public final Day getToday() {
		return this.today;
	}
}
