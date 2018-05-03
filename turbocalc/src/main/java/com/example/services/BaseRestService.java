package com.example.services;

import java.util.Calendar;

import javax.ws.rs.core.GenericEntity;

import com.example.util.CalendarAdapter;

public class BaseRestService {
	
	protected <T> GenericEntity<T> makeEntity(final T obj) {
		return new GenericEntity<T>(obj, obj.getClass());
	}
	
	// utility method
	
	/**
	 * Converts a {@link Calendar} object to its ISO 8601 representation
	 * 
	 * @param dateTime
	 *            A Calendar instance representing a date/time
	 * @return The given date/time as an ISO 8601 date/time string
	 * @throws Exception
	 *             If the given date/time could not be converted to an ISO 8601 string
	 */
	public String calToStr(final Calendar dateTime)
	{
		CalendarAdapter parser = new CalendarAdapter();
		try
		{
			return parser.marshal(dateTime);
		}
		catch (final Exception e)
		{
			throw new RuntimeException(e);
		}
	}

}