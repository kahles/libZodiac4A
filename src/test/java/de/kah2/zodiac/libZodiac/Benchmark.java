package de.kah2.zodiac.libZodiac;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import de.kah2.zodiac.libZodiac.Calendar.Scope;

/**
 * This class is used to measure generation time of a {@link Calendar}.
 * It can be used to measure generation of a Calendar for {@link Scope#DAY}, {@link Calendar.Scope#PHASE} and
 * {@link Scope#CYCLE}.
 */
public class Benchmark {

    private final static int MAX_THREADS = -1;

    private final static int NUMBER_OF_LOOPS = 3;

    private final static LocalDate DATE = LocalDate.of(2017, 10, 4);
    private final static DateRange RANGE = new DateRange(DATE, DATE.plusYears(1));

    private static void benchmark(Scope scope) {

        System.out.println("Benchmarking for Scope " + scope + " ...");
        System.out.println("Calculating Calendar from " + RANGE);

        for (int loop = 0; loop < NUMBER_OF_LOOPS; loop ++) {

            final Calendar calendar = new Calendar(TestConstantsAndHelpers.POSITION_MUNICH, RANGE, scope);
            calendar.addProgressListener(new BenchmarkListener());
            calendar.getGenerator().setMaxThreadCount(MAX_THREADS);

            System.out.println("Run " + loop + "/" + NUMBER_OF_LOOPS);

            calendar.startGeneration();
        }
    }

    private static class BenchmarkListener implements ProgressListener {

        private final static Instant start = Instant.now();

        @Override
        public void onStateChanged(State state) {
            final Duration duration = Duration.between(start, Instant.now());
            System.out.println("State is " + state + " after " + duration);
        }

        @Override
        public void onCalculationProgress(float percent) {}
    }

    /**
     * Launcher method.
     * @param args one of day, phase or cycle
     */
    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("Wrong number of arguments - use one of \"day\", \"phase\" or \"cycle\"");
            System.exit(1);
        } else {
            switch (args[0]) {
                case "day":
                    benchmark(Scope.DAY);
                    break;
                case "phase":
                    benchmark(Scope.PHASE);
                    break;
                case "cycle":
                    benchmark(Scope.CYCLE);
                    break;
                default:
                    System.out.println("Unknown argument.");
                    System.exit(2);
            }

        }
    }
}
