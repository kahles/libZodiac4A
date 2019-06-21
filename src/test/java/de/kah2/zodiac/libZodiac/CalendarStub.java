package de.kah2.zodiac.libZodiac;

/**
 * Simple Subclass of {@link Calendar}, which automatically sets {@link CalendarGeneratorStub} as generator.
 */
public class CalendarStub extends Calendar{

    public CalendarStub(DateRange expectedRange, Scope scope) {
        super(TestConstantsAndHelpers.POSITION_MUNICH, expectedRange, scope);

        final CalendarGenerator generator = new CalendarGeneratorStub(this);

        super.setGenerator( generator );
    }
}
