package de.kah2.zodiac.libZodiac4A;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

public class TestConstantsAndHelpers {

	private final static Logger LOG = LoggerFactory.getLogger(TestConstantsAndHelpers.class);

	public final static LocalDate SOME_DATE = LocalDate.of(2016, 9, 3);

	/** Last lunar extreme before SOME_DATE - full moon */
	public final static LocalDate SOME_DATES_LAST_EXTREME = LocalDate.of(2016, 9, 1);

	/** Next lunar extreme after SOME_DATE - new moon */
	public final static LocalDate SOME_DATES_NEXT_EXTREME = LocalDate.of(2016, 9, 6);

	private final static int sleepStepMs = 1000;

	/** Simple method to generate a {@link Calendar} and wait for results. */
	public static void generateAndWaitFor(CalendarGenerator generator) {

		final LastStateProgressListener listener = new LastStateProgressListener();

		generator.getProgressManager().addProgressListener(listener);

		generator.startGeneration();

		while (listener.getLastState() != ProgressListener.State.FINISHED) {
			try {
				LOG.trace("waiting " + sleepStepMs + "ms for state change to FINISHED");
				Thread.sleep(sleepStepMs);
			}
			catch (InterruptedException e) {
				LOG.error("Interrupted", e);
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
					LoggerFactory.getLogger( "TestConstantsAndHelpers#generateAndWaitFor(CalendarGenerator,int)" )
							.trace( "Terminated executor - {} jobs were still waiting", remaining );
					exitedNormally = false;
					break;
				}
			}
			catch (InterruptedException e) {
				LOG.error("Interrupted", e);
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
				LOG.error("Interrupted", e);
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
