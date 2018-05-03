package com.example.models.comparators;

import java.util.Comparator;

import com.example.models.Event;

/**
 * Sorts {@link Event}s by their start date/time, in either Ascending or Descending order.
 * 
 * @author mmebane
 */
public class EventStartDateTimeComparator implements Comparator<Event> {
	
	public enum Order {
		/** Ascending order (older to newer) */
		ASC,
		/** Descending order (newer to older) */
		DESC
	}

	private Order sortOrder;
	
	/**
	 * Constructs a new {@link EventStartDateTimeComparator} which sorts in ascending (oldest first) order.
	 */
	public EventStartDateTimeComparator() {
		this.sortOrder = Order.ASC;
	}
	
	/**
	 * Constructs a new {@link EventStartDateTimeComparator} which sorts in a given order.
	 * 
	 * @param sortOrder The {@link Order} by which to sort.
	 */
	public EventStartDateTimeComparator(final Order sortOrder) {
		this.sortOrder = sortOrder;
	}

	@Override
	public int compare(final Event e1, final Event e2) {
		final int comparison = e1.getStartDateTime().compareTo(e2.getStartDateTime());
		
		switch (this.sortOrder) {
		case DESC:
			return -comparison;
			
		case ASC:
		default:
			return comparison;
		}
	}

}
