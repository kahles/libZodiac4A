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
import static de.kah2.libZodiac.zodiac.ZodiacDirection.ASCENDING;
import static de.kah2.libZodiac.zodiac.ZodiacDirection.DESCENDING;
import static de.kah2.libZodiac.zodiac.ZodiacElement.AIR;
import static de.kah2.libZodiac.zodiac.ZodiacElement.FIRE;
import static de.kah2.libZodiac.zodiac.ZodiacElement.PlantPart.FRUIT;
import static de.kah2.libZodiac.zodiac.ZodiacElement.PlantPart.LEAF;
import static de.kah2.libZodiac.zodiac.ZodiacElement.PlantPart.ROOT;
import static de.kah2.libZodiac.zodiac.ZodiacElement.WATER;
import static de.kah2.libZodiac.zodiac.ZodiacSign.AQUARIUS;
import static de.kah2.libZodiac.zodiac.ZodiacSign.CANCER;
import static de.kah2.libZodiac.zodiac.ZodiacSign.CAPRICORN;
import static de.kah2.libZodiac.zodiac.ZodiacSign.GEMINI;
import static de.kah2.libZodiac.zodiac.ZodiacSign.LEO;
import static de.kah2.libZodiac.zodiac.ZodiacSign.PISCES;
import static de.kah2.libZodiac.zodiac.ZodiacSign.SAGITTARIUS;
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

    /**
     * Graft - Veredeln
     * Source: 135
     */
    public static class GraftInterpreter extends Interpreter {

        @Override
        protected Quality doInterpretation() {

            if ( getPlanetary().getLunarPhase() == INCREASING ) {

                if ( getZodiac().getElement().getPlantPart() == FRUIT && getPlanetary().getDaysUntilNextMaxPhase() < 8) {
                    return BEST;
                } else {
                    return GOOD;
                }
            }

            if ( getZodiac().getDirection() == ASCENDING && getZodiac().getElement().getPlantPart() == FRUIT) {
                return GOOD;
            }

            // TODO check if this can be done in a better way:
            if ( (getPlanetary().getLunarPhase() == DECREASING || getPlanetary().getLunarPhase() == NEW_MOON)
                    && getZodiac().getDirection() == DESCENDING ) {
                return WORST;
            }

            return NEUTRAL;
        }
    }

    /**
     * Trim plants - Pflanzen beschneiden
     * Source: 134
     */
    public static class TrimInterpreter extends Interpreter {

        public enum Category { FRUIT_TREES, SICK_PLANTS }

        @Override
        protected Quality doInterpretation() {

            if ( getPlanetary().getLunarPhase() == INCREASING && getZodiac().getElement().getPlantPart() == LEAF ) {
                return WORST;
            }

            if ( getPlanetary().getDaysUntilNextMaxPhase() < 4 ) {

                if ( getPlanetary().getLunarPhase() == DECREASING ) {

                    addAnnotation( Category.SICK_PLANTS );
                    return GOOD;

                } else if ( getPlanetary().getLunarPhase() == NEW_MOON ) {

                    addAnnotation( Category.SICK_PLANTS );
                    return BEST;
                }
            }

            if (getPlanetary().getLunarPhase() == DECREASING
                    || getZodiac().getDirection() == DESCENDING) {

                if (getZodiac().getElement().getPlantPart() == FRUIT) {
                    addAnnotation( Category.FRUIT_TREES );
                    return BEST;
                }

                return GOOD;
            }

            return BAD;
        }
    }
    
    /**
     * Combat pests - Schädlinge bekämpfen
     * Source: 124
     */
    public static class CombatPestsInterpreter extends Interpreter {

        public enum PestType { OVERTERRESTRIAL, SUBTERRESTRIAL, SLUGS };

        @Override
        protected Quality doInterpretation() {

            if ( getPlanetary().getLunarPhase() == DECREASING ) {

                // (TAURUS, VIRGO or CAPRICORN)
                if ( getZodiac().getElement().getPlantPart() == ROOT ) {

                    addAnnotation( PestType.SUBTERRESTRIAL );
                    return BEST;
                }

                if ( getZodiac().getSign() == CANCER ) {

                    addAnnotation(PestType.OVERTERRESTRIAL);
                    return BEST;
                }

                if ( getZodiac().getSign() == GEMINI || getZodiac().getSign() == SAGITTARIUS) {

                    addAnnotation(PestType.OVERTERRESTRIAL);
                    return GOOD;
                }
            } else if ( getPlanetary().getLunarPhase() == INCREASING && getZodiac().getSign() == SCORPIO) {

                addAnnotation( PestType.SLUGS );
                return BEST;
            }

            return NEUTRAL;
        }
    }

    /**
     * Sow/plant - Säen/pflanzen
     * Source: 116, 138
     */
    public static class SowPlantInterpreter extends Interpreter {

        public enum Plants {

            FRUIT_PLANTS, FLOWERS, LEAFY_VEGETABLES, ROOT_VEGETABLES, GRASS, POTATOES, SALAD;
        }

        @Override
        protected Quality doInterpretation() {

            if ( getPlanetary().getLunarPhase() == INCREASING &&
                    ( getZodiac().getSign() == VIRGO || getZodiac().getSign() == LEO ) ) {
                addAnnotation( Plants.GRASS );
            }

            if (getPlanetary().getLunarPhase() == INCREASING || getZodiac().getDirection() == DESCENDING) {

                switch ( getZodiac().getElement().getPlantPart() ) {

                    case FRUIT: addAnnotation( Plants.FRUIT_PLANTS ); break;

                    case LEAF: addAnnotation( Plants.LEAFY_VEGETABLES ); break;

                    case FLOWER: addAnnotation( Plants.FLOWERS ); break;
                }
            }

            if (getPlanetary().getLunarPhase() == DECREASING) {

                switch ( getZodiac().getElement().getPlantPart() ) {

                    case LEAF: addAnnotation( Plants.SALAD ); break;

                    case ROOT:
                        if ( getPlanetary().getDaysSinceLastMaxPhase() < 7 ) {
                            addAnnotation( Plants.POTATOES );
                        }
                        break;
                }
            }

            if (getPlanetary().getLunarPhase() == DECREASING || getZodiac().getDirection() == DESCENDING) {

                if ( getZodiac().getElement().getPlantPart() == ROOT ) {
                    addAnnotation( Plants.ROOT_VEGETABLES );
                }
            }

            if (getAnnotationCount() > 0) {
                return BEST;
            } else {
                return NEUTRAL;
            }
        }
    }
}
