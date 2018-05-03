package com.example;

import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.ws.rs.core.Response;

import org.hamcrest.Matcher;
import org.threeten.bp.ZonedDateTime;

import com.example.dao.IEventDAO;
import com.example.models.Event;
import com.example.models.EventList;
import com.example.util.CalendarAdapter;
import com.example.util.ZonedDateTimeAdapter;
import com.example.util.gson.CalendarGsonAdapter;
import com.example.util.gson.JAXBAnnotationsNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class BaseServiceTest
{
	private final GsonBuilder gsonBuilder = new GsonBuilder()
		.setFieldNamingStrategy(new JAXBAnnotationsNamingStrategy())
		.registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeDeserializer())
		.registerTypeAdapter(Calendar.class, new CalendarGsonAdapter())
		.registerTypeAdapter(GregorianCalendar.class, new CalendarGsonAdapter());

	/**
	 * This can be overridden to extend or replace the GsonBuilder used by getGson()
	 * 
	 * @return A {@link GsonBuilder} with some default configurations applied.
	 */
	protected GsonBuilder getGsonBuilder()
	{
		return gsonBuilder;
	}

	/**
	 * Obtains the GSON instance used for type conversions. Uses a
	 * {@link GsonBuilder} obtained via {@link BaseServiceTest#getGsonBuilder()}
	 * 
	 * @return A {@link Gson} instance
	 */
	protected Gson getGson()
	{
		return getGsonBuilder().create();
	}
	
	
	/**
	 * Utility class for parsing date/time strings into ZonedDateTime instances
	 */
	private static final class ZonedDateTimeDeserializer implements JsonDeserializer<ZonedDateTime>
	{
		private final ZonedDateTimeAdapter adapter = new ZonedDateTimeAdapter();
		@Override
		public ZonedDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			try {
				return adapter.unmarshal(json.getAsString());
			} catch (Exception e) {
				throw new JsonParseException(e);
			}
		}
	}
	
	/**
	 * Loads a JSON file with test data from src/test/resources/ and parses it.
	 * @param testResourceFileName The file path inside src/test/resources/
	 * @return The parsed test data
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public EventList loadTestEvents(final String testResourceFileName) throws URISyntaxException, IOException
	{
		final URI testDataURI = this.getClass().getResource(testResourceFileName).toURI();
		final byte[] testDataBytes = Files.readAllBytes(Paths.get(testDataURI));
		final String testData = new String(testDataBytes, StandardCharsets.UTF_8);
		
		final Gson gson = getGson();
		final EventList el = gson.fromJson(testData, EventList.class);
		
		return el;
	}
	
	public void insertAll(IEventDAO dao, final EventList el)
	{
		for(final Event ev : el.getEvents()) {
			dao.addEvent(ev);
		}
	}
	
	/**
	 * Parses an ISO 8601 date/time string into a Calendar instance
	 * 
	 * @param isoDateTime
	 *            The ISO 8601 date/time string to parse
	 * @return A Calendar instance representing the given date/time
	 * @throws Exception
	 *             If the given date/time string could not be parsed
	 */
	public Calendar cal(final String isoDateTime) throws Exception
	{
		CalendarAdapter parser = new CalendarAdapter();
		return parser.unmarshal(isoDateTime);
	}
	

	
	/**
	 * Converts a {@link Calendar} object to its ISO 8601 representation
	 * 
	 * @param dateTime
	 *            A Calendar instance representing a date/time
	 * @return The given date/time as an ISO 8601 date/time string
	 * @throws Exception
	 *             If the given date/time could not be converted to an ISO 8601 string
	 */
	public String calToStr(final Calendar dateTime) throws Exception
	{
		CalendarAdapter parser = new CalendarAdapter();
		return parser.marshal(dateTime);
	}
	
	/**
	 * Reads the body of a JAX-RS {@link Response} as a string. Buffering will
	 * be enabled on the response before the read is performed.
	 * 
	 * @param resp
	 * @return
	 */
	public String readResponseBodyAsString(final Response resp)
	{
		// buffer the response, in case we want to read it twice
		resp.bufferEntity();
		return resp.readEntity(String.class);
	}
	
	
	/**
	 * Reads the body from a Jersey {@link Response} and parses it into an
	 * {@link EventList}.
	 * 
	 * @param resp
	 *            The Jersey {@link Response} to parse
	 * @return An {@link EventList} containing the contents of the response
	 * @throws JsonSyntaxException
	 *             If the response body was unable to be converted to an
	 *             {@link EventList}
	 */
	public EventList readResponseBodyAsEventList(final Response resp)
	{
		final String respBodyJson = readResponseBodyAsString(resp);
		final EventList receivedEl = getGson().fromJson(respBodyJson, EventList.class);
		return receivedEl;
	}
	
	
	
	/**
	 * Matches an {@link Event} object whose ID field has the given value.
	 * 
	 * @param id
	 *            The ID to match
	 * @return A {@link Matcher} which will match an {@link Event} with the
	 *         given ID. The Matcher will have the appropriate generic type.
	 */
	protected Matcher<Event> eventWithId(final long id)
	{
		return hasProperty("id", equalTo(id));
	}
	
	/**
	 * Matches an {@link Event} object whose ID field has the given value.
	 * 
	 * @param id
	 *            The ID to match
	 * @return A {@link Matcher} which will match an {@link Event} with the
	 *         given ID. The Matcher will have the appropriate generic type.
	 */
	protected Matcher<Event> isEventWithId(final long id)
	{
		return eventWithId(id);
	}
	
	

	
	/**
	 * An {@link Event} subclass with a simpler (less verbose) toString
	 * implementation, which only displays the Event ID.
	 */
	public static class EventWithIDOnlyToString extends Event
	{
		@Override
		public String toString()
		{
			StringBuilder builder = new StringBuilder();
			builder.append("Event [id=");
			builder.append(getId());
			builder.append("]");
			return builder.toString();
		}
	}
	
	/**
	 * Converts Events into a subclass whose <code>toString</code> only prints
	 * the ID.
	 * 
	 * @param inputList
	 *            The original Events
	 * @return A new {@link List} of new {@link Event} instances, of a subtype
	 *         which only prints the ID field in the toString.
	 */
	public List<Event> wrapWithIDOnlyToString(final List<Event> inputList)
	{
		final List<Event> output = new ArrayList<Event>(inputList.size());
		
		for(final Event existing : inputList)
		{
			output.add(existing.copy(new EventWithIDOnlyToString()));
		}
		
		return output;
	}
}
