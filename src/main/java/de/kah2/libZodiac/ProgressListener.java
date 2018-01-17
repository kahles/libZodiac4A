package de.kah2.libZodiac;

import de.kah2.libZodiac.Calendar.Scope;

/**
 * Interface to enable listening to progress updates.
 * 
 * @author kahles
 */
public interface ProgressListener {

	/**
	 * Signals what activity is in progress.
	 */
	enum State {

		/** When importing stored data */
		IMPORTING,

		/** When data got imported and might be ready (but possibly not consistent). */
		IMPORT_FINISHED,

		/** When generating the expected range */
		GENERATING,

		/**
		 * When extending the expected range to last lunar extreme. Occurs only
		 * when scope is {@link Scope#CYCLE}.
		 */
		EXTENDING_PAST,

		/**
		 * When extending the expected range to next lunar extreme. Occurs only
		 * when scope is {@link Scope#CYCLE}.
		 */
		EXTENDING_FUTURE,

		/**
		 * When counting days between lunar extremes Occurs only when scope is
		 * {@link Scope#CYCLE}.
		 */
		COUNTING,

		/** When all is done */
		FINISHED
	}

	/** Is called when a new {@link State} is started. */
	void onStateChanged(State state);

	/**
	 * Is called during calculation to serve progress and date information.
	 * 
	 * @param percent
	 *            Percentage of calculation progress
	 */
	void onCalculationProgress(float percent);
}