package de.kah2.libZodiac.interpretation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class InterpreterTest {

	private final static String KEY = "key";

	@Test
	public void testThatAddGoodDoesntAddIfAlreadyAddedToBest() {
		final Interpreter interpreter = this.createEmptyInterpreter();
		interpreter.addBest(KEY);
		interpreter.addGood(KEY);

		assertTrue(interpreter.getBest().contains(KEY));
		assertFalse(interpreter.getGood().contains(KEY));
	}

	@Test
	public void testThatAddBadDoesntAddIfAlreadyAddedToWorst() {
		final Interpreter interpreter = this.createEmptyInterpreter();
		interpreter.addWorst(KEY);
		interpreter.addBad(KEY);

		assertTrue(interpreter.getWorst().contains(KEY));
		assertFalse(interpreter.getBad().contains(KEY));
	}

	@Test
	public void testThatAddBestRemovesFromGood() {
		final Interpreter interpreter = this.createEmptyInterpreter();

		interpreter.addGood(KEY);
		assertTrue(interpreter.getGood().contains(KEY));

		interpreter.addBest(KEY);

		assertTrue(interpreter.getBest().contains(KEY));
		assertFalse(interpreter.getGood().contains(KEY));
	}

	@Test
	public void testThatAddWorstRemovesFromBad() {
		final Interpreter interpreter = this.createEmptyInterpreter();

		interpreter.addBad(KEY);
		assertTrue(interpreter.getBad().contains(KEY));

		interpreter.addWorst(KEY);

		assertTrue(interpreter.getWorst().contains(KEY));
		assertFalse(interpreter.getBad().contains(KEY));
	}

	private Interpreter createEmptyInterpreter() {
		return new Interpreter(null) {

			@Override
			public void doInterpretations() {
				// we won't need this
			}
		};
	}
}
