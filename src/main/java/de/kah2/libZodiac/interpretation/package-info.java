/**
 * This package contains classes for adding interpretations. The main class is
 * the abstract class {@link de.kah2.libZodiac.interpretation.Interpreter} which
 * is a container for interpretations. Interpretation classes must extend this
 * class so that they can get passed to
 * {@link de.kah2.libZodiac.interpretation.InterpretationDayData}, which
 * instantiates, executes and manages them.
 * 
 * @author kahles
 */
package de.kah2.libZodiac.interpretation;