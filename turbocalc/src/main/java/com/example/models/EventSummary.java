package com.example.models;

import java.util.Calendar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "id", "startDateTime", "endDateTime", "description", "location" })
public class EventSummary implements Cloneable {
	
	private long id;
	
	@XmlElement(name = "start_datetime")
	private Calendar startDateTime;
	
	@XmlElement(name = "end_datetime")
	private Calendar endDateTime;
	
	private String description;
	
	private String location;
	
	public EventSummary() {
	}

	public EventSummary(final long id, final Calendar startDateTime, final Calendar endDateTime,
			final String description, final String location) {
		
		this.id = id;
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
		this.description = description;
		this.location = location;
	}
	
	/**
	 * Copy constructor.  Immutable fields may be copied by reference.
	 * @param toCopy
	 */
	private EventSummary(final EventSummary toCopy) {
		
		this.id = toCopy.id;
		this.startDateTime = (Calendar) toCopy.startDateTime.clone();
		this.endDateTime = (Calendar) toCopy.endDateTime.clone();
		this.description = toCopy.description;
		this.location = toCopy.location;
	}
	
	/**
	 * Creates a new instance containing a copy of this instance's data -
	 * similar to clone(). The copy will be a deep copy.<br>
	 * <br>
	 * Immutable fields may be copied by reference. This includes collection
	 * values: if this object contains a collection of immutable objects, a new
	 * collection will be made, but the collection contents may be copied by
	 * reference.
	 */
	public EventSummary copy() {
		return new EventSummary(this);
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(final long id) {
		this.id = id;
	}
	
	public Calendar getStartDateTime() {
		return startDateTime;
	}
	
	public void setStartDateTime(final Calendar startDateTime) {
		this.startDateTime = startDateTime;
	}
	
	public Calendar getEndDateTime() {
		return endDateTime;
	}
	
	public void setEndDateTime(final Calendar endDateTime) {
		this.endDateTime = endDateTime;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(final String description) {
		this.description = description;
	}
	
	public String getLocation() {
		return location;
	}
	
	public void setLocation(final String location) {
		this.location = location;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EventSummary [id=");
		builder.append(id);
		builder.append(", startTime=");
		builder.append(startDateTime);
		builder.append(", endTime=");
		builder.append(endDateTime);
		builder.append(", description=");
		builder.append(description);
		builder.append(", location=");
		builder.append(location);
		builder.append("]");
		return builder.toString();
	}
}
