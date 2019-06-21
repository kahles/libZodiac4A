package de.kah2.zodiac.libZodiac.example;

import de.kah2.zodiac.libZodiac.Calendar;
import de.kah2.zodiac.libZodiac.interpretation.Interpreter;
import de.kah2.zodiac.libZodiac.planetary.PlanetaryDayData;
import de.kah2.zodiac.libZodiac.zodiac.ZodiacDayData;

/**
 * This is a simple class for testing purposes to transform
 * {@link Calendar}-data to a {@link String} using a {@link StringBuilder}.
 */
public class CalendarDataStringBuilder {

	private final StringBuilder builder;

	/**
	 * Instantiates an empty StringBuilder
	 */
	public CalendarDataStringBuilder() {
		this.builder = new StringBuilder();
	}

	/**
	 * Adds Basic {@link Calendar} data to the result.
	 */
	public void appendCalendarData(final Calendar calendar) {
		this.appendLine(
				"Calendar: Observer:\t" + calendar.getObserverPosition() + ", time zone: " + calendar.getTimeZoneId());
	}

	/**
	 * Adds planetary data to the result.
	 */
	public void appendPlanetaryData(final PlanetaryDayData data) {
		this.appendLine("Moon is visible:\t\t" + Math.round(data.getLunarVisibility() * 1000) / 10.0 + "%");
		this.appendLine("Lunar rise/set:\t\t\t" + data.getLunarRiseSet());
		this.appendLine("Solar rise/set:\t\t\t" + data.getSolarRiseSet());

		if (data.getLunarPhase() == null) {
			this.appendLine("Lunar PHASE couldn't be calculated.");
		} else {
			this.appendLine("Lunar PHASE:\t\t\t" + data.getLunarPhase());
		}

		if (data.getDaysSinceLastMaxPhase() == PlanetaryDayData.DAY_COUNT_NOT_CALCULATED) {
			this.appendLine("Day count since last extreme isn't available.");
		} else {
			this.appendLine("Days since full/new:\t" + data.getDaysSinceLastMaxPhase());
		}

		if (data.getDaysUntilNextMaxPhase() == PlanetaryDayData.DAY_COUNT_NOT_CALCULATED) {
			this.appendLine("Day count until next extreme isn't available.");
		} else {
			this.appendLine("Days until full/new:\t" + data.getDaysUntilNextMaxPhase());
		}
	}

	/**
	 * Adds zodiac data to the result.
	 */
	public void appendZodiacData(final ZodiacDayData data) {
		this.appendLine("Zodiac Sign:\t\t\t" + data.getSign());
		this.appendLine("Direction in Zodiac:\t" + data.getDirection());
		this.appendLine("Zodiac element:\t\t\t" + data.getElement());
		this.appendLine("\tDay category:\t\t" + data.getElement().getDayCategory());
		this.appendLine("\tPlant part:\t\t\t" + data.getElement().getPlantPart());
		this.appendLine("\tFood element:\t\t" + data.getElement().getFoodElement());
	}

	public void appendInterpretation( Interpreter interpreter) {

		final String[] annotations = interpreter.getAnnotationsAsStringArray();

		String annotationString = "";

		if ( annotations.length > 0 ) {
			annotationString = " - " + String.join(",", annotations);
		}

		// This is how it is intended to get a Description for an interpretation.
		this.appendLine("Interpretation:\t\t\t" + interpreter.getClass().getSimpleName() + ": "
				+ interpreter.getQuality() + annotationString);
	}

	public void appendLine(final String line) {
		this.builder.append(line).append("\n");
	}

	/**
	 * @return the resulting String.
	 */
	@Override
	public String toString() {
		return this.builder.toString();
	}
}
