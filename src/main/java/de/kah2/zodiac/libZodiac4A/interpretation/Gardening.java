package de.kah2.zodiac.libZodiac4A.interpretation;

import org.threeten.bp.Month;

import de.kah2.zodiac.libZodiac4A.planetary.LunarPhase;

import static de.kah2.zodiac.libZodiac4A.zodiac.ZodiacDirection.ASCENDING;
import static de.kah2.zodiac.libZodiac4A.zodiac.ZodiacDirection.DESCENDING;
import static de.kah2.zodiac.libZodiac4A.zodiac.ZodiacElement.PlantPart.FLOWER;
import static de.kah2.zodiac.libZodiac4A.zodiac.ZodiacElement.PlantPart.FRUIT;
import static de.kah2.zodiac.libZodiac4A.zodiac.ZodiacElement.PlantPart.LEAF;
import static de.kah2.zodiac.libZodiac4A.zodiac.ZodiacElement.PlantPart.ROOT;
import static de.kah2.zodiac.libZodiac4A.zodiac.ZodiacSign.ARIES;
import static de.kah2.zodiac.libZodiac4A.zodiac.ZodiacSign.CANCER;
import static de.kah2.zodiac.libZodiac4A.zodiac.ZodiacSign.CAPRICORN;
import static de.kah2.zodiac.libZodiac4A.zodiac.ZodiacSign.GEMINI;
import static de.kah2.zodiac.libZodiac4A.zodiac.ZodiacSign.LEO;
import static de.kah2.zodiac.libZodiac4A.zodiac.ZodiacSign.SAGITTARIUS;
import static de.kah2.zodiac.libZodiac4A.zodiac.ZodiacSign.SCORPIO;
import static de.kah2.zodiac.libZodiac4A.zodiac.ZodiacSign.VIRGO;

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

            Quality quality = Quality.NEUTRAL;

            switch (getZodiac().getSign()) {

                case ARIES:

                    quality = Quality.BEST;
                    break;

                case PISCES:
                case CANCER:

                    quality = Quality.BAD;
                    break;

                case VIRGO:

                    quality = Quality.WORST;
                    break;

                default:

                    if (getZodiac().getDirection() == ASCENDING) {

                        quality = Quality.GOOD;

                    } else if ( getPlanetary().getLunarPhase() == LunarPhase.INCREASING ) {

                        quality = Quality.BAD;
                    }
                    break;
            }

            // Step 2: Add annotations depending on quality

            if ( quality.isBetterThan( Quality.NEUTRAL) ) {

                addAnnotation(Usage.TO_CONSERVE);

                if ( getPlanetary().getLunarPhase() == LunarPhase.DECREASING ) {
                    addAnnotation( Usage.TO_DRY );
                }

            } else if ( quality.isWorseThan( Quality.NEUTRAL) ) {

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

                return Quality.WORST;
            }

            if (getPlanetary().getLunarPhase() == LunarPhase.NEW_MOON || getPlanetary().getLunarPhase() == LunarPhase.INCREASING) {

                return Quality.BAD;

            } else {

                if ( getZodiac().getSign() == ARIES || getZodiac().getSign() == SAGITTARIUS ) {

                    addAnnotation(Plants.FRUIT_PLANTS);
                    return Quality.BEST;
                }

                if ( getZodiac().getElement().getPlantPart() == LEAF ) {

                    addAnnotation(Plants.FLOWERS);
                    return Quality.BEST;
                }

                return Quality.GOOD;
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

                if ( getZodiac().getSign() == CANCER && getPlanetary().getLunarPhase() == LunarPhase.INCREASING ) {
                    return Quality.BEST;
                } else {
                    return Quality.GOOD;
                }
            }

            return Quality.NEUTRAL;
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
                return Quality.BEST;
            } else if (getZodiac().getElement().getPlantPart() == FLOWER) {
                return Quality.WORST;
            }

            return Quality.NEUTRAL;
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
                return Quality.BEST;
            }

            if (getPlanetary().getLunarPhase() == LunarPhase.INCREASING) {

                if (getZodiac().getSign() == LEO ) {

                    addAnnotation( Actions.DIG );
                    return Quality.WORST;

                } else {

                    return Quality.BAD;
                }
            } else /*  DECREASING */ {

                addAnnotation( Actions.WEED );

                if (getZodiac().getSign() == CAPRICORN) {
                    return Quality.BEST;
                } else {
                    return Quality.GOOD;
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

            if (getPlanetary().getLunarPhase() == LunarPhase.INCREASING || getZodiac().getDirection() == DESCENDING) {
                if (getZodiac().getSign() == VIRGO) {
                    return Quality.BEST;
                } else {
                    return Quality.GOOD;
                }
            }

            return Quality.NEUTRAL;
        }
    }

    /**
     * Graft - Veredeln
     * Source: 135
     */
    public static class GraftInterpreter extends Interpreter {

        @Override
        protected Quality doInterpretation() {

            if ( getPlanetary().getLunarPhase() == LunarPhase.INCREASING || getPlanetary().getLunarPhase() == LunarPhase.FULL_MOON ) {

                if ( getZodiac().getElement().getPlantPart() == FRUIT && getPlanetary().getDaysUntilNextMaxPhase() < 8) {
                    return Quality.BEST;
                } else {
                    return Quality.GOOD;
                }
            }

            if ( getZodiac().getDirection() == ASCENDING ) {

                if (getZodiac().getElement().getPlantPart() == FRUIT) {
                    return Quality.BEST;
                } else {
                    return Quality.GOOD;
                }
            }

            if ( (getPlanetary().getLunarPhase() == LunarPhase.DECREASING || getPlanetary().getLunarPhase() == LunarPhase.NEW_MOON)
                    && getZodiac().getDirection() == DESCENDING ) {
                return Quality.WORST;
            }

            return Quality.NEUTRAL;
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

            if ( getPlanetary().getLunarPhase() == LunarPhase.INCREASING && getZodiac().getElement().getPlantPart() == LEAF ) {
                return Quality.WORST;
            }

            if ( getPlanetary().getDaysUntilNextMaxPhase() < 4 ) {

                if ( getPlanetary().getLunarPhase() == LunarPhase.DECREASING ) {

                    addAnnotation( PlantCategory.SICK_PLANTS );
                    return Quality.GOOD;

                } else if ( getPlanetary().getLunarPhase() == LunarPhase.NEW_MOON ) {

                    addAnnotation( PlantCategory.SICK_PLANTS );
                    return Quality.BEST;
                }
            }

            if (getPlanetary().getLunarPhase() == LunarPhase.DECREASING
                    || getZodiac().getDirection() == DESCENDING) {

                if (getZodiac().getElement().getPlantPart() == FRUIT) {
                    addAnnotation( PlantCategory.FRUIT_TREES );
                    return Quality.BEST;
                }

                return Quality.GOOD;
            }

            return Quality.BAD;
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

            if ( getPlanetary().getLunarPhase() == LunarPhase.DECREASING ) {

                // (TAURUS, VIRGO or CAPRICORN)
                if ( getZodiac().getElement().getPlantPart() == ROOT ) {

                    addAnnotation( PestType.SUBTERRESTRIAL );
                    return Quality.BEST;
                }

                if ( getZodiac().getSign() == CANCER ) {

                    addAnnotation(PestType.OVERTERRESTRIAL);
                    return Quality.BEST;
                }

                if ( getZodiac().getSign() == GEMINI || getZodiac().getSign() == SAGITTARIUS) {

                    addAnnotation(PestType.OVERTERRESTRIAL);
                    return Quality.GOOD;
                }
            } else if ( getPlanetary().getLunarPhase() == LunarPhase.INCREASING && getZodiac().getSign() == SCORPIO) {

                addAnnotation( PestType.SLUGS );
                return Quality.BEST;
            }

            return Quality.NEUTRAL;
        }
    }

    /**
     * Sow/plant - Säen/pflanzen
     * Source: 116, 138
     */
    public static class SowPlantInterpreter extends Interpreter<Plants> {

        @Override
        protected Quality doInterpretation() {

            if ( getPlanetary().getLunarPhase() == LunarPhase.INCREASING &&
                    ( getZodiac().getSign() == VIRGO || getZodiac().getSign() == LEO ) ) {
                addAnnotation( Plants.LAWN );
            }

            if (getPlanetary().getLunarPhase() == LunarPhase.INCREASING || getZodiac().getDirection() == DESCENDING) {

                switch ( getZodiac().getElement().getPlantPart() ) {

                    case FRUIT: addAnnotation( Plants.FRUIT_PLANTS ); break;

                    case LEAF: addAnnotation( Plants.LEAFY_VEGETABLES ); break;

                    case FLOWER: addAnnotation( Plants.FLOWERS ); break;
                }
            }

            if (getPlanetary().getLunarPhase() == LunarPhase.DECREASING) {

                switch ( getZodiac().getElement().getPlantPart() ) {

                    case LEAF: addAnnotation( Plants.SALAD ); break;

                    case ROOT:
                        if ( getPlanetary().getDaysSinceLastMaxPhase() < 7 ) {
                            addAnnotation( Plants.POTATOES );
                        }
                        break;
                }
            }

            if (getPlanetary().getLunarPhase() == LunarPhase.DECREASING || getZodiac().getDirection() == DESCENDING) {

                if ( getZodiac().getElement().getPlantPart() == ROOT ) {
                    addAnnotation( Plants.ROOT_VEGETABLES );
                }
            }

            if (getAnnotationCount() > 0) {
                return Quality.BEST;
            } else {
                return Quality.NEUTRAL;
            }
        }
    }
}
