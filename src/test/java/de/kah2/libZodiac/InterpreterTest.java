package de.kah2.libZodiac;

import de.kah2.libZodiac.interpretation.Interpreter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class InterpreterTest {

    enum OneCategory {ONE}

    /** Simple Interpreter, that always returns one fix quality and category. */
    private class TestInterpreter extends Interpreter {
        @Override
        protected Interpreter.Quality doInterpretation() {
            setCategory(OneCategory.ONE);
            return Interpreter.Quality.GOOD;
        }
    }

    @Test
    public void testInterpretationIsDoneIfNecessary() {

        TestInterpreter testInterpreter = new TestInterpreter();
        assertEquals( "Quality should be set if getQuality is called.", Interpreter.Quality.GOOD, testInterpreter.getQuality() );

        testInterpreter = new TestInterpreter();
        assertEquals( "Category should be set if getCategory is called.", OneCategory.ONE, testInterpreter.getCategory() );
    }
}
