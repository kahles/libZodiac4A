package de.kah2.libZodiac.interpretation;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.kah2.libZodiac.Day;
import de.kah2.libZodiac.planetary.PlanetaryDayData;

/**
 * This class manages the {@link Interpreter}s, which do the interpretation of
 * corresponding {@link PlanetaryDayData}. {@link Interpreter}s can be added
 * through this method or by calling {@link #addInterpreter(Class)} afterwards.
 * 
 * @author kahles
 */
public class InterpretationDayData {

	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	private final Day myDay;

	private final Map<String, Interpreter> interpreters;

	public InterpretationDayData(final Day myDay) {
		this(myDay, new LinkedHashMap<>());
	}

	// For later ...
	private InterpretationDayData(final Day myDay, final Map<String, Interpreter> interpreters) {
		this.myDay = myDay;
		this.interpreters = interpreters;
	}

	public void runInterpreters() {
		if (!this.areAllInterpretersCalculated()) {
			this.log.debug("calculate: " + this.myDay.getDate());

			for (final Interpreter interpreter : this.interpreters.values()) {
				interpreter.run();
			}
		}
	}

	/**
	 * Adds a new interpreter instance to this DAY. It gets stored with
	 * getClass().getSimpleName() as key - so avoid duplicate class names or
	 * they get overwritten by each other.
	 */
	public void addInterpreter(final Class<? extends Interpreter> interpreterClass) {
		try {
			final Constructor<? extends Interpreter> constructor = interpreterClass.getConstructor(Day.class);
			final Interpreter interpreter = constructor.newInstance(this.getDay());
			final String key = this.getKeyFor(interpreterClass);
			this.interpreters.put(key, interpreter);
		} catch (final Exception e) {
			this.log.error("Couldn't add Interpreter: " + interpreterClass.getName(), e);
		}
	}

	/**
	 * Removes an {@link Interpreter} from the list.
	 */
	public void removeInterpreter(final Class<? extends Interpreter> interpreterClass) {
		final String key = this.getKeyFor(interpreterClass);
		this.interpreters.remove(key);
	}

	/**
	 * @return The interpreter of the given class if available.
	 */
	public Interpreter getInterpreter(final Class<? extends Interpreter> interpreterClass) {
		final String key = this.getKeyFor(interpreterClass);
		return this.interpreters.get(key);
	}

	private String getKeyFor(final Class<?> clazz) {
		return clazz.getName();
	}

	/**
	 * @return A <strong>new</strong> {@link LinkedList} containing the
	 *         interpreters. So changes made to this list won't change this
	 *         object.
	 */
	public Collection<Interpreter> getInterpreters() {
		return new LinkedList<>(this.interpreters.values());
	}

	private boolean areAllInterpretersCalculated() {
		for (final Interpreter interpreter : this.interpreters.values()) {
			if (!interpreter.isCalculated()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @return The {@link Day} this object belongs to.
	 */
	protected Day getDay() {
		return this.myDay;
	}
}
