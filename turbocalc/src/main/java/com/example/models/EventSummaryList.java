package com.example.models;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class EventSummaryList {
	
	private List<EventSummary> events = new ArrayList<>();
	
	public EventSummaryList()
	{
	}
	
	/**
	 * @param events
	 */
	public EventSummaryList(List<EventSummary> events) {
		this.events = new ArrayList<>(events);
	}

	/**
	 * @return the events
	 */
	public List<EventSummary> getEvents() {
		return events;
	}

	/**
	 * @param events the events to set
	 */
	public void setEvents(final List<EventSummary> events) {
		this.events = events;
	}
	
	public void addEvent(final EventSummary event)
	{
		this.events.add(event);
	}
}
