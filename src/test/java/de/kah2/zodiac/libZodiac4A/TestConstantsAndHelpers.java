package de.kah2.zodiac.libZodiac4A;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.bp.LocalDate;
import org.threeten.bp.ZoneId;

import de.kah2.zodiac.libZodiac4A.planetary.Position;

public class TestConstantsAndHelpers {

	public final static Position POSITION_MUNICH = new Position(48.137, 11.57521);

	public final static LocalDate SOME_DATE = LocalDate.of(2016, 9, 3);

	/** Last lunar extreme before SOME_DATE - full moon */
	public final static LocalDate SOME_DATES_LAST_EXTREME = LocalDate.of(2016, 9, 1);

	/** Next lunar extreme after SOME_DATE - new moon */
	public final static LocalDate SOME_DATES_NEXT_EXTREME = LocalDate.of(2016, 9, 6);


	public final static String TIME_ZONE_STRING = "Europe/Berlin";

	public final static LocationProvider LOCATION_PROVIDER = new LocationProvider() {
		@Override
		public ZoneId getTimeZoneId() {
			return ZoneId.of(TIME_ZONE_STRING);
		}

		@Override
		public Position getObserverPosition() {
			return POSITION_MUNICH;
		}
	};

	private final static int sleepStepMs = 1000;

	/**
	 * No functionality at android
	 */
	public static void enableLogging(final String level) {
		LoggerFactory.getLogger(TestConstantsAndHelpers.class)
				.warn("enableLogging() has no functionality on android");
	}

	/** Simple method to generate a {@link Calendar} and wait for results. */
	public static void generateAndWaitFor(CalendarGenerator generator) {

		final Logger log = LoggerFactory.getLogger("TestConstantsAndHelpers#generateAndWaitFor");

		final LastStateProgressListener listener = new LastStateProgressListener();

		generator.getProgressManager().addProgressListener(listener);

		generator.startGeneration();

		while (listener.getLastState() != ProgressListener.State.FINISHED) {
			try {
				log.trace("waiting " + sleepStepMs + "ms for state change to FINISHED");
				Thread.sleep(sleepStepMs);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		generator.getProgressManager().removeProgressListener(listener);
	}

	/**
	 * Simple method to generate a {@link Calendar} and wait for results for a specified time.
	 * @return true, if state reached FINISHED within specified time or false, if operation was cancelled because of
	 * time limit
	 */
	public static boolean generateAndWaitFor(CalendarGenerator generator, int maxWait) {
		final LastStateProgressListener listener = new LastStateProgressListener();

		generator.getProgressManager().addProgressListener(listener);

		generator.startGeneration();

		int msWaited = 0;
		boolean exitedNormally = true;

		while (listener.getLastState() != ProgressListener.State.FINISHED) {

			try {
				Thread.sleep(sleepStepMs);
				msWaited += sleepStepMs;

				if (msWaited > maxWait) {
					int remaining = generator.getExecutor().shutdownNow().size();
					LoggerFactory.getLogger("TestConstantsAndHelpers#generateAndWaitFor(CalendarGenerator,int)")
							.trace("Terminated executor - " + remaining + " jobs were still waiting");
					exitedNormally = false;
					break;
				}
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		generator.getProgressManager().removeProgressListener(listener);
		return exitedNormally;
	}


		/** Simple method to generate a {@link Calendar} and wait for results. */
	public static void generateAndWaitFor(Calendar calendar) {

		final LastStateProgressListener listener = new LastStateProgressListener();

		calendar.addProgressListener(listener);

		calendar.startGeneration();

		while (listener.getLastState() != ProgressListener.State.FINISHED) {
			try {
				Thread.sleep(sleepStepMs);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		calendar.removeProgressListener(listener);
	}

	/**
	 * Simple implementation of {@link ProgressListener} to be able to wait for results.
	 */
	public static class LastStateProgressListener implements ProgressListener{

		public State getLastState() {
			return lastState;
		}

		private State lastState = null;

		@Override
		public void onStateChanged(State state) {
			this.lastState = state;
		}

		@Override
		public void onCalculationProgress(float percent) {}
	}
}
