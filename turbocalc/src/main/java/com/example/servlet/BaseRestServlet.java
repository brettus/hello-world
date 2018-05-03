package com.example.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Objects;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import com.example.util.gson.CalendarGsonAdapter;
import com.example.util.gson.JAXBAnnotationsNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class BaseRestServlet extends HttpServlet {
	private static final long serialVersionUID = -3449843961986421647L;
	
	private final GsonBuilder gsonBuilder = new GsonBuilder()
		    .setFieldNamingStrategy(new JAXBAnnotationsNamingStrategy())
		    .registerTypeAdapter(Calendar.class, new CalendarGsonAdapter())
		    .registerTypeAdapter(GregorianCalendar.class, new CalendarGsonAdapter());
	
	/**
	 * This can be used to extend or replace the GsonBuilder used by getGson()
	 * @return
	 */
	protected GsonBuilder getGsonBuilder() {
		return gsonBuilder;
	}
	
	/**
	 * Obtains the GSON instance used for type conversions. Uses a
	 * {@link GsonBuilder} obtained via {@link BaseRestServlet#getGsonBuilder()}
	 * 
	 * @return A {@link Gson} instance
	 */
	protected Gson getGson() {
		return getGsonBuilder().create();
	}
	
	/**
	 * Converts a JSON string to an object of the specified type
	 * 
	 * @param jsonString
	 *            The JSON to convert
	 * @param resultType
	 *            The type the JSON represents
	 * @return An object of the given result type
	 * @throws JsonSyntaxException
	 *             if the given JSON is not a valid representation for an object
	 *             of type resultType
	 */
	protected <T> T fromJson(final String jsonString, final Class<T> resultType) {
	    return getGson().fromJson(jsonString, resultType);
	}
	
	/**
	 * Converts an object to JSON using a GSON instance obtained via {@link BaseRestServlet#getGson()}
	 * @param obj The object to convert to JSON
	 * @return
	 */
	protected String toJson(final Object obj) {
	    return getGson().toJson(obj);
	}
	
	/**
	 * Reads the body of the request as JSON and converts it to the given type
	 * @param req The HTTP request
	 * @param resultType The type the request body corresponds to
	 * @return
	 * @throws IOException If an error occurred reading the request body
	 */
	protected <T> T requestBodyToJson(final HttpServletRequest req, final Class<T> resultType) throws IOException {
		final BufferedReader reqBodyReader = req.getReader();
		
		final StringBuilder builder = new StringBuilder();
		final char[] charBuffer = new char[8192];
	    int bytesRead = -1;
	    while ((bytesRead = reqBodyReader.read(charBuffer)) > 0) {
	    	builder.append(charBuffer, 0, bytesRead);
	    }
		
		return fromJson(builder.toString(), resultType);
	}

	/**
	 * Obtains the base URL of the service. E.g., if
	 * the service root is <code>http://www.example.com/services/event</code>,
	 * and a service call is made to
	 * <code>http://www.example.com/services/event/from/2014/01/to/2014/02</code>, 
	 * this method would return <code>http://www.example.com/services/event</code>
	 * 
	 * @param req
	 *            The {@link HttpServletRequest} of the service call
	 * @return The base portion of the URL.
	 */
	protected String getBaseURL(final HttpServletRequest req) {
		final String fullURL = req.getRequestURL().toString();
		Objects.requireNonNull(fullURL);
		final String pathInfo = req.getPathInfo();
		
		if(null == pathInfo) {
			return fullURL;
		}
		else {
			final int pathInfoIdx = fullURL.indexOf(pathInfo);
			final String baseURL = fullURL.substring(0, pathInfoIdx);
			
			return baseURL;
		}
	}

	/**
	 * Obtains the portion of the URL which is specific to the service. E.g., if
	 * the service root is <code>http://www.example.com/services/event</code>,
	 * and a service call is made to
	 * <code>http://www.example.com/services/event/from/2014/01/to/2014/02</code>, 
	 * this method would return <code>/from/2014/01/01/to/2014/01/07</code>
	 * 
	 * @param req
	 *            The {@link HttpServletRequest} of the service call
	 * @return The service portion of the URL. Note that this may be an empty
	 *         string if a call is being made to the base URL.
	 */
	protected String getServiceURL(final HttpServletRequest req) {
		final String pathInfo = req.getPathInfo();
		
		if(null == pathInfo) {
			return "";
		}
		else {
			return pathInfo;
		}
	}

}