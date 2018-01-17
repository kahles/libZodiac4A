package de.kah2.libZodiac.interpretation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.kah2.libZodiac.Day;
import de.kah2.libZodiac.planetary.LunarPhase;
import de.kah2.libZodiac.planetary.PlanetaryDayData;
import de.kah2.libZodiac.zodiac.ZodiacDayData;
import de.kah2.libZodiac.zodiac.ZodiacDirection;
import de.kah2.libZodiac.zodiac.ZodiacElement;
import de.kah2.libZodiac.zodiac.ZodiacSign;

/**
 * Interpretation class for basic gardening activities. As source I used the
 * book "Vom richtigen Zeitpunkt"/"The Power of Timing" (Johanna Paungger,
 * Thomas Poppe).
 *
 * This is a rewrite of old and very bad code - no guarantee for correctness. =>
 * TODO 50 Check book if this is still right
 *
 * @author kahles
 */
public class GardeningInterpreter extends Interpreter {

	private final Logger log = LoggerFactory.getLogger(GardeningInterpreter.class);

	// Gießen
	public final static String WATER = "water";

	// Jäten umgraben
	public final static String WEED_DIG = "weed / dig";

	// Stecklinge schneiden/Pflanzen umsetzen
	public final static String CUTTINGS_TRANSPLANT = "make cuttings / transplant";

	// kranke Pflanzen beschneiden
	public final static String TRIM_SICK = "trim sick plants";

	// unterirdische Schädlinge bekämpfen
	public final static String SUBTERRESTRIAL_PESTS = "combat subterrestrial pests";

	// oberirdische Schädlinge bekämpfen
	public final static String OVERTERRESTRIAL_PESTS = "combat overterrestrial pests";

	// Schnecken bekämpfen
	public final static String SLUGS = "combat slugs";

	// Rasen mähen
	public final static String GRASS = "cut grass";

	// Obstbäume schneiden
	public final static String FRUIT_TREES = "cut fruit trees";

	// Veredeln
	public final static String GRAFT = "graft";

	public GardeningInterpreter(final Day src) {
		super(src);
	}

	@Override
	public void doInterpretations() {

		this.log.debug("calculate: " + this.getToday().getDate());

		final PlanetaryDayData planetary = this.getToday().getPlanetaryData();
		final ZodiacDayData zodiac = this.getToday().getZodiacData();

		if (zodiac.getElement() == ZodiacElement.WATER) {
			this.addGood(WATER);
		} else if (zodiac.getElement() == ZodiacElement.AIR) {
			this.addBad(WATER);
		}

		if (zodiac.getSign() == ZodiacSign.CANCER) {
			if (planetary.getLunarPhase() == LunarPhase.INCREASING) {
				this.addBest(GRASS);
			} else {
				this.addGood(GRASS);
			}
		} else if (zodiac.getSign() == ZodiacSign.SCORPIO || zodiac.getSign() == ZodiacSign.PISCES) {
			this.addGood(GRASS);
		}

		if (planetary.getLunarPhase() == LunarPhase.INCREASING) {

			if (zodiac.getSign() == ZodiacSign.LEO) {
				this.addWorst(WEED_DIG);
			}

			if (zodiac.getSign() == ZodiacSign.VIRGO) {
				this.addBest(CUTTINGS_TRANSPLANT);
			} else {
				this.addGood(CUTTINGS_TRANSPLANT);
			}

			if (zodiac.getSign() == ZodiacSign.CANCER) {
				this.addBest(OVERTERRESTRIAL_PESTS);
			} else if (zodiac.getSign() == ZodiacSign.GEMINI) {
				this.addGood(OVERTERRESTRIAL_PESTS);
			} else if (zodiac.getSign() == ZodiacSign.SAGITTARIUS) {
				this.addGood(OVERTERRESTRIAL_PESTS);
			}

			if (zodiac.getSign() == ZodiacSign.SCORPIO) {
				this.addGood(SLUGS);
			}

		} else if (planetary.getLunarPhase() == LunarPhase.DECREASING) {

			if (zodiac.getSign() == ZodiacSign.CAPRICORN) {
				this.addBest(WEED_DIG);
			} else if (zodiac.getSign() == ZodiacSign.AQUARIUS) {
				this.addGood(WEED_DIG);
			}

			if (zodiac.getElement() == ZodiacElement.EARTH) {
				this.addGood(SUBTERRESTRIAL_PESTS);
			}
		}

		if (zodiac.getDirection() == ZodiacDirection.DESCENDING) {
			this.addGood(CUTTINGS_TRANSPLANT);
		}

		if (planetary.getDaysUntilNextMaxPhase() < 3) {
			if (planetary.getLunarPhase() == LunarPhase.DECREASING) {
				this.addGood(TRIM_SICK);
			} else if (planetary.getLunarPhase() == LunarPhase.NEW_MOON) {
				this.addBest(TRIM_SICK);
			}
		}

		if (zodiac.getElement() == ZodiacElement.FIRE) {
			if (planetary.getLunarPhase() == LunarPhase.DECREASING
					|| zodiac.getDirection() == ZodiacDirection.DESCENDING) {
				this.addGood(FRUIT_TREES);
			}

			if (planetary.getLunarPhase() == LunarPhase.INCREASING && planetary.getDaysUntilNextMaxPhase() < 8) {
				this.addGood(GRAFT);
			} else if (planetary.getLunarPhase() == LunarPhase.FULL_MOON) {
				this.addBest(GRAFT);
			}
		}
	}
}
