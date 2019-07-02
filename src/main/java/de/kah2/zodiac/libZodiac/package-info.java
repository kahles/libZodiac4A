/**
 * This package contains the main classes of this framework and three
 * sub-packages. Here the most important elements:
 * @see de.kah2.zodiac.libZodiac.Calendar - the main class of this framework. It initialized days and triggers their data calculation.
 * @see de.kah2.zodiac.libZodiac.Day - the "glue" between the other classes.
 * @see de.kah2.zodiac.libZodiac.DayStorableDataSet - A basic class to extend with serialisation functionality.
 * @see de.kah2.zodiac.libZodiac.CalendarGenerator - does the work of handling calculation threads. Allows tweaking thread priority and
 * number of threads.
 * @see de.kah2.zodiac.libZodiac.ProgressListener - implementations of this class can be registered at
 * {@link de.kah2.zodiac.libZodiac.Calendar} to receive progress updates
 * @see de.kah2.zodiac.libZodiac.planetary.PlanetaryDayData - calculates and contains the basic data needed for ...
 * @see de.kah2.zodiac.libZodiac.zodiac.ZodiacDayData - contains enums and classes to manage planetary and zodiac data
 * @see de.kah2.zodiac.libZodiac.interpretation.Interpreter - abstract class to write interpreters
 * @see de.kah2.zodiac.libZodiac.interpretation.Gardening - implementation of {@link de.kah2.zodiac.libZodiac.interpretation.Interpreter}
 * with gardening interpretations
 * see package "examples" in tests for examples on how to use this framework.
 */
package de.kah2.zodiac.libZodiac;