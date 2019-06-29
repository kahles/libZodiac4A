package de.kah2.zodiac.libZodiac4A;

import org.threeten.bp.LocalDate;
import org.threeten.bp.temporal.ChronoUnit;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * This class represents a range between two dates including them and offers
 * some operations for comparing and accessing.
 * 
 * @author kahles
 */
public class DateRange implements Iterable<LocalDate> {

	private final LocalDate start, end;

	/**
	 * Instantiates a {@link DateRange} between two dates and ensures that end
	 * is after start.
	 * @param date1 the first date contained
	 * @param date2 the last date contained
	 */
	public DateRange(final LocalDate date1, final LocalDate date2) {

		if (date1.isAfter(date2)) {
			this.start = date2;
			this.end = date1;
		} else {
			this.start = date1;
			this.end = date2;
		}
	}

	/**
	 * Allows instantiating da {@link DateRange} from two {@link Day} objects.
	 * @param day1 a Day-object containing the start date of the range
	 * @param day2 a Day-object containing the end date of the range
	 */
	public DateRange(final Day day1, final Day day2) {
		this(day1.getDate(), day2.getDate());
	}

	public boolean contains(final LocalDate date) {
		return !(date.isBefore(this.start) || date.isAfter(this.end));
	}

	/**
	 * Checks if a range contains the other ore if they are equal.
	 * @param other the range which should be contained in actual range
	 * @return true if <code>other</code> is contained or equal - false if not.
	 */
	public boolean contains(final DateRange other) {
		return !(other.end.isAfter(this.end) || other.start.isBefore(this.start));
	}

	/**
	 * @param other the range to compare
	 * @return true, if ranges have same start and end.
	 */
	public boolean isEqual(DateRange other) {
		return this.getStart().isEqual( other.getStart() )
				&& this.getEnd().isEqual( other.getEnd() );
	}

	/**
	 * @return the start
	 */
	public LocalDate getStart() {
		return this.start;
	}

	/**
	 * @return the end
	 */
	public LocalDate getEnd() {
		return this.end;
	}

	@Override
	public Iterator<LocalDate> iterator() {
		return new Iterator<LocalDate>() {

			private LocalDate nextDate = DateRange.this.getStart();

			@Override
			public boolean hasNext() {
				return this.nextDate.isBefore(DateRange.this.getEnd())
						|| this.nextDate.isEqual(DateRange.this.getEnd());
			}

			@Override
			public LocalDate next() {
				final LocalDate currentDate = this.nextDate;
				this.nextDate = currentDate.plusDays(1);
				return currentDate;
			}
		};
	}

	/**
	 * @return Number of days contained in a range.
	 */
	public int size() {
		return (int) ChronoUnit.DAYS.between(this.getStart(), this.getEnd()) + 1;
	}

	@Override
	public String toString() {
		return this.getStart() + "-->" + this.getEnd();
	}

	LinkedList<LocalDate> toList() {
		final LinkedList<LocalDate> result = new LinkedList<>();

		for (LocalDate date : this) {
			result.add(date);
		}

		return result;
	}
}
