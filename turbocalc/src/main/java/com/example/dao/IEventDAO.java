package com.example.dao;

import java.util.Calendar;
import java.util.List;

import com.example.models.Event;

public interface IEventDAO {
	
	/**
	 * Obtains all the stored events, sorted by ID in ascending order.
	 * 
	 * @return All events in ascending order by ID.
	 */
	public List<Event> getAllEvents();
	
	/**
	 * Obtains an event based on its ID
	 * @param eventID The ID of the event to retrieve 
	 * @return The event with the given ID, or <code>null</code> if no such event was found
	 */
	public Event getEvent(final long eventID);
	
	/**
	 * Obtains all the events which occur at least partially within the given
	 * time period.<br>
	 * <br>
	 * Example:<br>
	 * <br>
	 * <pre>
	 * Search window:      08:00           09:00
	 *                       |               |
	 * Events:               |               |
	 * Matched?              |               |
	 *    N        07:30   08:00             |
	 *    Y        07:30     |     08:30     |
	 *    Y                08:00           09:00
	 *    Y                  | 08:15   08:45 | 
	 *    Y                  |         08:45 |         09:45
	 *    N                  |               | 09:15   09:45
	 *    N                  |             09:00   09:30
	 *    Y        07:30     |             09:00
	 *    Y                08:00             |     09:30
	 *    Y        07:30     |               |     09:30
	 * </pre>
	 * 
	 * @param searchRangeStart
	 *            Start of the search window
	 * @param searchRangeEnd
	 *            End of the search window
	 * @return A list of events, such that at least part of the event occurs
	 *         within the given search window, inclusive
	 */
	public List<Event> getEventsBetweenPartial(final Calendar searchRangeStart, final Calendar searchRangeEnd);
	
	/**
	 * Obtains all the events which occur completely within the given time
	 * period.<br>
	 * <br>
	 * Example:<br>
	 * <br>
	 * <pre>
	 * Search window:      08:00           09:00
	 *                       |               |
	 * Events:               |               |
	 * Matched?              |               |
	 *    N        07:30   08:00             |
	 *    N        07:30     |     08:30     |
	 *    Y                08:00           09:00
	 *    Y                  | 08:15   08:45 | 
	 *    N                  |         08:45 |         09:45
	 *    N                  |               | 09:15   09:45
	 *    N                  |             09:00   09:30
	 *    N        07:30     |             09:00
	 *    N                08:00             |     09:30
	 *    N        07:30     |               |     09:30
	 * </pre>
	 * 
	 * @param searchRangeStart
	 *            Start of the search window
	 * @param searchRangeEnd
	 *            End of the search window
	 * @return A list of events, such that the entire event occurs within the
	 *         given search window, inclusive
	 */
	public List<Event> getEventsBetweenComplete(final Calendar searchRangeStart, final Calendar searchRangeEnd);
	
	/**
	 * Searches for events by tag. If more than one tag is given, the search
	 * will be treated as an AND search - i.e., only events containing all tags
	 * will be returned. If <em>no tags</em> are given, the result set will be
	 * empty.
	 * 
	 * @param tags
	 *            An array of one or more tags.
	 * @return List of events which contain the given tags. The list may be
	 *         empty, but will never be null.
	 */
	public List<Event> getEventsByTag(final String... tags);
	
	public Event addEvent(final Event newEvent);
	
	public Event removeEvent(final long eventId);
	public Event removeEvent(final Event event);
	
	/**
	 * Updates an event if it is already in the database. If no event with the
	 * given ID exists, this will no do anything.
	 * 
	 * @param eventToUpdate
	 *            The updated event to store in the database.
	 * @return The event which was replaced by the updated event, or
	 *         <code>null</code> if no event was updated.
	 */
	public Event updateEvent(final Event eventToUpdate);
	
	/**
	 * Adds an event into the database. The ID included in the event is
	 * preserved, and any other event with that same ID is replaced. Essentially
	 * an "upsert".
	 * 
	 * @param event
	 *            The event to add
	 * @return The event which was added
	 */
	public Event forceAddEvent(final Event event);
}
