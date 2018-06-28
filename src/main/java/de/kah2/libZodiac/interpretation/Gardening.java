package de.kah2.libZodiac.interpretation;

import static de.kah2.libZodiac.interpretation.Interpreter.Quality.BAD;
import static de.kah2.libZodiac.interpretation.Interpreter.Quality.BEST;
import static de.kah2.libZodiac.interpretation.Interpreter.Quality.GOOD;
import static de.kah2.libZodiac.interpretation.Interpreter.Quality.NEUTRAL;
import static de.kah2.libZodiac.interpretation.Interpreter.Quality.WORST;
import static de.kah2.libZodiac.planetary.LunarPhase.DECREASING;
import static de.kah2.libZodiac.planetary.LunarPhase.FULL_MOON;
import static de.kah2.libZodiac.planetary.LunarPhase.INCREASING;
import static de.kah2.libZodiac.planetary.LunarPhase.NEW_MOON;
import static de.kah2.libZodiac.zodiac.ZodiacDirection.DESCENDING;
import static de.kah2.libZodiac.zodiac.ZodiacElement.AIR;
import static de.kah2.libZodiac.zodiac.ZodiacElement.EARTH;
import static de.kah2.libZodiac.zodiac.ZodiacElement.FIRE;
import static de.kah2.libZodiac.zodiac.ZodiacElement.WATER;
import static de.kah2.libZodiac.zodiac.ZodiacSign.AQUARIUS;
import static de.kah2.libZodiac.zodiac.ZodiacSign.CANCER;
import static de.kah2.libZodiac.zodiac.ZodiacSign.CAPRICORN;
import static de.kah2.libZodiac.zodiac.ZodiacSign.LEO;
import static de.kah2.libZodiac.zodiac.ZodiacSign.PISCES;
import static de.kah2.libZodiac.zodiac.ZodiacSign.SCORPIO;
import static de.kah2.libZodiac.zodiac.ZodiacSign.VIRGO;

/**
 * <p>This class containes {@link Interpreter}s for basic gardening activities. As source I used the book
 * "Vom richtigen Zeitpunkt" / "The Power of Timing" (Johanna Paungger, Thomas Poppe).</p>
 */
public class Gardening {

    /** Water plants - gießen */
    // TODO Check book if this is correct
    public static class WaterInterpreter extends Interpreter {

        @Override
        protected Quality doInterpretation() {

            if (getZodiac().getElement() == WATER) {
                return GOOD;
            } else if (getZodiac().getElement() == AIR) {
                return BAD;
            }

            return NEUTRAL;
        }
    }

    /** Mow the lawn - Rasen mähen */
    // TODO Check book if this is correct
    public static class MowLawnInterpreter extends Interpreter {

        @Override
        protected Quality doInterpretation() {

            if (getZodiac().getSign() == CANCER) {
                if (getPlanetary().getLunarPhase() == INCREASING) {
                    return BEST;
                } else {
                    return GOOD;
                }
            } else if (getZodiac().getSign() == SCORPIO || getZodiac().getSign() == PISCES) {
                return GOOD;
            }

            return NEUTRAL;
        }
    }

    /** Trim sick plants - Kranke Pflanzen beschneiden */
    // TODO Check book if this is correct
    public static class TrimSickInterpreter extends Interpreter {

        @Override
        protected Quality doInterpretation() {

            if (getPlanetary().getDaysUntilNextMaxPhase() < 3) {
                if (getPlanetary().getLunarPhase() == DECREASING) {
                    return GOOD;
                } else if (getPlanetary().getLunarPhase() == NEW_MOON) {
                    return BEST;
                }
            }

            return NEUTRAL;
        }
    }

    /** weed / dig - Jäten / umgraben */
    // TODO Check book if this is correct
    public static class WeedDigInterpreter extends Interpreter {

        @Override
        protected Quality doInterpretation() {

            if (getPlanetary().getLunarPhase() == INCREASING) {

                    if (getZodiac().getSign() == LEO ) {
                        return WORST;
                    } else {
                        return BAD;
                    }
            }

            if (getPlanetary().getLunarPhase() == DECREASING) {

                if (getZodiac().getSign() == CAPRICORN) {
                    return BEST;
                } else if (getZodiac().getSign() == AQUARIUS) {
                    return GOOD;
                }
            }

            return NEUTRAL;
        }
    }

    /** make cuttings / transplant - Stecklinge schneiden / Pflanzen umsetzen */
    // TODO Check book if this is correct
    public static class CuttingInterpreter extends Interpreter {

        @Override
        protected Quality doInterpretation() {

            if (getPlanetary().getLunarPhase() == INCREASING) {
                if (getZodiac().getSign() == VIRGO) {
                    return BEST;
                } else {
                    return GOOD;
                }
            }

            if (getZodiac().getDirection() == DESCENDING) {
                return GOOD;
            }

            return NEUTRAL;
        }
    }

    /** Graft - Veredeln */
    // TODO Check book if this is correct
    public static class GraftInterpreter extends Interpreter {

        @Override
        protected Quality doInterpretation() {

            if (getZodiac().getElement() == FIRE) {

                if (getPlanetary().getLunarPhase() == INCREASING && getPlanetary().getDaysUntilNextMaxPhase() < 8) {
                    return GOOD;
                } else if (getPlanetary().getLunarPhase() == FULL_MOON) {
                    return BEST;
                }
            }

            return NEUTRAL;
        }
    }

    /** Cut fruit trees - Obstbäume schneiden */
    // TODO Check book if this is correct
    public static class CutFruitTreeInterpreter extends Interpreter {

        @Override
        protected Quality doInterpretation() {

            if (getZodiac().getElement() == FIRE) {

                if (getPlanetary().getLunarPhase() == DECREASING
                        || getZodiac().getDirection() == DESCENDING) {
                    return GOOD;
                }
            }

            return NEUTRAL;
        }
    }



	/** Combat subterrestrial pests - Unterirdische Schädlinge bekämpfen"; */
    // TODO Check book if this is correct
    public static class SubterrestrialPestsInterpreter extends Interpreter {

        @Override
        protected Quality doInterpretation() {

            if (getPlanetary().getLunarPhase() == DECREASING && getZodiac().getElement() == EARTH) {
                return GOOD;
            }

            return NEUTRAL;
        }
    }

	/** Combat overterrestrial pests - Oberirdische Schädlinge bekämpfen */
    // TODO Check book if this is correct
    public static class OverterrestrialPestsInterpreter extends Interpreter {

        @Override
        protected Quality doInterpretation() {
            if (getPlanetary().getLunarPhase() == INCREASING) {

                switch (getZodiac().getSign()) {

                    case CANCER: return BEST;

                    case GEMINI: return GOOD;

                    case SAGITTARIUS: return GOOD;

                }
            }
            
            return NEUTRAL;
        }
    }

	/** Combat slugs - Schnecken bekämpfen */
    // TODO Check book if this is correct
	public static class CombatSlugsInterpreter extends Interpreter {

        @Override
        protected Quality doInterpretation() {

            if (getPlanetary().getLunarPhase() == INCREASING && getZodiac().getSign() == SCORPIO) {
                    return GOOD;
            }

            return NEUTRAL;
        }
    }

    /** Sow/plant - Säen/pflanzen */
    public static class SowPlantInterpreter extends Interpreter {

        @Override
        protected Quality doInterpretation() {

            if (getZodiac().getDirection() == DESCENDING) {

                this.setCategory( getZodiac().getElement().getPlantPart() );

                return GOOD;
            }

            return NEUTRAL;
        }
    }
}
