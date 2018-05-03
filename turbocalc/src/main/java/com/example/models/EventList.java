package com.example.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.example.models.comparators.EventStartDateTimeComparator;
import com.example.models.comparators.EventStartDateTimeComparator.Order;

@XmlRootElement
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class EventList {
	
	private List<Event> events = new ArrayList<>();
	
	public EventList()
	{
	}
	
	public EventList(final List<Event> events) {
		this.events = new ArrayList<>(events);
	}
	
	
	public List<Event> getEvents() {
		return events;
	}
	
	public void setEvents(final List<Event> events) {
		this.events = events;
	}
	
	public void addEvent(final Event event)
	{
		this.events.add(event);
	}
	
	/**
	 * Obtains an event from the event list based on its ID. If multiple events
	 * exist with the same ID, only the first one will be returned. Searching
	 * will be done based on the order events were inserted into the EventList.
	 * 
	 * @param id
	 *            The ID to search for
	 * @throws IllegalArgumentException
	 *             If no event with the given ID was found.
	 * @return An {@link Event} with the given ID
	 */
	public Event getEventById(final long id) {
		for(final Event ev : this.events) {
			if(id == ev.getId()) {
				return ev;
			}
		}
		
		throw new IllegalArgumentException("No event with the given ID was found");
	}
	
	public void sortEventsAscending() {
		Collections.sort(this.events, new EventStartDateTimeComparator(Order.ASC));
	}
	
	
	public EventSummaryList summarize()
	{
		final EventSummaryList events = new EventSummaryList();
		
		for(final Event ev : this.events) {
			events.addEvent(ev.summarize());
		}
		
		return events;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EventList [events=");
		builder.append(events);
		builder.append("]");
		return builder.toString();
	}
}
