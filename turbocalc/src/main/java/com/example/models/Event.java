package com.example.models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.example.util.CalendarAdapter;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Event {
	
	private long id = -1;

	@XmlElement(name = "start_datetime")
	@XmlJavaTypeAdapter(CalendarAdapter.class)
	private Calendar startDateTime;
	
	@XmlElement(name = "end_datetime")
	@XmlJavaTypeAdapter(CalendarAdapter.class)
	private Calendar endDateTime;
	
	private String description = "";
	
	private String location = "";
	
	private String notes = "";
	
	private Set<String> tags = new TreeSet<>();
	
	@XmlElement(name = "other_attendees")
	private Set<String> otherAttendees = new TreeSet<>();
	
	public Event() {
	}

	/**
	 * @param startDateTime
	 * @param endDateTime
	 * @param description
	 * @param location
	 */
	public Event(final long id, final Calendar startDateTime, final Calendar endDateTime,
			final String description, final String location, final String notes, final Set<String> tags,
			final Set<String> otherAttendees) {

		this.id = id;
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
		this.description = description;
		this.location = location;
		this.notes = notes;
		this.tags = tags;
		this.otherAttendees = otherAttendees;
	}
	
	/**
	 * Copy constructor.  Immutable fields may be copied by reference.
	 * @param toCopy
	 */
	private Event(final Event toCopy) {
		this.copy(toCopy, this);
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
	public Event copy() {
		return new Event(this);
	}
	
	public Event copy(final Event into)
	{
		return this.copy(this, into);
	}
	
	public Event copy(final Event from, final Event into)
	{
		into.id = from.id; 
		into.startDateTime = (Calendar) from.startDateTime.clone();
		into.endDateTime = (Calendar) from.endDateTime.clone();
		into.description = from.description;
		into.location = from.location;
		into.notes = from.notes;
		into.tags = new TreeSet<String>(from.tags);
		into.otherAttendees = new TreeSet<String>(from.otherAttendees);
		
		return into;
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
	
	public String getNotes() {
		return notes;
	}

	public void setNotes(final String notes) {
		this.notes = notes;
	}

	public Set<String> getTags() {
		return tags;
	}

	public void setTags(final Set<String> tags) {
		this.tags = tags;
	}

	public Set<String> getOtherAttendees() {
		return otherAttendees;
	}

	public void setOtherAttendees(final Set<String> otherAttendees) {
		this.otherAttendees = otherAttendees;
	}
	
	@Override
	public String toString() {
		final DateFormat fmt = new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm:ssX");
		
		fmt.setCalendar(startDateTime);
    	fmt.setTimeZone(TimeZone.getTimeZone("UTC"));
    	final String startTimeISO = fmt.format(startDateTime.getTime());
    	
    	fmt.setCalendar(endDateTime);
    	fmt.setTimeZone(TimeZone.getTimeZone("UTC"));
    	final String endTimeISO = fmt.format(endDateTime.getTime());
		
		StringBuilder builder = new StringBuilder();
		builder.append("Event [id=");
		builder.append(id);
		builder.append(", startTime=");
		builder.append(startTimeISO);
		builder.append(", endTime=");
		builder.append(endTimeISO);
		builder.append(", description=");
		builder.append(description);
		builder.append(", location=");
		builder.append(location);
		builder.append(", notes=");
		builder.append(notes);
		builder.append(", tags=");
		builder.append(tags);
		builder.append(", otherAttendees=");
		builder.append(otherAttendees);
		builder.append("]");
		return builder.toString();
	}

	public EventSummary summarize()
	{
		return new EventSummary(id, startDateTime, endDateTime, description, location);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((endDateTime == null) ? 0 : endDateTime.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((notes == null) ? 0 : notes.hashCode());
		result = prime * result + ((otherAttendees == null) ? 0 : otherAttendees.hashCode());
		result = prime * result + ((startDateTime == null) ? 0 : startDateTime.hashCode());
		result = prime * result + ((tags == null) ? 0 : tags.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Event other = (Event) obj;
		if (description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!description.equals(other.description)) {
			return false;
		}
		if (endDateTime == null) {
			if (other.endDateTime != null) {
				return false;
			}
		} else if (!endDateTime.equals(other.endDateTime)) {
			return false;
		}
		if (id != other.id) {
			return false;
		}
		if (location == null) {
			if (other.location != null) {
				return false;
			}
		} else if (!location.equals(other.location)) {
			return false;
		}
		if (notes == null) {
			if (other.notes != null) {
				return false;
			}
		} else if (!notes.equals(other.notes)) {
			return false;
		}
		if (otherAttendees == null) {
			if (other.otherAttendees != null) {
				return false;
			}
		} else if (!otherAttendees.equals(other.otherAttendees)) {
			return false;
		}
		if (startDateTime == null) {
			if (other.startDateTime != null) {
				return false;
			}
		} else if (!startDateTime.equals(other.startDateTime)) {
			return false;
		}
		if (tags == null) {
			if (other.tags != null) {
				return false;
			}
		} else if (!tags.equals(other.tags)) {
			return false;
		}
		return true;
	}
}
