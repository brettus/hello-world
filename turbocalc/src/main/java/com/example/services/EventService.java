package com.example.services;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.example.dao.IEventDAO;
import com.example.dao.MemoryEventDAO;
import com.example.exceptions.NotYetImplementedException;
import com.example.models.ErrorMessage;
import com.example.models.Event;
import com.example.models.EventList;

@Path("/event")
@Produces(MediaType.APPLICATION_JSON)
public class EventService extends BaseRestService {
	
	private static final int DAY_NOT_SPECIFIED = -1;
	
	@Context
	private UriInfo uriInfo;
	
	@QueryParam("groupby")
	private String groupby;
	
	@GET
	@Path("/all")
	public Response getAllEvents() {
		final IEventDAO dao = new MemoryEventDAO();
		final EventList events = new EventList(dao.getAllEvents());
		return Response
			.ok(makeEntity(events))
			.build();
	}
	
	@GET
	@Path("/{id: \\d+(?:\\+\\d+)*}")
	public Response getEventsById(@PathParam("id") final String eventIDs) {
		final EventList events = new EventList();
		final IEventDAO dao = new MemoryEventDAO();
		
		for(final String id : eventIDs.split("\\+")) {
			final long idNum = Long.parseLong(id);
			final Event event = dao.getEvent(idNum);
			events.addEvent(event);
		}
		
		return Response
			.ok(makeEntity(events))
			.build();
	}
	
	
	/**
	 * Searches for events by tag. Multiple tags can be given, separated by plus
	 * signs. If more than one tag is present, they will be treated as an AND
	 * search - i.e., only events containing all tags will be returned.<br>
	 * <br>
	 * The response body will be an {@link Event} object.
	 * 
	 * @param tags
	 *            One or more tags, separated by plus signs. E.g., "nerf" or
	 *            "nerf+fun+work".
	 * @return JAX-RS Response object with the search results as the response
	 *         body.
	 */
	@GET
	@Path("/tag/{tag: \\w+(?:\\+\\w+)*}")
	public Response getEventsByTag(@PathParam("tag") final String tagString) {
		throw new NotYetImplementedException("getEventsByTag service not yet implemented");
	}
	
	
	@GET
	@Path("/from/{startYear: \\d{1,5}}/{startMonth: \\d{1,2}}{startDay: (?:/\\d{1,2})?}" + 
		  "/to/{endYear: \\d{1,5}}/{endMonth: \\d{1,2}}{endDay: (?:/\\d{1,2})?}")
	public Response getEventsByDateRange(
		@PathParam("startYear") final int startYear,
		@PathParam("startMonth") final int startMonth,
		@PathParam("startDay") final String startDayStr, 
		@PathParam("endYear") final int endYear,
		@PathParam("endMonth") final int endMonth,
		@PathParam("endDay") final String endDayStr
	)
	{
		//TODO: Times will be parsed in UTC.  Should probably allow the client to specify the desired time zone.
		
		int startDay;
		int endDay;
		
		// If a start day was passed in, use it
		if(!"".equals(startDayStr)) {
			startDay = Integer.parseInt(startDayStr.replace("/", ""));
		}
		else {
			// default to first day of month if not specified
			startDay = 1;
		}
		
		final Calendar start = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		start.clear();
		start.set(startYear, startMonth-1, startDay, 0, 0, 0);
		start.set(Calendar.MILLISECOND, 0);
		
		// If an end day was passed in, use it
		if(!"".equals(endDayStr)) {
			endDay = Integer.parseInt(endDayStr.replace("/", ""));
		}
		else {
			endDay = DAY_NOT_SPECIFIED;
		}
		
		final Calendar end = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		// If the end day is not specified, we want the end of the month.
		// Midnight at the end of the last day is the same as 0:00:00.000 on the start 
		// of the first day of the next month
		if(DAY_NOT_SPECIFIED == endDay) {
			end.set(endYear, endMonth-1, 1, 0, 0, 0);
			end.set(Calendar.MILLISECOND, 0);
			end.add(Calendar.MONTH, 1);
		}
		// If we DO have an end day, we want to set the search period end to be midnight at the 
		// end of that day - 0:00:00.000 at the start of the next day
		else {
			end.set(endYear, endMonth, endDay, 0, 0, 0);
			end.set(Calendar.MILLISECOND, 0);
			end.add(Calendar.DAY_OF_MONTH, 1);
		}
		
		final IEventDAO dao = new MemoryEventDAO();
		final EventList matchingEvents = new EventList(dao.getEventsBetweenPartial(start, end));
		matchingEvents.sortEventsAscending();
		
		return Response
			.ok(makeEntity(matchingEvents))
			.build();
	}
	
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addEvent(final Event eventToAdd) {
		if(null == eventToAdd.getStartDateTime() || 
		   null == eventToAdd.getEndDateTime()) {
			
			return Response
				.status(Response.Status.BAD_REQUEST)
				.entity(makeEntity(new ErrorMessage("Event contained missing or unparseable date(s).")))
				.build();
		}
		
		final UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
		
		final IEventDAO dao = new MemoryEventDAO();
		
		final Event added = dao.addEvent(eventToAdd);
		
		uriBuilder.path(Long.toString(added.getId()));
		
		return Response
			.created(uriBuilder.build())
			// silence a Firefox console warning that occurs with 0-len responses which have no MIME type
			.type(MediaType.APPLICATION_OCTET_STREAM)
			.build();
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	// might be a bug in Jersey: if I use path "/{id: \\d+}", then the GET starts failing
	@Path("/{id: \\d+(?:\\+\\d+)*}")
	public Response updateEvent(@PathParam("id") final long eventId, final Event eventToUpdate) {
		final UriBuilder uriBuilder = uriInfo.getBaseUriBuilder().path(EventService.class);
		final IEventDAO dao = new MemoryEventDAO();
		
		// FIXME: Handle case where we tried to update an event that didn't exist
		eventToUpdate.setId(eventId);
		dao.updateEvent(eventToUpdate);
		
		uriBuilder.path(Long.toString(eventId));
		
		return Response
			.noContent()
			.location(uriBuilder.build())
			.build();
	}
	
	@DELETE
	@Path("/{id: \\d+(?:\\+\\d+)*}")
	public Response deleteEvent(@PathParam("id") final long eventId) {
		final IEventDAO dao = new MemoryEventDAO();
		
		// FIXME: Handle case where we tried to delete an event that didn't exist
		dao.removeEvent(eventId);
		
		return Response
			.noContent()
			.build();
	}
}
