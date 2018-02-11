package de.kah2.libZodiac.example;

import de.kah2.libZodiac.interpretation.Gardening;
import de.kah2.libZodiac.interpretation.Interpreter;
import de.kah2.libZodiac.interpretation.Translatable;

/**
 * Lists available Interpreters and their keys for adding them to string resources.
 */

public class ListInterpreterKeys {

    static void listKeys(Class<?> groupClass) {
        final Class<?>[] interpreters = groupClass.getDeclaredClasses();

        System.out.println("Group Key: " + Translatable.getKey( groupClass.getName() ) +
                            "\n\tcontains interpreters:");

        for (Class<?> clazz : interpreters) {

            System.out.println( Translatable.getKey( clazz.getName() ) );
        }
    }

    public static void main(String[] args) {

        listKeys(Gardening.class);
    }
}
