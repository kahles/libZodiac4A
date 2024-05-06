package de.kah2.zodiac.libZodiac4A;

/**
 * Simple Subclass of {@link Calendar}, which automatically sets {@link CalendarGeneratorStub} as generator.
 */
public class CalendarStub extends Calendar{

    public CalendarStub( DateRange expectedRange, Scope scope ) {
        super( expectedRange, scope, new MunichLocationProvider() );

        final CalendarGenerator generator = new CalendarGeneratorStub(this);

        super.setGenerator( generator );
    }
}
