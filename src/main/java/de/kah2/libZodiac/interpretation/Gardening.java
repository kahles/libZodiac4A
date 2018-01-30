package de.kah2.libZodiac.interpretation;

// TODO optimize imports
import static de.kah2.libZodiac.zodiac.ZodiacElement.*;
import static de.kah2.libZodiac.zodiac.ZodiacSign.*;
import static de.kah2.libZodiac.zodiac.ZodiacDirection.*;
import static de.kah2.libZodiac.planetary.LunarPhase.*;
import static de.kah2.libZodiac.interpretation.Interpreter.Quality.*;

/**
 * <p>This class containes {@link Interpreter}s for basic gardening activities. As source I used the book
 * "Vom richtigen Zeitpunkt" / "The Power of Timing" (Johanna Paungger, Thomas Poppe).</p>
 *
 * <p>This is a rewrite of old and very bad code - no guarantee for correctness. =>
 * TODO Check book if this is still right</p>
 */
public class Gardening extends Translatable {

    /** Water plants - gießen */
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

    /** Cut grass - Rasen mähen */
    public static class GrassInterpreter extends Interpreter {

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
    public class WeedDigInterpreter extends Interpreter {

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
    public class CuttingInterpreter extends Interpreter {

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
    public class GraftInterpreter extends Interpreter {

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
    public class CutFruitTreeInterpreter extends Interpreter {

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
    public class SubterrestrialPestsInterpreter extends Interpreter {

        @Override
        protected Quality doInterpretation() {

            if (getPlanetary().getLunarPhase() == DECREASING && getZodiac().getElement() == EARTH) {
                return GOOD;
            }

            return NEUTRAL;
        }
    }

	/** Combat overterrestrial pests - Oberirdische Schädlinge bekämpfen */
    public class OverterrestrialPestsInterpreter extends Interpreter {

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
	public class CombatSlugsInterpreter extends Interpreter {

        @Override
        protected Quality doInterpretation() {

            if (getPlanetary().getLunarPhase() == INCREASING && getZodiac().getSign() == SCORPIO) {
                    return GOOD;
            }

            return NEUTRAL;
        }
    }
}
