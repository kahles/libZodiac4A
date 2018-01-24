package de.kah2.libZodiac;

import de.kah2.libZodiac.ProgressListener.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * This class contains functionality to calculate and deliver progress
 * information.
 *
 * @see ProgressListener
 * @author kahles
 */
class ProgressManager {

	private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

	private final static int APPROXIMATE_CYCLE_LENGTH = 17;

	private final List<ProgressListener> progressListeners = new LinkedList<>();

	private long daysToGenerate = 0;
	private long daysGenerated = 0;

	private float percentGenerated;

	private ProgressListener.State state;

	/** Used to calculate percentage */
	void reset() {
		this.daysToGenerate = 0;
		this.daysGenerated = 0;
		this.percentGenerated = 0;

		this.notifyStateChanged(null);
	}

	/** Estimates in how many directions data must be extended, when Scope is CYCLE. */
	void estimateExtensions(Calendar calendar) {

		if (calendar.getScope() == Calendar.Scope.CYCLE) {

			if (calendar.getDays().isEmpty()) {

				addEstimatedExtensions(2);

			} else {
				final LinkedList<Day> days = calendar.getDays().allAsList();

				// first and last can't have lunar phases
				days.removeFirst();
				days.removeLast();

				int estimatedExtensions = 0;

				if (days.getFirst().getPlanetaryData().getLunarPhase() == null
						|| !days.getFirst().getPlanetaryData().getLunarPhase().isLunarExtreme()) {
					estimatedExtensions++;
				}

				if (days.getLast().getPlanetaryData().getLunarPhase() == null
						|| !days.getLast().getPlanetaryData().getLunarPhase().isLunarExtreme()) {
					estimatedExtensions++;
				}

				this.addEstimatedExtensions(estimatedExtensions);
			}
		}
	}

	/** Used to calculate percentage */
	void addNumberOfDaysToGenerate(final long count) {
		this.daysToGenerate += count;
	}

	/** Set directions (0, 1 or 2) we have to extend */
	private void addEstimatedExtensions(final int count) {

		final int actualCycleOverflow = (int) (this.daysToGenerate % APPROXIMATE_CYCLE_LENGTH);

		if (count > 0) {
			this.daysToGenerate += APPROXIMATE_CYCLE_LENGTH - actualCycleOverflow;
		}
	}

	/**
	 * Keeps the {@link ProgressManager} up to date on state changes.
	 */
	void notifyStateChanged(final ProgressListener.State state) {

		this.log.debug(">>>>>>>> State changes to " + state);

		if (this.state != state) {

			this.state = state;

			if (this.state == ProgressListener.State.COUNTING) {

				this.percentGenerated = .99f;

			} else if (this.state == State.FINISHED) {

				this.percentGenerated = 1;
			}

			for (ProgressListener listener : this.progressListeners) {

				listener.onStateChanged(this.state);
				listener.onCalculationProgress(this.percentGenerated);
			}
		}
	}

	/**
	 * Keeps the {@link ProgressManager} on track with progress.
	 */
	void notifyDayCreated() {

		this.daysGenerated++;
		this.updatePercentageOnCalculationProgress();

		this.log.debug("Day created, progress is " + this.percentGenerated);

		for (ProgressListener listener : this.progressListeners) {
			listener.onCalculationProgress(this.percentGenerated);
		}
	}

	/**
	 * calculates values between 0 and 0.98 (0.98, because if there is more to
	 * extend than approximated, we will be at 1 (100%) before it's done.)
	 */
	private void updatePercentageOnCalculationProgress() {
		final float percent = (float) this.daysGenerated / this.daysToGenerate;

		if (percent < .98) {
			this.percentGenerated = percent;
		} else {
			this.percentGenerated = .98f;
		}
	}

	/**
	 * Registers a {@link ProgressListener}.
	 */
	public void addProgressListener(final ProgressListener progressListener) {
		this.progressListeners.add(progressListener);
	}

	/**
	 * Unregisters a {@link ProgressListener}
	 */
	public void removeProgressListener(final ProgressListener progressListener) {
		this.progressListeners.remove(progressListener);
	}

	/** Needed for {@link de.kah2.libZodiac.CalendarGenerator} to know what to do next. */
	ProgressListener.State getState() {
		return state;
	}
}
