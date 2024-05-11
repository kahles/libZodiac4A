package de.kah2.zodiac.libZodiac.example;

import de.kah2.zodiac.libZodiac.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class shows how to create, store, load and extend a {@link Calendar}.
 *
 * @author kahles
 */
public class CalendarExampleStorage {

	private final static Logger LOG = LoggerFactory.getLogger(CalendarExampleStorage.class);

	/**
	 * Runs the example.
	 */
	public static void run() {

		LocalDate today = LocalDate.now();

		// Let's say we have an application which always shows today and the
		// next three days
		DateRange range = new DateRange(today, today.plusDays(3));
		// Because this example doesn't need lunar phase calculation, we use
		// Scope.DAY to avoid calculation overhead
		final Calendar.Scope scope = Calendar.Scope.DAY;

		// Instantiate the Calendar:
		// We use CalendarStub because we don't need real data in this example -
		// it works like a normal Calendar, but doesn't calculate anything.
		Calendar calendar = new CalendarStub(range, scope);

		// For this example we use a List as database

		CalendarExampleStorage.LOG.info( "Generating Calendar for DateRange: {}", range );
		TestConstantsAndHelpers.generateAndWaitFor(calendar);
		LinkedList<Day> generated = calendar.getNewlyGenerated();
		CalendarExampleStorage.LOG.info( "=> Data generated: {} -> {}",
				generated.getFirst().getDate(),
				generated.getLast().getDate() );

		// Here we can store the newly created days for next time ...
		final List<DayStorableDataSetPojo> fakeDatabase = generated.stream()
				.map( DayStorableDataSetPojo::new )
				.collect( Collectors.toList() );

		// Display the results to the user and the user closes our application
		// afterwards.
		// (...)

		// After two days the user opens our application again:
		today = today.plusDays(2);

		// Instantiate a new Calendar:
		range = new DateRange(today, today.plusDays(3));
		calendar = new CalendarStub(range, scope);

		// Import stored data:
		calendar.importDays(fakeDatabase);

		LinkedList<Day> days = calendar.getAllDays();
		CalendarExampleStorage.LOG.info( "Data loaded: {} -> {}", days.getFirst().getDate(), days.getLast().getDate() );

		// And generate the new ones:
		CalendarExampleStorage.LOG.info( "Generating Calendar for DateRange: {}", range );
		TestConstantsAndHelpers.generateAndWaitFor(calendar);
		generated = calendar.getNewlyGenerated();
		CalendarExampleStorage.LOG.info( "=> Data generated: {} -> {}",
				generated.getFirst().getDate(),
				generated.getLast().getDate() );

		// Store the newly created ...
		fakeDatabase.addAll( generated.stream()
				.map( DayStorableDataSetPojo::new)
				.toList() );

		// Display the results and close the application
		// (...)

		// Now, the user opens our application after 7 days:
		today = today.plusDays(7);

		// Instantiate a new Calendar:
		range = new DateRange(today, today.plusDays(3));
		calendar = new CalendarStub(range, scope);

		// Import stored data:
		calendar.importDays(fakeDatabase);

		days = calendar.getAllDays();
		CalendarExampleStorage.LOG.info( "Data loaded: {} -> {}", days.getFirst().getDate(), days.getLast().getDate() );

		CalendarExampleStorage.LOG.info( "Generating Calendar for DateRange: {}", range );
		TestConstantsAndHelpers.generateAndWaitFor(calendar);
		generated = calendar.getNewlyGenerated();
		CalendarExampleStorage.LOG.info( "=> Data generated: {} -> {}", generated.getFirst().getDate(), generated.getLast().getDate() );

		// Now we have an inconsistent Calendar containing a gap and should:

		// Remove past (or all) days that aren't needed anymore ...
		final LinkedList<Day> removed = calendar.removeOverhead(false);
		CalendarExampleStorage.LOG.info( "Removed overhead: {} -> {}", removed.getFirst().getDate(), removed.getLast().getDate() );

		// OR correct the expectedRange to include imported days ...
//		calendar.fixRangeExpectedToIncludeExistingDays();
//		range = calendar.getRangeExpected();
//		CalendarExampleStorage.LOG.info("Fixed expected range: " + range);

		// and generate again (or better fix the range between import and first call of generate)
//		CalendarExampleStorage.LOG.info("Generating Calendar for DateRange: " + range);
//		generated = calendar.generate();
//		CalendarExampleStorage.LOG.info("=> Data generated: " + generated.getFirst().getDate() + " -> " + generated.getLast().getDate());
	}

	public static void main(final String[] args) {

		CalendarExampleStorage.run();
	}
}
