package de.kah2.libZodiac.interpretation;

import java.time.Month;

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
import static de.kah2.libZodiac.zodiac.ZodiacElement.PlantPart.FLOWER;
import static de.kah2.libZodiac.zodiac.ZodiacElement.PlantPart.FRUIT;
import static de.kah2.libZodiac.zodiac.ZodiacElement.PlantPart.LEAF;
import static de.kah2.libZodiac.zodiac.ZodiacElement.PlantPart.ROOT;
import static de.kah2.libZodiac.zodiac.ZodiacSign.ARIES;
import static de.kah2.libZodiac.zodiac.ZodiacSign.CANCER;
import static de.kah2.libZodiac.zodiac.ZodiacSign.CAPRICORN;
import static de.kah2.libZodiac.zodiac.ZodiacSign.GEMINI;
import static de.kah2.libZodiac.zodiac.ZodiacSign.LEO;
import static de.kah2.libZodiac.zodiac.ZodiacSign.SAGITTARIUS;
import static de.kah2.libZodiac.zodiac.ZodiacSign.SCORPIO;
import static de.kah2.libZodiac.zodiac.ZodiacSign.VIRGO;

/**
 * <p>This class containes {@link Interpreter}s for basic gardening activities. As source I used the book
 * "Vom richtigen Zeitpunkt" / "The Power of Timing" (Johanna Paungger, Thomas Poppe).</p>
 */
public class Gardening {

    public enum Plants {
        FRUIT_PLANTS, FLOWERS, LEAFY_VEGETABLES, ROOT_VEGETABLES, LAWN, POTATOES, SALAD
    }

    /**
     * Harvest / Ernten
     * Source: 146
     */
    public static class HarvestInterpreter extends Interpreter<HarvestInterpreter.Usage> {

        public enum Usage { TO_DRY, TO_CONSERVE, CONSUME_IMMEDIATELY }

        @Override
        protected Quality doInterpretation() {

            // Step 1: Set Quality

            Quality quality = NEUTRAL;

            switch (getZodiac().getSign()) {

                case ARIES:

                    quality = BEST;
                    break;

                case PISCES:
                case CANCER:

                    quality = BAD;
                    break;

                case VIRGO:

                    quality = WORST;
                    break;

                default:

                    if (getZodiac().getDirection() == ASCENDING) {

                        quality = GOOD;

                    } else if ( getPlanetary().getLunarPhase() == INCREASING ) {

                        quality = BAD;
                    }
                    break;
            }

            // Step 2: Add annotations depending on quality

            if ( quality.isBetterThan(NEUTRAL) ) {

                addAnnotation(Usage.TO_CONSERVE);

                if ( getPlanetary().getLunarPhase() == DECREASING ) {
                    addAnnotation( Usage.TO_DRY );
                }

            } else if ( quality.isWorseThan(NEUTRAL) ) {

                addAnnotation( Usage.CONSUME_IMMEDIATELY );
            }

            return quality;
        }
    }

    /**
     * Fertilize / Düngen
     * Source: 142
     */
    public static class FertilizeInterpreter extends Interpreter<Plants>{

        @Override
        protected Quality doInterpretation() {

            if (getZodiac().getSign() == LEO) {

                return WORST;
            }

            if (getPlanetary().getLunarPhase() == NEW_MOON || getPlanetary().getLunarPhase() == INCREASING) {

                return BAD;

            } else {

                if ( getZodiac().getSign() == ARIES || getZodiac().getSign() == SAGITTARIUS ) {

                    addAnnotation(Plants.FRUIT_PLANTS);
                    return BEST;
                }

                if ( getZodiac().getElement().getPlantPart() == LEAF ) {

                    addAnnotation(Plants.FLOWERS);
                    return BEST;
                }

                return GOOD;
            }
        }
    }

    /**
     * Mow the lawn - Rasen mähen
     * Source: 151, 155, 159
     */
    public static class MowLawnInterpreter extends Interpreter {

        @Override
        protected Quality doInterpretation() {

            if ( getZodiac().getElement().getPlantPart() == LEAF ) {

                if ( getZodiac().getSign() == CANCER && getPlanetary().getLunarPhase() == INCREASING ) {
                    return BEST;
                } else {
                    return GOOD;
                }
            }

            return NEUTRAL;
        }
    }

    /**
     * Water plants - Gießen
     * Source: 120
     */
    public static class WaterInterpreter extends Interpreter {

        @Override
        protected Quality doInterpretation() {

            if (getZodiac().getElement().getPlantPart() == LEAF) {
                return BEST;
            } else if (getZodiac().getElement().getPlantPart() == FLOWER) {
                return WORST;
            }

            return NEUTRAL;
        }
    }

    /**
     * Weed control - Unkrautbekämpfung
     * Source: 132
     */
    public static class WeedControlInterpreter extends Interpreter<WeedControlInterpreter.Actions> {

        public enum Actions { DIG, WEED, WEED_BEFORE_NOON}
        @Override
        protected Quality doInterpretation() {

            if ( getToday().getDate().getMonth() == Month.JUNE && getToday().getDate().getDayOfMonth() == 18 ) {
                addAnnotation(Actions.WEED_BEFORE_NOON);
                return BEST;
            }

            if (getPlanetary().getLunarPhase() == INCREASING) {

                if (getZodiac().getSign() == LEO ) {

                    addAnnotation( Actions.DIG );
                    return WORST;

                } else {

                    return BAD;
                }
            } else /*  DECREASING */ {

                addAnnotation( Actions.WEED );

                if (getZodiac().getSign() == CAPRICORN) {
                    return BEST;
                } else {
                    return GOOD;
                }
            }
        }
    }

    /**
     *  Make cuttings / transplant - Stecklinge schneiden / Pflanzen umsetzen
     *  Source: 123
     */
    public static class CuttingTransplantInterpreter extends Interpreter {

        @Override
        protected Quality doInterpretation() {

            if (getPlanetary().getLunarPhase() == INCREASING || getZodiac().getDirection() == DESCENDING) {
                if (getZodiac().getSign() == VIRGO) {
                    return BEST;
                } else {
                    return GOOD;
                }
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

            if ( getPlanetary().getLunarPhase() == INCREASING || getPlanetary().getLunarPhase() == FULL_MOON ) {

                if ( getZodiac().getElement().getPlantPart() == FRUIT && getPlanetary().getDaysUntilNextMaxPhase() < 8) {
                    return BEST;
                } else {
                    return GOOD;
                }
            }

            if ( getZodiac().getDirection() == ASCENDING ) {

                if (getZodiac().getElement().getPlantPart() == FRUIT) {
                    return BEST;
                } else {
                    return GOOD;
                }
            }

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
    public static class TrimInterpreter extends Interpreter<TrimInterpreter.PlantCategory> {

        public enum PlantCategory { FRUIT_TREES, SICK_PLANTS }

        @Override
        protected Quality doInterpretation() {

            if ( getPlanetary().getLunarPhase() == INCREASING && getZodiac().getElement().getPlantPart() == LEAF ) {
                return WORST;
            }

            if ( getPlanetary().getDaysUntilNextMaxPhase() < 4 ) {

                if ( getPlanetary().getLunarPhase() == DECREASING ) {

                    addAnnotation( PlantCategory.SICK_PLANTS );
                    return GOOD;

                } else if ( getPlanetary().getLunarPhase() == NEW_MOON ) {

                    addAnnotation( PlantCategory.SICK_PLANTS );
                    return BEST;
                }
            }

            if (getPlanetary().getLunarPhase() == DECREASING
                    || getZodiac().getDirection() == DESCENDING) {

                if (getZodiac().getElement().getPlantPart() == FRUIT) {
                    addAnnotation( PlantCategory.FRUIT_TREES );
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
    public static class CombatPestsInterpreter extends Interpreter<CombatPestsInterpreter.PestType> {

        public enum PestType { OVERTERRESTRIAL, SUBTERRESTRIAL, SLUGS }

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
    public static class SowPlantInterpreter extends Interpreter<Plants> {

        @Override
        protected Quality doInterpretation() {

            if ( getPlanetary().getLunarPhase() == INCREASING &&
                    ( getZodiac().getSign() == VIRGO || getZodiac().getSign() == LEO ) ) {
                addAnnotation( Plants.LAWN );
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
