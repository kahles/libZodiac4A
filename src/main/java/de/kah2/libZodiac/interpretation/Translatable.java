package de.kah2.libZodiac.interpretation;

/** Simple class to set a way to create a translation key at one place. */
public class Translatable {

    /** Returns a key to identify the Interpreter. */
    public String getKey() {
        return Translatable.getKey( getClass().getName() );
    }

    /** Converts a class name like it is obtained from {@link java.lang.Class#getName()} to a shorter version for use as translation key. */
    public static String getKey(String className) {

        final int splitAt = className.lastIndexOf('.') + 1;
        return className.substring(splitAt).replace('$', '_');
    }
}
