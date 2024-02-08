package de.kah2.zodiac.libZodiac.interpretation;

import de.kah2.zodiac.libZodiac.CalendarGeneratorStub;
import de.kah2.zodiac.libZodiac.Day;
import de.kah2.zodiac.libZodiac.TestConstantsAndHelpers;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        assertTrue(threwRightException, "Should throw RuntimeException when Interpreter returns null");

        testInterpreter = createInterpreterStub(Interpreter.Quality.GOOD);
        testInterpreter.setDayAndInterpret(day);

        assertEquals(Interpreter.Quality.GOOD, testInterpreter.getQuality(), "Should return correct Quality");
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

        assertEquals(0, testInterpreter.getAnnotationCount(), "Should return 0 if no annotations are set");

        testInterpreter.addAnnotation(TestEnum.A);
        assertEquals(1, testInterpreter.getAnnotationCount(), "Should return 1 if one annotation is set");

        testInterpreter.addAnnotation(TestEnum.B);
        assertEquals(2, testInterpreter.getAnnotationCount(), "Should return 2 if two annotations are set");
    }

    @Test
    public void testGetAnnotations() {

        final Interpreter<TestEnum> testInterpreter = createInterpreterStub(null);

        // Interpretion has no annotations
        EnumSet<TestEnum> annonations = testInterpreter.getAnnotations(TestEnum.class);
        assertEquals(0, annonations.size(), "Should return empty set");

        // Interpretation has annotations
        testInterpreter.addAnnotation(TestEnum.A);
        annonations = testInterpreter.getAnnotations(TestEnum.class);
        assertEquals(1, annonations.size(), "Should return set with one element");
        assertTrue(annonations.contains(TestEnum.A), "Should contain A");
    }

    @Test
    public void testGetAnnotationsAsStringArray() {

        final Interpreter<TestEnum> testInterpreter = createInterpreterStub(null);

        // Interpretion has no annotations
        String[] annonations = testInterpreter.getAnnotationsAsStringArray();
        assertEquals(0, annonations.length, "Should return empty array");

        // Interpretation has annotations
        testInterpreter.addAnnotation(TestEnum.A);
        annonations = testInterpreter.getAnnotationsAsStringArray();
        assertEquals(1, annonations.length, "Should return set with one element");
        assertEquals(TestEnum.A.toString(), annonations[0], "Should contain A");
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
