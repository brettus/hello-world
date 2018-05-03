package com.example.services;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.example.dao.IEventDAO;
import com.example.dao.MemoryEventDAO;
import com.example.models.ErrorMessage;
import com.example.models.Event;
import com.example.models.EventList;
import com.example.models.MessageList;

@Path("/admin")
@Produces(MediaType.APPLICATION_JSON)
public class Admin extends BaseRestService
{
	@Context
    private UriInfo uriInfo;
	
	@GET
	@Path("/resetmemdb")
	public Response resetMemDB() {
		MemoryEventDAO.resetMemDB();
		
		return Response
			.ok()
			.entity(makeEntity(new ErrorMessage("Memory DB reset!")))
			.build();
	}
	
	@GET
	@Path("/export")
	public Response exportEvents() {
		final IEventDAO dao = new MemoryEventDAO();
		final EventList events = new EventList(dao.getAllEvents());
		return Response
			.ok(makeEntity(events))
			.build();
	}
	

	
	@POST
	@Path("/import")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response importEvents(final EventList events) {
		
		MemoryEventDAO.resetMemDB();
		final IEventDAO dao = new MemoryEventDAO();
		
		final MessageList errors = new MessageList();
		
		for(final Event event : events.getEvents()) {
			if(null == event.getStartDateTime() || 
			   null == event.getEndDateTime()) {
				
				errors.addMessage(new ErrorMessage("Event contained missing or unparseable date(s)."));
				
				// skip to the next one
				continue;
			}
			
			dao.forceAddEvent(event);
		}
		
		if(errors.isEmpty()) {
			final UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
			uriBuilder.path("../export");
			
			// Jersey doesn't seem to send Content-Type on empty response
			// Firefox will default to text/xml for XHR responses
			// and then spit out a "no element found" warning in the console if the response body is empty
			// Manually setting the header to JSON avoids this.
			return Response
				.created(uriBuilder.build())
				.header("Content-Type", "application/json")
				.build();
		}
		else {
			return Response
				.status(Response.Status.BAD_REQUEST)
				.entity(makeEntity(errors))
				.build();
		}
	}
}
