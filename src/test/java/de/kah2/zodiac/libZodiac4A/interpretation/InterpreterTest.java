package de.kah2.zodiac.libZodiac4A.interpretation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import de.kah2.zodiac.libZodiac4A.CalendarGeneratorStub;
import de.kah2.zodiac.libZodiac4A.Day;
import de.kah2.zodiac.libZodiac4A.TestConstantsAndHelpers;

public class InterpreterTest {

    enum TestEnum {A,B,C}

    @Test
    public void testSetQuality() {

        final Day day = CalendarGeneratorStub.stubDay( TestConstantsAndHelpers.SOME_DATE);

        boolean threwRightException = false;

        Interpreter<?> testInterpreter = createInterpreterStub(null);

        try {

            testInterpreter.setDayAndInterpret(day);

        } catch (RuntimeException e) {
            threwRightException = true;
        }

		assertThat(threwRightException).as("Should throw RuntimeException when Interpreter returns null").isTrue();

        testInterpreter = createInterpreterStub(Interpreter.Quality.GOOD);
        testInterpreter.setDayAndInterpret(day);

		assertThat(testInterpreter.getQuality()).as("Should return correct Quality").isEqualTo(Interpreter.Quality.GOOD);
    }

    @Test
    public void testAddAnnotations() {

        final Interpreter<TestEnum> testInterpreter = createInterpreterStub(null);

        testInterpreter.addAnnotation(TestEnum.A);
        testInterpreter.addAnnotation(TestEnum.C);

        final EnumSet<TestEnum> annotations = testInterpreter.getContainedAnnotations();

		assertThat(annotations.contains(TestEnum.A)).isTrue();
		assertThat(annotations.contains(TestEnum.B)).isFalse();
		assertThat(annotations.contains(TestEnum.C)).isTrue();
    }

    @Test
    public void testGetAnnotationCount() {
        final Interpreter<TestEnum> testInterpreter = createInterpreterStub(null);

		assertThat(testInterpreter.getAnnotationCount()).as("Should return 0 if no annotations are set").isEqualTo(0);

        testInterpreter.addAnnotation(TestEnum.A);
		assertThat(testInterpreter.getAnnotationCount()).as("Should return 1 if one annotation is set").isEqualTo(1);

        testInterpreter.addAnnotation(TestEnum.B);
		assertThat(testInterpreter.getAnnotationCount()).as("Should return 2 if two annotations are set").isEqualTo(2);
    }

    @Test
    public void testGetAnnotations() {

        final Interpreter<TestEnum> testInterpreter = createInterpreterStub(null);

        // Interpretion has no annotations
        EnumSet<TestEnum> annonations = testInterpreter.getAnnotations(TestEnum.class);
		assertThat(annonations.size()).as("Should return empty set").isEqualTo(0);

        // Interpretation has annotations
        testInterpreter.addAnnotation(TestEnum.A);
        annonations = testInterpreter.getAnnotations(TestEnum.class);
		assertThat(annonations.size()).as("Should return set with one element").isEqualTo(1);
		assertThat(annonations.contains(TestEnum.A)).as("Should contain A").isTrue();
    }

    @Test
    public void testGetAnnotationsAsStringArray() {

        final Interpreter<TestEnum> testInterpreter = createInterpreterStub(null);

        // Interpretion has no annotations
        String[] annonations = testInterpreter.getAnnotationsAsStringArray();
		assertThat(annonations.length).as("Should return empty array").isEqualTo(0);

        // Interpretation has annotations
        testInterpreter.addAnnotation(TestEnum.A);
        annonations = testInterpreter.getAnnotationsAsStringArray();
		assertThat(annonations.length).as("Should return set with one element").isEqualTo(1);
		assertThat(annonations[0]).as("Should contain A").isEqualTo(TestEnum.A.toString());
    }

    private static Interpreter<TestEnum> createInterpreterStub(Interpreter.Quality expectedQuality) {

        return new Interpreter<>() {

            @Override
            protected Quality doInterpretation() {
                return expectedQuality;
            }
        };
    }
}
