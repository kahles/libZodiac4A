package de.kah2.zodiac.libZodiac;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

/**
 * This class holds and manages the {@link Day}-objects of a {@link Calendar}.
 */
class CalendarData {

    // All days actually contained in the Calendar
    private final TreeSet<Day> data;

    CalendarData() {
        this.data = new TreeSet<>();
    }

    /**
     * <p>This method does the job of importing data - it should only be used internally!</p>
     * <p>To import data, use {@link Calendar#importDays(List)}.</p>
     */

    void importDays(final List<DayStorableDataSet> storedDays) {

        for (DayStorableDataSet storedDay : storedDays) {
            this.insert(Day.importFrom(storedDay));
        }
    }

    /**
     * Checks which dates of given range are not contained and returns them.
     */
    LinkedList<LocalDate> getMissingDates(DateRange rangeExpected) {

        if (this.data.isEmpty()) {
            return rangeExpected.toList();
        }

        final LinkedList<LocalDate> missingDates = new LinkedList<>();

        for (LocalDate date : rangeExpected) {
            final Day day = new Day(date);

            if (!this.data.contains(day)) {
                missingDates.add(date);
            }
        }

        return missingDates;
    }

    /**
     * Inserts a day at its correct position.
     */
    void insert(Day day) {
        this.data.add(day);
    }

    /** Removes days before given date. */
    LinkedList<Day> removeBefore(LocalDate date) {
        final LinkedList<Day> deletedDays = new LinkedList<>();

        if (this.isEmpty()) {
            return deletedDays;
        }

        while ( !this.isEmpty() && this.data.first().getDate().isBefore(date) ) {
            deletedDays.add( this.data.pollFirst() );
        }

        return deletedDays;
    }

    /** Removes days after given date. */
    LinkedList<Day> removeAfter(LocalDate date) {
        final LinkedList<Day> deletedDays = new LinkedList<>();

        if (this.isEmpty()) {
            return deletedDays;
        }

        while ( !this.isEmpty() && this.data.last().getDate().isAfter(date) ) {
            deletedDays.add( this.data.pollLast() );
        }

        return deletedDays;
    }

    Day getFirst() {
        return this.data.first();
    }

    Day getLast() {
        return this.data.last();
    }

    boolean contains(Day day) {
        return this.data.contains(day);
    }

    /**
     * @return The requested {@link Day} or null, if date is out of
     *         {@link Calendar} range.
     */
    Day get(final LocalDate date) {

        final Day dummyDay = new Day(date);

        NavigableSet<Day> subSet = this.data.subSet(dummyDay, true, dummyDay, true);

        if ( subSet.isEmpty() ) {
            return null;
        } else {
            return subSet.first();
        }
    }

    /**
     * Returns all {@link Day}-elements this calendar contains.<br/>
     * Consistency isn't guaranteed, if no data is available an empty list is returned.
     */
    LinkedList<Day> allAsList() {
        return new LinkedList<>(this.data);
    }

    /**
     * Returns all {@link Day}-elements this calendar contains.<br/>
     * Consistency isn't guaranteed, if no data is available an empty list is returned.
     */
    TreeSet<Day> allAsTreeSet() {
        return new TreeSet<>(this.data);
    }

    /**
     * Returns all days within a given range. Range might contain gaps!
     */
    LinkedList<Day> of(Day start, Day end) {

        NavigableSet<Day> subSet = this.data.subSet(start, true, end, true);

        return new LinkedList<>(subSet);
    }

    /** Returns the number of all contained days. */
    public int size() {
        return this.data.size();
    }

    /**
     * Returns a {@link DateRange} of all contained days, or null if {@link #isEmpty()} == true.
     */
    DateRange getRangeOfAllContainedDays() {
        if (this.isEmpty()) {
            return null;
        }

        return new DateRange(this.data.first(), this.data.last());
    }

    boolean isComplete() {
        final DateRange all = this.getRangeOfAllContainedDays();
        return all != null && this.isComplete(all);
    }

    /** Checks if {@link Day}-objects exist for all dates in given range. */
    boolean isComplete(DateRange range) {
        return this.getMissingDates(range).isEmpty();
    }

    /**
     * Checks if any calculated days are present.
     */
    boolean isEmpty() {
        return this.data.isEmpty();
    }
}
