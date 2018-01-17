/**
 * This package contains the main classes of this framework and three
 * sub-packages:
 * <ul>
 * <li>{@link de.kah2.libZodiac.Calendar} is the main class of this framework.
 * It initialized days and triggers their data calculation.</li>
 * <li>{@link de.kah2.libZodiac.Day} is the "glue" between the other
 * classes.</li>
 * <li>{@link de.kah2.libZodiac.planetary.PlanetaryDayData} calculates and
 * contains the basic data needed for ...</li>
 * <li>... {@link de.kah2.libZodiac.interpretation.InterpretationDayData} which
 * manages a set of {@link de.kah2.libZodiac.interpretation.Interpreter}s to get
 * interpretations for various purposes.</li>
 * <li>{@link de.kah2.libZodiac.zodiac} contains enums and classes to manage
 * planetary and zodiac data</li>
 * <li>{@link de.kah2.libZodiac.interpretation} contains classes needed for
 * interpreting the raw data through
 * {@link de.kah2.libZodiac.interpretation.InterpretationDayData}.</li>
 * <li>{@link de.kah2.libZodiac.example} contains examples of how to use this
 * framework.
 * </ul>
 *
 * @see de.kah2.libZodiac.example for usage of this framework
 *
 * @author kahles
 */
package de.kah2.libZodiac;