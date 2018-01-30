package de.kah2.libZodiac.interpretation;

/** Simple class to set a way to create a translation key at one place. */
public class Translatable {

    /** Returns a key to identify the Interpreter. */
    public final String getKey() {
        return this.getClass().getName();
    }
}
