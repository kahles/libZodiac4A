package de.kah2.libZodiac;

import de.kah2.libZodiac.interpretation.Interpreter;
import de.kah2.libZodiac.interpretation.StubInterpreter;
import de.kah2.libZodiac.planetary.LunarPhase;
import org.junit.Test;

import static de.kah2.libZodiac.TestConstantsAndHelpers.SOME_DATE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DayTest {

	@Test
	public void testInterpreterCalculation() {
		final Day day = CalendarGeneratorStub.stubDay(SOME_DATE);

		day.getInterpretationData().addInterpreter(StubInterpreter.class);

		assertEquals("One interpreter should be set", 1, day.getInterpretationData().getInterpreters().size());

		day.getPlanetaryData().setLunarPhase(LunarPhase.FULL_MOON);
		day.getPlanetaryData().setDaysSinceLastMaxPhase(0);
		day.getPlanetaryData().setDaysUntilNextMaxPhase(0);

		day.getInterpretationData().runInterpreters();

		final Interpreter interpreter = day.getInterpretationData().getInterpreter(StubInterpreter.class);
		assertEquals("Interpreter should be returned again", StubInterpreter.class, interpreter.getClass());
		assertTrue("Interpreter should be calculated", interpreter.isCalculated());

		assertEquals("One interpretation result should exists", 1, interpreter.getBest().size());
		assertEquals("One interpretation result should exists", 1, interpreter.getGood().size());
		assertEquals("One interpretation result should exists", 1, interpreter.getBad().size());
		assertEquals("One interpretation result should exists", 1, interpreter.getWorst().size());
	}
}
