package com.example.services;

import static com.example.IsCollectionContainingInAnyOrder.isCollectionContainingInAnyOrder;
import static com.example.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.jetty.server.Server;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.runners.MethodSorters;

import com.example.BaseServiceTest;
import com.example.Main;
import com.example.dao.IEventDAO;
import com.example.dao.MemoryEventDAO;
import com.example.models.Event;
import com.example.models.EventList;

@RunWith(JUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestEventServices extends BaseServiceTest
{
	/** Port the local server will run on during the tests */
	private static final int PORT = 9386;
	private static Server server = null;
	
	/** DAO used in the tests */
	private IEventDAO dao;
	
	Client client = null;
	/** The base web service URL, http://localhost:PORT/services/event/ (see the setup() method) */
	WebTarget baseService = null;
	
	/////////////////////////////////////////////////////////////
	// 
	// JUnit Lifecycle Methods
	// 
	/////////////////////////////////////////////////////////////
	
	/**
	 * Start up server instance which will host the web services for the unit
	 * tests. The same server instance will be used for all tests in this class.
	 * 
	 * @throws Exception
	 */
	@BeforeClass
	public static void startServer() throws Exception
	{
		TestEventServices.server = Main.startServer("src/main/webapp/", PORT);
	}
	
	/**
	 * Stops the web service server cleanly after all the unit test methods in
	 * this class have finished.
	 * 
	 * @throws Exception
	 */
	@AfterClass
	public static void stopServer() throws Exception
	{
		TestEventServices.server.stop();
		TestEventServices.server.join();
	}
	
	/**
	 * Common setup steps, run before each of the unit test methods.
	 */
	@Before
	public void setup()
	{
		this.dao = new MemoryEventDAO();
		
		MemoryEventDAO.resetMemDB();
		assertThat(dao.getAllEvents(), is(empty()), "DAO should start off empty");
		
		this.client = ClientBuilder.newClient();
		this.baseService = client
			.target(String.format("http://localhost:%d", PORT))
			.path("services")
			.path("event");
	}
	
	/////////////////////////////////////////////////////////////
	// 
	// Unit tests
	// 
	/////////////////////////////////////////////////////////////
	

	/**
	 * Test the get all with an empty DB
	 * Tests {@link EventService#getAllEvents()}
	 */
	@Test
	public void getAllEvents_EmptyDB()
	{
		// Call the get all events service
		// Implementation: EventService.getAllEvents
		final Response resp = baseService.path("all")
			.request(MediaType.APPLICATION_JSON_TYPE)
			.get();
		
		final String respBodyJson = readResponseBodyAsString(resp);
		final EventList receivedEl = getGson().fromJson(respBodyJson, EventList.class);

		assertThat(receivedEl, is(notNullValue()));
		assertThat(receivedEl.getEvents(), hasSize(0));
	}
	
	
	/**
	 * Tests {@link EventService#addEvent(Event)}
	 * @throws Exception
	 */
	@Test
	public void addEvent_MultipleInsertion() throws URISyntaxException, IOException
	{
		final EventList el = loadTestEvents("/CalData1.json");
		
		int id = 1;
		for(final Event ev : el.getEvents())
		{
			ev.setId(id);

			// Call the add event service
			// Implementation: EventService.addEvent
			final Response resp = baseService
				.request(MediaType.APPLICATION_JSON_TYPE)
				.post(Entity.entity(ev, MediaType.APPLICATION_JSON_TYPE));
			
			assertThat(resp.getStatus(), is(201), "Expected response status 201 Created");
			assertThat(resp.getHeaderString("Location"), endsWith("/" + id), "Expected Location header in response");
			
			++id;
		}
		
		final int numEventsLoaded = el.getEvents().size();
		assertThat(dao.getAllEvents(), hasSize(numEventsLoaded), "All events should have been added");
		
		for(int i = 0; i < numEventsLoaded; ++i)
		{
			final Event fromList = el.getEvents().get(i); // list indexes start at 0
			final Event fromDB = dao.getEvent(i+1); // DB IDs start at 1
			assertThat(fromDB, is(equalTo(fromList)), "Event should have been added to DB");
		}
	}
	
	
	/**
	 * Tests {@link EventService#updateEvent(long, Event)}
	 * @throws Exception
	 */
	@Test
	public void updateEvent() throws URISyntaxException, IOException
	{
		final EventList el = loadTestEvents("/CalDataTestUpdate.json");
		
		final Event e1 = el.getEvents().get(0);
		final Event e1UpdatedFromFile = el.getEvents().get(1);
		
		// Add the original event to the DB
		dao.addEvent(e1);
		assertThat(dao.getAllEvents(), hasSize(1));
		assertThat(dao.getEvent(e1.getId()), is(equalTo(e1)));
		
		// Call the update service
		// Implementation: EventService.updateEvent
		final Response resp = baseService
			.path(Long.toString(e1UpdatedFromFile.getId()))
			.request(MediaType.APPLICATION_JSON_TYPE)
			.put(Entity.entity(e1UpdatedFromFile, MediaType.APPLICATION_JSON_TYPE));
		
		// verify response headers
		assertThat(resp.getStatus(), is(204), "Expected response status 204 No Content");
		assertThat(resp.getHeaderString("Location"), endsWith("/1"), "Expected Location header in response");
		
		// Check the DB to make sure the service updated the event
		assertThat(dao.getAllEvents(), hasSize(1));
		final Event e1UpdatedFromService = dao.getEvent(e1.getId());
		assertThat(e1UpdatedFromService, is(equalTo(e1UpdatedFromFile)));
	}
	
	
	/**
	 * Tests {@link EventService#deleteEvent(long)}
	 * @throws Exception
	 */
	@Test
	public void deleteEvent() throws Exception
	{
		final EventList loadedEvents = loadTestEvents("/CalDataTestDelete.json");
		final Event e1 = loadedEvents.getEvents().get(0);
		final Event e2 = loadedEvents.getEvents().get(1);
		
		dao.addEvent(e1);
		dao.addEvent(e2);
		assertThat(dao.getAllEvents(), hasSize(2));
		
		// Call the delete service
		// Implementation: EventService.deleteEvent
		final Response resp = baseService
			.path(Long.toString(e1.getId()))
			.request()
			.delete();
		
		// verify response headers
		assertThat(resp.getStatus(), is(204), "Expected response status 204 No Content");
		
		// Make sure the event is no longer in the DB
		assertThat(dao.getAllEvents(), hasSize(1));
		
		// Only e2 should be left
		assertThat(dao.getEvent(e1.getId()), is(nullValue()), "Event 1 should no longer be present");
		assertThat(dao.getEvent(e2.getId()), is(equalTo(e2)), "Event 2 should be untouched");
	}
	
	
	/**
	 * Tests {@link EventService#getEventsByDateRange(int, int, String, int, int, String)}
	 * @throws Exception
	 */
	@Test
	public void getEventsByDateRange_SingleDay() throws Exception
	{
		final EventList loadedEventsList = loadTestEvents("/CalDataTestBetween.json");
		insertAll(dao, loadedEventsList);
		
		// Call the search by date range service to get all events on January 1, 2014
		// Implementation: EventService.getEventsByDateRange
		final Response resp = baseService
			.path("/from/2014/01/01/to/2014/01/01")
			.request(MediaType.APPLICATION_JSON_TYPE)
			.get();
		
		// parse the response into an EventList object
		final EventList receivedEl = readResponseBodyAsEventList(resp);
		final List<Event> receivedEvents = receivedEl.getEvents();
		final List<Event> receivedEventsNeat = wrapWithIDOnlyToString(receivedEvents);
		
		// validate that the response contained all of the Jan 01 events, and only those
		assertThat(receivedEventsNeat, isCollectionContainingInAnyOrder(
			eventWithId(1), 
			eventWithId(2), 
			eventWithId(3), 
			eventWithId(4), 
			eventWithId(5), 
			eventWithId(6), 
			eventWithId(7), 
			eventWithId(8)
		));
	}
	
	
	/**
	 * Tests {@link EventService#getEventsByDateRange(int, int, String, int, int, String)}
	 * @throws Exception
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void getEventsByDateRange_WholeMonth() throws Exception
	{
		final EventList loadedEvents = loadTestEvents("/CalDataTestBetweenSpanningMonths.json");
		insertAll(dao, loadedEvents);
		
		// call the search by date range service to get all the events from February
		// Implementation: EventService.getEventsByDateRange
		final Response resp = baseService
			.path("/from/2014/02/to/2014/02")
			.request(MediaType.APPLICATION_JSON_TYPE)
			.get();
		
		// parse the response into an EventList object
		final EventList receivedEl = readResponseBodyAsEventList(resp);
		final List<Event> receivedEvents = receivedEl.getEvents();
		
		// verify that we got back the expected events, in order, with nothing extra
		assertThat(receivedEvents, contains(
			eventWithId(3), 
			eventWithId(4)
		));
	}
	
	
	/**
	 * Tests {@link EventService#getEventsById(String)}
	 * @throws Exception
	 */
	@Test
	public void getEventsById_GetSingleEvent() throws Exception
	{
		final EventList loadedEvents = loadTestEvents("/CalData1.json");
		insertAll(dao, loadedEvents);
		
		
		// call the get event by ID service to get event 1
		// Implementation: EventService.getEventsById
		final Response resp = baseService
			.path("/2")
			.request(MediaType.APPLICATION_JSON_TYPE)
			.get();
		
		// parse the response into an EventList object
		final EventList receivedEl = readResponseBodyAsEventList(resp);
		final List<Event> receivedEvents = receivedEl.getEvents();
		
		// verify that we received exactly event 2
		assertThat(receivedEvents, contains(
			eventWithId(2)
		));
	}
	
	
	/**
	 * Tests {@link EventService#getEventsById(String)}
	 * @throws Exception
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void getEventsById_MultipleEventIDs() throws Exception
	{
		final EventList loadedEvents = loadTestEvents("/CalData1.json");
		insertAll(dao, loadedEvents);
		
		
		// call the get event by ID service to get events 1, 3, and 7
		// Implementation: EventService.getEventsById
		final Response resp = baseService
			.path("/1+3+7")
			.request(MediaType.APPLICATION_JSON_TYPE)
			.get();
		
		// parse the response into an EventList object
		final EventList receivedEl = readResponseBodyAsEventList(resp);
		final List<Event> receivedEvents = receivedEl.getEvents();
		
		// verify that we received exactly events 1, 3, and 7, in order
		assertThat(receivedEvents, contains(
			eventWithId(1),
			eventWithId(3), 
			eventWithId(7)
		));
	}
	
	
	/**
	 * Tests {@link EventService#getEventsById(String)}
	 * @throws Exception
	 */
	@Test
	public void getEventsById_NonexistantEvent() throws Exception
	{
		final EventList loadedEvents = loadTestEvents("/CalData1.json");
		insertAll(dao, loadedEvents);
		
		
		// call the get event by ID service to get event 100 (which doesn't exist)
		// Implementation: EventService.getEventsById
		final Response resp = baseService
			.path("/100")
			.request(MediaType.APPLICATION_JSON_TYPE)
			.get();
		
		// parse the response into an EventList object
		final EventList receivedEl = readResponseBodyAsEventList(resp);
		final List<Event> receivedEvents = receivedEl.getEvents();
		
		// verify that we received exactly 1 event, which was null
		assertThat(receivedEvents, contains(
			nullValue()
		));
	}
	
	
	/**
	 * Tests {@link EventService#getEventsById(String)}
	 * @throws Exception
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void getEventsById_MultipleEventIDsIncludingOneNonexistant() throws Exception
	{
		final EventList loadedEvents = loadTestEvents("/CalData1.json");
		insertAll(dao, loadedEvents);
		
		
		// call the get event by ID service to get events 1, 100, and 2. (100
		// doesn't exist and should come back null.)
		// Implementation: EventService.getEventsById
		final Response resp = baseService
			.path("/1+100+2")
			.request(MediaType.APPLICATION_JSON_TYPE)
			.get();
		
		// parse the response into an EventList object
		final EventList receivedEl = readResponseBodyAsEventList(resp);
		final List<Event> receivedEvents = receivedEl.getEvents();
		
		// verify that we received exactly the requested events 
		// (ID 1, null, ID 2), in that order, with no extras
		assertThat(receivedEvents, contains(
			eventWithId(1), 
			nullValue(), 
			eventWithId(2)
		));
	}
	
	
	/**
	 * Tests {@link EventService#getEventsByTag(String)}
	 * @throws Exception
	 */
	@Test
	@Ignore
	@SuppressWarnings("unchecked")
	public void getEventsByTag_SingleTag() throws Exception
	{
		final EventList loadedEvents = loadTestEvents("/CalDataTestTags.json");
		insertAll(dao, loadedEvents);
		
		// call the get events by tag service to get events with tag "alpha"
		// Implementation: EventService.getEventsByTag
		final Response resp = baseService
			.path("/tag/alpha")
			.request(MediaType.APPLICATION_JSON_TYPE)
			.get();
		
		// parse the response into an EventList object
		final EventList receivedEl = readResponseBodyAsEventList(resp);
		final List<Event> receivedEvents = receivedEl.getEvents();
		final List<Event> receivedEventsNeat = wrapWithIDOnlyToString(receivedEvents);
		
		// verify that we received exactly the requested events 
		// (ID 1, ID 3, ID 6), in that order, with no extras
		assertThat(receivedEventsNeat, contains(
			eventWithId(1), 
			eventWithId(3), 
			eventWithId(6)
		));
	}
	
	
	/**
	 * Tests {@link EventService#getEventsByTag(String)}
	 * @throws Exception
	 */
	@Test
	@Ignore
	@SuppressWarnings("unchecked")
	public void getEventsByTag_MultipleTags() throws Exception
	{
		final EventList loadedEvents = loadTestEvents("/CalDataTestTags.json");
		insertAll(dao, loadedEvents);
		
		// call the get events by tag service to get events with tags "alpha" and "beta"
		// Implementation: EventService.getEventsByTag
		final Response resp = baseService
			.path("/tag/alpha+beta")
			.request(MediaType.APPLICATION_JSON_TYPE)
			.get();
		
		// parse the response into an EventList object
		final EventList receivedEl = readResponseBodyAsEventList(resp);
		final List<Event> receivedEvents = receivedEl.getEvents();
		final List<Event> receivedEventsNeat = wrapWithIDOnlyToString(receivedEvents);
		
		// verify that we received exactly the requested events 
		// (ID 3, ID 6), in that order, with no extras
		assertThat(receivedEventsNeat, contains(
			eventWithId(3), 
			eventWithId(6)
		));
	}
}
