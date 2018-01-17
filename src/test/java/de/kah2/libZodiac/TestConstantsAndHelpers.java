package de.kah2.libZodiac;

import org.threeten.bp.LocalDate;
import org.threeten.bp.ZoneId;

import org.slf4j.LoggerFactory;
import org.slf4j.impl.SimpleLogger;

import de.kah2.libZodiac.planetary.Position;

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

	private final static int sleepStepMs = 100;

	/**
	 * Enables and configures {@link SimpleLogger} for level DEBUG.
	 */
	public static void enableLogging() {
		TestConstantsAndHelpers.enableLogging("debug");
	}

	/**
	 * Enables and configures {@link SimpleLogger}.
	 */
	public static void enableLogging(final String level) {
		System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, level);
		System.setProperty(org.slf4j.impl.SimpleLogger.SHOW_THREAD_NAME_KEY, "true");
		System.setProperty(org.slf4j.impl.SimpleLogger.SHOW_DATE_TIME_KEY, "true");
		System.setProperty(org.slf4j.impl.SimpleLogger.DATE_TIME_FORMAT_KEY, "HH:mm:ss:SSS");
		System.setProperty(org.slf4j.impl.SimpleLogger.SHOW_SHORT_LOG_NAME_KEY, "true");
	}

	/** Simple method to generate a {@link de.kah2.libZodiac.Calendar} and wait for results. */
	public static void generateAndWaitFor(CalendarGenerator generator) {
		final LastStateProgressListener listener = new LastStateProgressListener();

		generator.getProgressManager().addProgressListener(listener);

		generator.startGeneration();

		while (listener.getLastState() != ProgressListener.State.FINISHED) {
			try {
				Thread.sleep(sleepStepMs);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		generator.getProgressManager().removeProgressListener(listener);
	}

	/**
	 * Simple method to generate a {@link de.kah2.libZodiac.Calendar} and wait for results for a specified time.
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


		/** Simple method to generate a {@link de.kah2.libZodiac.Calendar} and wait for results. */
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
