package de.kah2.zodiac.libZodiac.interpretation;

import org.junit.Test;

import java.util.EnumSet;

import de.kah2.zodiac.libZodiac.CalendarGeneratorStub;
import de.kah2.zodiac.libZodiac.Day;
import de.kah2.zodiac.libZodiac.TestConstantsAndHelpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class InterpreterTest {

    enum TestEnum {A,B,C}

    @Test
    public void testSetQuality() {

        final Day day = CalendarGeneratorStub.stubDay( TestConstantsAndHelpers.SOME_DATE);

        boolean threwRightException = false;

        Interpreter testInterpreter = createInterpreterStub(null);

        try {

            testInterpreter.setDayAndInterpret(day);

        } catch (RuntimeException e) {
            threwRightException = true;
        }

        assertTrue("Should throw RuntimeException when Interpreter returns null", threwRightException);

        testInterpreter = createInterpreterStub(Interpreter.Quality.GOOD);
        testInterpreter.setDayAndInterpret(day);

        assertEquals("Should return correct Quality", Interpreter.Quality.GOOD, testInterpreter.getQuality());
    }

    @Test
    public void testAddAnnotations() {

        final Interpreter<TestEnum> testInterpreter = createInterpreterStub(null);

        testInterpreter.addAnnotation(TestEnum.A);
        testInterpreter.addAnnotation(TestEnum.C);

        final EnumSet<TestEnum> annotations = testInterpreter.getContainedAnnotations();

        assertTrue(annotations.contains(TestEnum.A));
        assertFalse(annotations.contains(TestEnum.B));
        assertTrue(annotations.contains(TestEnum.C));
    }

    @Test
    public void testGetAnnotationCount() {
        final Interpreter<TestEnum> testInterpreter = createInterpreterStub(null);

        assertEquals("Should return 0 if no annotations are set", 0, testInterpreter.getAnnotationCount());

        testInterpreter.addAnnotation(TestEnum.A);
        assertEquals("Should return 1 if one annotation is set", 1, testInterpreter.getAnnotationCount());

        testInterpreter.addAnnotation(TestEnum.B);
        assertEquals("Should return 2 if two annotations are set", 2, testInterpreter.getAnnotationCount());
    }

    @Test
    public void testGetAnnotations() {

        final Interpreter<TestEnum> testInterpreter = createInterpreterStub(null);

        // Interpretion has no annotations
        EnumSet<TestEnum> annonations = testInterpreter.getAnnotations(TestEnum.class);
        assertEquals("Should return empty set", 0, annonations.size());

        // Interpretation has annotations
        testInterpreter.addAnnotation(TestEnum.A);
        annonations = testInterpreter.getAnnotations(TestEnum.class);
        assertEquals("Should return set with one element", 1, annonations.size());
        assertTrue("Should contain A", annonations.contains(TestEnum.A));
    }

    @Test
    public void testGetAnnotationsAsStringArray() {

        final Interpreter<TestEnum> testInterpreter = createInterpreterStub(null);

        // Interpretion has no annotations
        String[] annonations = testInterpreter.getAnnotationsAsStringArray();
        assertEquals("Should return empty array", 0, annonations.length);

        // Interpretation has annotations
        testInterpreter.addAnnotation(TestEnum.A);
        annonations = testInterpreter.getAnnotationsAsStringArray();
        assertEquals("Should return set with one element", 1, annonations.length);
        assertEquals("Should contain A", TestEnum.A.toString(), annonations[0]);
    }

    private static Interpreter<TestEnum> createInterpreterStub(Interpreter.Quality expectedQuality) {

        return new Interpreter<TestEnum>() {

            @Override
            protected Quality doInterpretation() {
                return expectedQuality;
            }
        };
    }
}
