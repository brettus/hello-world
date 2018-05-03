package com.example.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.dao.IEventDAO;
import com.example.dao.MemoryEventDAO;
import com.example.models.ErrorMessage;
import com.example.models.Event;
import com.example.models.EventList;

public class EventServiceServlet extends BaseRestServlet {
	private static final long serialVersionUID = -2054937251496003560L;
	
	private static class MimeTypes {
		public static final String APPLICATION_JSON = "application/json";
		public static final String TEXT_PLAIN = "text/plain";
	}
	
	private static class UrlPatterns {
		public static final Pattern SERVICE_GET_ALL_EVENTS = Pattern.compile("/all");
		public static final Pattern SERVICE_GET_EVENTS_BY_ID = Pattern.compile("/(?<id>\\d+(?:\\+\\d+)*)");
	}
	
	public IEventDAO getDAO() {
		return new MemoryEventDAO();
	}
	
	public void addEvent(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
		final Event eventToAdd = requestBodyToJson(req, Event.class);
		
		if(null == eventToAdd.getStartDateTime() || 
		   null == eventToAdd.getEndDateTime()) {
			
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			final ErrorMessage output = new ErrorMessage("Event contained missing or unparseable date(s).");
			final PrintWriter respBody = resp.getWriter();
			respBody.write(toJson(output));
			respBody.flush();
			
			return;
		}
		
		final IEventDAO dao = getDAO();
		final Event added = dao.addEvent(eventToAdd);
		
		final String baseURL = getBaseURL(req);
		final String getURL = String.format("%s/%s", baseURL, Long.valueOf(added.getId()));
		
		resp.setHeader("Location", getURL);
		resp.setStatus(HttpServletResponse.SC_CREATED);
		resp.setContentType(MimeTypes.TEXT_PLAIN);
	}
	
	@Override
	protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
		
		// we only have one POST method on the service, so no need to do URL-based routing
		addEvent(req, resp);
	}
}
