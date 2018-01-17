package de.kah2.libZodiac.example;

import org.threeten.bp.LocalDate;

import de.kah2.libZodiac.Calendar;
import de.kah2.libZodiac.Day;
import de.kah2.libZodiac.interpretation.InterpretationDayData;
import de.kah2.libZodiac.interpretation.Interpreter;
import de.kah2.libZodiac.planetary.PlanetaryDayData;
import de.kah2.libZodiac.zodiac.ZodiacDayData;

/**
 * This is a simple class for testing purposes to transform
 * {@link Calendar}-data to a {@link String} using a {@link StringBuilder}.
 * 
 * @author kahles
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
	 * Adds all available data for a given day to the result.
	 */
	public void appendAllDayData(final Day day) {
		final boolean isToday = day.getDate().isEqual(LocalDate.now());
		if (isToday) {
			this.appendLine("********** TODAY **********");
		}
		this.appendLine("Date:\t\t\t" + day.getDate().getDayOfWeek() + ", " + day.getDate());
		this.appendPlanetaryData(day.getPlanetaryData());
		this.appendZodiacData(day.getZodiacData());
		this.appendInterpretedDayData(day.getInterpretationData());
		if (isToday) {
			this.appendLine("***************************");
		}
		this.builder.append("\n");
	}

	/**
	 * Adds planetary data to the result.
	 */
	public void appendPlanetaryData(final PlanetaryDayData data) {
		this.appendLine("Moon is visible:\t" + Math.round(data.getLunarVisibility() * 1000) / 10.0 + "%");
		this.appendLine("Lunar rise/set:\t\t" + data.getLunarRiseSet());
		this.appendLine("Solar rise/set:\t\t" + data.getSolarRiseSet());

		if (data.getLunarPhase() == null) {
			this.appendLine("Lunar PHASE couldn't be calculated.");
		} else {
			this.appendLine("Lunar PHASE:\t\t" + data.getLunarPhase());
		}

		if (data.getDaysSinceLastMaxPhase() == -1) {
			this.appendLine("Day count since last extreme isn't available.");
		} else {
			this.appendLine("Days since full/new:\t" + data.getDaysSinceLastMaxPhase());
		}

		if (data.getDaysUntilNextMaxPhase() == -1) {
			this.appendLine("Day count until next extreme isn't available.");
		} else {
			this.appendLine("Days until full/new:\t" + data.getDaysUntilNextMaxPhase());
		}
	}

	/**
	 * Adds zodiac data to the result.
	 */
	public void appendZodiacData(final ZodiacDayData data) {
		this.appendLine("Zodiac Sign:\t\t" + data.getSign());
		this.appendLine("Direction in Zodiac:\t" + data.getDirection());
		this.appendLine("Zodiac element:\t\t" + data.getElement());
		this.appendLine("\tDay category:\t" + data.getElement().getDayCategory());
		this.appendLine("\tPlant part:\t" + data.getElement().getPlantPart());
		this.appendLine("\tFood element:\t" + data.getElement().getFoodElement());
	}

	/**
	 * Adds {@link Interpreter}-data to the result.
	 */
	public void appendInterpretedDayData(final InterpretationDayData data) {
		for (final Interpreter interpreter : data.getInterpreters()) {
			this.appendLine(this.formatInterpreterData(interpreter));
		}
	}

	private String formatInterpreterData(final Interpreter interpreter) {

		String result = interpreter.getClass().getSimpleName() + ":";

		result += "\n\tbest:\t\t" + String.join(", ", interpreter.getBest());
		result += "\n\tgood:\t\t" + String.join(", ", interpreter.getGood());
		result += "\n\tbad:\t\t" + String.join(", ", interpreter.getBad());
		result += "\n\tworst:\t\t" + String.join(", ", interpreter.getWorst());

		return result;
	}

	private void appendLine(final String line) {
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
