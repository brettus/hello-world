package com.example.dao;

import static com.example.IsCollectionContainingInAnyOrder.isCollectionContainingInAnyOrder;
import static com.example.IsEventWithTags.*;
import static com.example.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.List;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.runners.MethodSorters;

import com.example.BaseServiceTest;
import com.example.models.Event;
import com.example.models.EventList;


/**
 * Tests for the MemoryEventDAO class.<br>
 * <br>
 * Assumptions: Generated IDs start at 1 and are incremented by 1 for every new event inserted.
 */
@RunWith(JUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestMemoryEventDAO extends BaseServiceTest
{
	private IEventDAO dao = null;
	
	// setup methods
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Before
	public void resetDAO() throws Exception
	{
		this.dao = new MemoryEventDAO();
		
		MemoryEventDAO.resetMemDB();
		assertThat(dao.getAllEvents(), is(empty()), "DAO should start off empty");
	}
	
	// tests
	
	@Test
	public void addEvent_BasicAdd() throws Exception
	{
		final EventList loadedEventList = loadTestEvents("/CalData1.json");
		final List<Event> loadedEvents = loadedEventList.getEvents();
		
		final Event e1 = loadedEvents.get(0);
		dao.addEvent(e1);
		assertThat(dao.getAllEvents(), contains(e1));
	}
	
	
	@Test
	public void addEvent_MultiInsert() throws Exception
	{
		final EventList loadedEventList = loadTestEvents("/CalData1.json");
		final List<Event> loadedEvents = loadedEventList.getEvents();
		
		for(final Event ev : loadedEvents)
		{
			dao.addEvent(ev);
		}
		
		assertThat(dao.getAllEvents(), isCollectionContainingInAnyOrder(loadedEvents));
	}
	
	
	@Test
	public void addEvent_InsertionGeneratesIDs() throws Exception
	{
		EventList loadedEventList = loadTestEvents("/CalData1.json");
		
		// starting with an empty DB, we expect new IDs to get generated as we insert elements
		// starting with 1
		long currentId = 1L;
		for(final Event ev : loadedEventList.getEvents())
		{
			final Event added = dao.addEvent(ev);
			assertThat(added.getId(), is(currentId), "ID mismatch");
			
			++currentId;
		}
	}
	
	@Test
	public void getAllEvents() throws Exception
	{
		final EventList loadedEventList = loadTestEvents("/CalDataTestGetAll.json");
		final List<Event> loadedEvents = loadedEventList.getEvents();
		insertAll(dao, loadedEventList);
		
		assertThat(dao.getAllEvents(), isCollectionContainingInAnyOrder(loadedEvents), 
				"DB contents differed from expected");
	}
	
	@Test
	public void getEvent_GetAllOneByOne() throws Exception
	{
		final EventList loadedEventList = loadTestEvents("/CalDataTestGetAll.json");
		final List<Event> loadedEvents = loadedEventList.getEvents();
		insertAll(dao, loadedEventList);
		
		for(int i = 0; i < loadedEvents.size(); ++i)
		{
			final Event ev = loadedEvents.get(i);
			final long eventId = ev.getId();
			assertThat(dao.getEvent(eventId), is(ev));
		}
	}
	
	@Test
	public void remove_RemoveActuallyRemovesEvent() throws Exception
	{
		final EventList loadedEventList = loadTestEvents("/CalData1.json");
		final List<Event> loadedEvents = loadedEventList.getEvents();
		
		final Event e1 = loadedEvents.get(0);
		dao.addEvent(e1);
		
		dao.removeEvent(e1);
		assertThat(dao.getAllEvents(), is(empty()));
	}
	
	@Test
	public void remove_RemoveReturnsRemovedEvent() throws Exception
	{
		final EventList loadedEventList = loadTestEvents("/CalData1.json");
		final List<Event> loadedEvents = loadedEventList.getEvents();
		
		final Event e1 = loadedEvents.get(0);
		
		dao.addEvent(e1);
		
		final Event e1Removed = dao.removeEvent(e1);
		assertThat(e1Removed, is(e1));
	}
	
	@Test
	public void remove_RemovingNonExistantEventReturnsNull() throws Exception
	{
		final EventList loadedEventList = loadTestEvents("/CalData1.json");
		final List<Event> loadedEvents = loadedEventList.getEvents();
		
		final Event e1 = loadedEvents.get(0);
		final Event e2 = loadedEvents.get(1);
		
		dao.addEvent(e1);
		
		// try removing a non-existent element
		final Event e2Removed = dao.removeEvent(e2);
		assertThat(e2Removed, is(nullValue()));
	}
	
	@Test
	public void remove_RemovingNonExistantEventDoesntAffectOtherData() throws Exception
	{
		final EventList loadedEventList = loadTestEvents("/CalData1.json");
		final List<Event> loadedEvents = loadedEventList.getEvents();
		
		final Event e1 = loadedEvents.get(0);
		final Event e2 = loadedEvents.get(1);
		
		dao.addEvent(e1);
		dao.removeEvent(e2);
		
		assertThat(dao.getAllEvents(), contains(e1));
	}
	
	@Test
	public void remove_RemovingEventDoesntAffectOtherEvents() throws Exception
	{
		final EventList loadedEventList = loadTestEvents("/CalData1.json");
		final List<Event> loadedEvents = loadedEventList.getEvents();
		
		final Event e1 = loadedEvents.get(0);
		final Event e2 = loadedEvents.get(1);
		
		dao.addEvent(e1);
		dao.addEvent(e2);
		dao.removeEvent(e2);
		
		assertThat(dao.getAllEvents(), contains(e1));
	}
	
	
	/**
	 * This test expects to get back events which are fully contained within the search period, 
	 * including some which exactly match the search period 
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void getEventsBetweenPartial_FullContainmentIncludingEqualEndpoints() throws Exception
	{
		final EventList loadedEventList = loadTestEvents("/CalDataTestBetween.json");
		insertAll(dao, loadedEventList);
		
		final Calendar start = cal("2014-01-01T10:00:00Z");
		final Calendar end = cal("2014-01-01T12:00:00Z");
		
		final List<Event> foundEvents = dao.getEventsBetweenPartial(start, end);
		assertThat(foundEvents, containsInAnyOrder(
				eventWithId(3), 
				eventWithId(4), 
				eventWithId(5), 
				eventWithId(6)
		));
	}
	
	
	/**
	 * This test expects to get back events where the event start matches the search start, 
	 * but the event end is after the search end
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void getEventsBetweenPartial_EventEndGoesPastSearchEnd() throws Exception
	{
		final EventList loadedEventList = loadTestEvents("/CalDataTestBetween.json");
		insertAll(dao, loadedEventList);
		
		final Calendar start = cal("2014-01-01T10:00:00Z");
		final Calendar end = cal("2014-01-01T10:29:00Z");
		
		final List<Event> foundEvents = dao.getEventsBetweenPartial(start, end);
		assertThat(foundEvents, containsInAnyOrder(
				eventWithId(3), 
				eventWithId(4)
		));
	}
	
	
	/**
	 * This test verifies that an event which only "touches" the start of search period
	 * is not contained in the results 
	 */
	@Test
	public void getEventsBetweenPartial_EventWhichTouchesSearchStartDoesntMatch() throws Exception
	{
		final EventList loadedEventList = loadTestEvents("/CalDataTestBetween.json");
		insertAll(dao, loadedEventList);
		
		final Calendar start = cal("2014-01-01T09:00:00Z");
		final Calendar end = cal("2014-01-01T09:30:00Z");
		
		final List<Event> foundEvents = dao.getEventsBetweenPartial(start, end);
		assertThat(foundEvents, contains(
				eventWithId(2)
		));
	}
	
	
	/**
	 * This test verifies that an event which only "touches" the end of search period
	 * is not contained in the results 
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void getEventsBetweenPartial_EventWhichTouchesSearchEndDoesntMatch() throws Exception
	{
		final EventList loadedEventList = loadTestEvents("/CalDataTestBetween.json");
		insertAll(dao, loadedEventList);
		
		final Calendar start = cal("2014-01-01T10:00:00Z");
		final Calendar end = cal("2014-01-01T10:30:00Z");
		
		final List<Event> foundEvents = dao.getEventsBetweenPartial(start, end);
		assertThat(foundEvents, containsInAnyOrder(
				eventWithId(3), 
				eventWithId(4)
		));
	}
	
	
	/**
	 * This test verifies a search period which comes before any events doesn't match anything
	 */
	@Test
	public void getEventsBetweenPartial_SearchPeriodBeforeAnyEvents() throws Exception
	{
		final EventList loadedEventList = loadTestEvents("/CalDataTestBetween.json");
		insertAll(dao, loadedEventList);
		
		final Calendar start = cal("2014-01-01T06:00:00Z");
		final Calendar end = cal("2014-01-01T07:00:00Z");
		
		final List<Event> foundEvents = dao.getEventsBetweenPartial(start, end);
		assertThat(foundEvents, is(empty()));
	}
	
	
	/**
	 * This test verifies a search period which comes after any events doesn't match anything
	 */
	@Test
	public void getEventsBetweenPartial_SearchPeriodAfterAnyEvents() throws Exception
	{
		final EventList loadedEventList = loadTestEvents("/CalDataTestBetween.json");
		insertAll(dao, loadedEventList);
		
		final Calendar start = cal("2014-01-01T15:00:00Z");
		final Calendar end = cal("2014-01-01T16:00:00Z");
		
		final List<Event> foundEvents = dao.getEventsBetweenPartial(start, end);
		assertThat(foundEvents, is(empty()));
	}
	
	
	/**
	 * This test verifies that getEventsBetweenPartial properly rejects invalid
	 * ranges (start time after end time)
	 */
	@Test
	public void getEventsBetweenPartial_StartTimeAfterEndTime() throws Exception
	{
		final EventList loadedEventList = loadTestEvents("/CalDataTestBetween.json");
		insertAll(dao, loadedEventList);
		
		final Calendar start = cal("2014-01-01T10:00:00Z");
		final Calendar end = cal("2014-01-01T09:00:00Z");
		
		thrown.expect(IllegalArgumentException.class);
		dao.getEventsBetweenPartial(start, end);
	}
	
	
	/**
	 * This test verifies that a search period which only covers part of test
	 * events doesn't match anything
	 */
	@Test
	public void getEventsBetweenComplete_PartialContainment() throws Exception
	{
		final EventList loadedEventList = loadTestEvents("/CalDataTestBetween.json");
		insertAll(dao, loadedEventList);
		
		final Calendar start = cal("2014-01-01T10:00:00Z");
		final Calendar end = cal("2014-01-01T10:29:00Z");
		
		final List<Event> foundEvents = dao.getEventsBetweenComplete(start, end);
		assertThat(foundEvents, is(empty()));
	}
	
	
	/**
	 * This test verifies that a search period which completely contains one
	 * event and partially contains other only matches the completely-contained
	 * events
	 */
	@Test
	public void getEventsBetweenComplete_ContainsSomeEventsButOnlyPartsOfOthers() throws Exception
	{
		final EventList loadedEventList = loadTestEvents("/CalDataTestBetween.json");
		insertAll(dao, loadedEventList);
		
		final Calendar start = cal("2014-01-01T09:59:00Z");
		final Calendar end = cal("2014-01-01T11:01:00Z");
		
		final List<Event> foundEvents = dao.getEventsBetweenComplete(start, end);
		assertThat(foundEvents, contains(
				eventWithId(4)
		));
	}
	
	
	/**
	 * This test expects to get back events which are fully contained within the search period, 
	 * including some which exactly match the search period 
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void getEventsBetweenComplete_FullContainmentIncludingEqualEndpoints() throws Exception
	{
		final EventList loadedEventList = loadTestEvents("/CalDataTestBetween.json");
		insertAll(dao, loadedEventList);
		
		final Calendar start = cal("2014-01-01T10:00:00Z");
		final Calendar end = cal("2014-01-01T12:00:00Z");
		
		final List<Event> foundEvents = dao.getEventsBetweenComplete(start, end);
		assertThat(foundEvents, containsInAnyOrder(
				eventWithId(3), 
				eventWithId(4), 
				eventWithId(5), 
				eventWithId(6)
		));
	}
	
	
	/**
	 * This test verifies that an event which only "touches" the start of search period
	 * is not contained in the results 
	 */
	@Test
	public void getEventsBetweenComplete_EventWhichTouchesSearchStartDoesntMatch() throws Exception
	{
		final EventList loadedEventList = loadTestEvents("/CalDataTestBetween.json");
		insertAll(dao, loadedEventList);
		
		final Calendar start = cal("2014-01-01T09:00:00Z");
		final Calendar end = cal("2014-01-01T10:01:00Z");
		
		final List<Event> foundEvents = dao.getEventsBetweenComplete(start, end);
		assertThat(foundEvents, contains(
				eventWithId(2)
		));
	}
	
	
	/**
	 * This test verifies that an event which only "touches" the end of search period
	 * is not contained in the results 
	 */
	@Test
	public void getEventsBetweenComplete_EventWhichTouchesSearchEndDoesntMatch() throws Exception
	{
		final EventList loadedEventList = loadTestEvents("/CalDataTestBetween.json");
		insertAll(dao, loadedEventList);
		
		final Calendar start = cal("2014-01-01T11:59:00Z");
		final Calendar end = cal("2014-01-01T13:00:00Z");
		
		final List<Event> foundEvents = dao.getEventsBetweenComplete(start, end);
		assertThat(foundEvents, contains(
				eventWithId(7)
		));
	}
	
	
	/**
	 * This test verifies a search period which comes before any events doesn't match anything
	 */
	@Test
	public void getEventsBetweenComplete_SearchPeriodBeforeAnyEvents() throws Exception
	{
		final EventList loadedEventList = loadTestEvents("/CalDataTestBetween.json");
		insertAll(dao, loadedEventList);
		
		final Calendar start = cal("2014-01-01T06:00:00Z");
		final Calendar end = cal("2014-01-01T07:00:00Z");
		
		final List<Event> foundEvents = dao.getEventsBetweenComplete(start, end);
		assertThat(foundEvents, is(empty()));
	}
	
	
	/**
	 * This test verifies a search period which comes after any events doesn't match anything
	 */
	@Test
	public void getEventsBetweenComplete_SearchPeriodAfterAnyEvents() throws Exception
	{
		final EventList loadedEventList = loadTestEvents("/CalDataTestBetween.json");
		insertAll(dao, loadedEventList);
		
		final Calendar start = cal("2014-01-01T15:00:00Z");
		final Calendar end = cal("2014-01-01T16:00:00Z");
		
		final List<Event> foundEvents = dao.getEventsBetweenComplete(start, end);
		assertThat(foundEvents, is(empty()));
	}
	
	
	/**
	 * This test verifies that getEventsBetweenComplete properly rejects invalid
	 * ranges (start time after end time)
	 */
	@Test
	public void getEventsBetweenComplete_StartTimeAfterEndTime() throws Exception
	{
		final EventList loadedEventList = loadTestEvents("/CalDataTestBetween.json");
		insertAll(dao, loadedEventList);
		
		final Calendar start = cal("2014-01-01T10:00:00Z");
		final Calendar end = cal("2014-01-01T09:00:00Z");
		
		thrown.expect(IllegalArgumentException.class);
		dao.getEventsBetweenComplete(start, end);
	}
	
	
	@Test
	public void update_UpdatingReplacesOriginalEvent() throws URISyntaxException, IOException
	{
		final EventList loadedEventList = loadTestEvents("/CalDataTestUpdate.json");
		final List<Event> loadedEvents = loadedEventList.getEvents();
		
		final Event e1        = loadedEvents.get(0);
		final Event e1Updated = loadedEvents.get(1);
		
		dao.addEvent(e1);
		dao.updateEvent(e1Updated);
		
		final Event e1PostUpdate = dao.getEvent(e1.getId());
		assertThat(e1PostUpdate, is(e1Updated), "Event ID 1 should have been updated");
	}
	
	@Test
	public void update_UpdatingReturnsOldEvent() throws URISyntaxException, IOException
	{
		final EventList loadedEventList = loadTestEvents("/CalDataTestUpdate.json");
		final List<Event> loadedEvents = loadedEventList.getEvents();
		
		final Event e1        = loadedEvents.get(0);
		final Event e1Updated = loadedEvents.get(1);
		
		dao.addEvent(e1);
		
		final Event e1PreUpdate = dao.updateEvent(e1Updated);
		assertThat(e1PreUpdate, is(equalTo(e1)), "Updating should have returned old event");
	}
	
	@Test
	public void update_UpdatingNonexistantIDReturnsNull() throws URISyntaxException, IOException
	{
		final EventList loadedEventList = loadTestEvents("/CalDataTestUpdate.json");
		final List<Event> loadedEvents = loadedEventList.getEvents();
		
		final Event e1        = loadedEvents.get(0);
		final Event e2        = loadedEvents.get(2);
		
		dao.addEvent(e1);
		
		final Event e2UpdateReturnValue = dao.updateEvent(e2);
		assertThat(e2UpdateReturnValue, is(nullValue()), "There should have been no event with ID 2 to update");
	}
	
	@Test
	public void update_UpdatingNonexistantIDIsANoop() throws URISyntaxException, IOException
	{
		final EventList loadedEventList = loadTestEvents("/CalDataTestUpdate.json");
		final List<Event> loadedEvents = loadedEventList.getEvents();
		
		final Event e1        = loadedEvents.get(0);
		final Event e2        = loadedEvents.get(2);
		
		dao.addEvent(e1);
		dao.updateEvent(e2);
		
		final Event e2FromDBAfterUpdate = dao.getEvent(e2.getId());
		assertThat(e2FromDBAfterUpdate, is(nullValue()), "Event 2 should not exist in the database");
	}
	
	
	@Test
	@Ignore
	public void getByTag_EmptyTagListDoesNotReturnNull() throws URISyntaxException, IOException
	{
		// In this test file, no two events have exactly the same set of tags
		final EventList loadedEventList = loadTestEvents("/CalDataTestTags.json");
		insertAll(dao, loadedEventList);
		
		final List<Event> results = dao.getEventsByTag();
		assertThat(results, is(notNullValue()), "Result have should not have been null");
	}

	
	@Test
	@Ignore
	public void getByTag_EmptyTagListReturnsEmptyCollection() throws URISyntaxException, IOException
	{
		// In this test file, no two events have exactly the same set of tags
		final EventList loadedEventList = loadTestEvents("/CalDataTestTags.json");
		insertAll(dao, loadedEventList);
		
		final List<Event> results = dao.getEventsByTag();
		assertThat(results, is(empty()), "Result have should have been an empty list");
	}
	
	
	@Test
	@Ignore
	public void getByTag_NonexistantTagDoesNotReturnNull() throws URISyntaxException, IOException
	{
		// In this test file, no two events have exactly the same set of tags
		final EventList loadedEventList = loadTestEvents("/CalDataTestTags.json");
		insertAll(dao, loadedEventList);
		
		final List<Event> emptyResults = dao.getEventsByTag("nonexistanttag");
		assertThat(emptyResults, is(empty()), "Result have should have been an empty list");
	}
	
	@Test
	@Ignore
	@SuppressWarnings("unchecked")
	public void getByTag_SearchBySingleTag() throws URISyntaxException, IOException
	{
		// In this test file, no two events have exactly the same set of tags
		final EventList loadedEventList = loadTestEvents("/CalDataTestTags.json");
		insertAll(dao, loadedEventList);
		
		final List<Event> alphaResults = dao.getEventsByTag("alpha");
		assertThat(alphaResults, containsInAnyOrder(
				eventWithTags("alpha"), 
				eventWithTags("alpha", "beta"), 
				eventWithTags("alpha", "beta", "gamma")
		), "Didn't find expected items");
		
		final List<Event> betaResults = dao.getEventsByTag("beta");
		assertThat(betaResults, containsInAnyOrder(
				eventWithTags("beta"), 
				eventWithTags("alpha", "beta"), 
				eventWithTags("beta", "gamma"), 
				eventWithTags("alpha", "beta", "gamma")
		), "Didn't find expected items");
		
		final List<Event> gammaResults = dao.getEventsByTag("gamma");
		assertThat(gammaResults, containsInAnyOrder(
				eventWithTags("gamma"), 
				eventWithTags("beta", "gamma"), 
				eventWithTags("alpha", "beta", "gamma")
		), "Didn't find expected items");
	}
	
	@Test
	@Ignore
	@SuppressWarnings("unchecked")
	public void getByTag_SearchByTwoTags() throws URISyntaxException, IOException
	{
		// In this test file, no two events have exactly the same set of tags
		final EventList loadedEventList = loadTestEvents("/CalDataTestTags.json");
		insertAll(dao, loadedEventList);
		
		final List<Event> alphaResults = dao.getEventsByTag("alpha", "beta");
		assertThat(alphaResults, containsInAnyOrder(
			eventWithTags("alpha", "beta"), 
			eventWithTags("alpha", "beta", "gamma")
		));
	}
	
	@Test
	@Ignore
	public void getByTag_SearchByMoreThanTwoTags() throws URISyntaxException, IOException
	{
		// In this test file, no two events have exactly the same set of tags
		final EventList loadedEventList = loadTestEvents("/CalDataTestTags.json");
		insertAll(dao, loadedEventList);
		
		final List<Event> alphaResults = dao.getEventsByTag("alpha", "beta", "gamma");
		assertThat(alphaResults, contains(eventWithTags("alpha", "beta", "gamma")));
	}
	
	@Test
	@Ignore
	public void getByTag_SearchByMultipleTagsOrderDoesntMatter() throws URISyntaxException, IOException
	{
		// In this test file, no two events have exactly the same set of tags
		final EventList loadedEventList = loadTestEvents("/CalDataTestTags.json");
		insertAll(dao, loadedEventList);
		
		final List<Event> alphaResults = dao.getEventsByTag("gamma", "alpha", "beta");
		assertThat(alphaResults, contains(eventWithTags("alpha", "beta", "gamma")));
	}
	
	@Test
	@Ignore
	public void getByTag_WithUpdatingToChangeTag() throws Exception
	{
		// In this test file, no two events have exactly the same set of tags
		final EventList loadedEventList = loadTestEvents("/CalDataTestTagsUpdate.json");
		final List<Event> loadedEvents = loadedEventList.getEvents();
		
		// all events in the test file have ID 1
		final Event alpha = loadedEvents.get(0);
		final Event beta = loadedEvents.get(1);
		
		// add in the initial event
		dao.addEvent(alpha);
		
		final List<Event> alphaResultsBeforeUpdate = dao.getEventsByTag("alpha");
		assertThat(alphaResultsBeforeUpdate, contains(alpha), "Didn't find expected items");
		
		final List<Event> betaResultsBeforeUpdate = dao.getEventsByTag("beta");
		assertThat(betaResultsBeforeUpdate, is(empty()), "No events should have tag beta");
		
		
		// update to change the tag to something else
		dao.updateEvent(beta);
		
		final List<Event> alphaResultsAfterUpdate = dao.getEventsByTag("alpha");
		assertThat(alphaResultsAfterUpdate, is(empty()), "No events should have tag alpha");
		
		final List<Event> betaResultsAfterUpdate = dao.getEventsByTag("beta");
		assertThat(betaResultsAfterUpdate, contains(beta), "Didn't find expected items");
	}
	
	@Test
	@Ignore
	public void getByTag_WithUpdatingToAddTag() throws Exception
	{
		// In this test file, no two events have exactly the same set of tags
		final EventList loadedEventList = loadTestEvents("/CalDataTestTagsUpdate.json");
		final List<Event> loadedEvents = loadedEventList.getEvents();
		
		// all events in the test file have ID 1
		final Event alpha = loadedEvents.get(0);
		final Event alphaBeta = loadedEvents.get(2);
		
		// add in the initial event
		dao.addEvent(alpha);
		
		final List<Event> alphaResultsBeforeUpdate = dao.getEventsByTag("alpha");
		assertThat(alphaResultsBeforeUpdate, contains(alpha), "Didn't find expected items");
		
		final List<Event> betaResultsBeforeUpdate = dao.getEventsByTag("beta");
		assertThat(betaResultsBeforeUpdate, is(empty()), "No events should have tag beta");
		
		
		// update to add another tag
		dao.updateEvent(alphaBeta);
		
		final List<Event> alphaResultsAfterUpdate = dao.getEventsByTag("alpha");
		assertThat(alphaResultsAfterUpdate, contains(alphaBeta), "Didn't find expected items");
		
		final List<Event> betaResultsAfterUpdate = dao.getEventsByTag("beta");
		assertThat(betaResultsAfterUpdate, contains(alphaBeta), "Didn't find expected items");
	}
	
	@Test
	@Ignore
	public void getByTag_WithUpdatingToRemoveTag() throws Exception
	{
		// In this test file, no two events have exactly the same set of tags
		final EventList loadedEventList = loadTestEvents("/CalDataTestTagsUpdate.json");
		final List<Event> loadedEvents = loadedEventList.getEvents();
		
		// all events in the test file have ID 1
		final Event alpha = loadedEvents.get(0);
		final Event alphaBeta = loadedEvents.get(2);
		
		// add in the initial event
		dao.addEvent(alphaBeta);
		
		final List<Event> alphaBetaResultsBeforeUpdate = dao.getEventsByTag("alpha", "beta");
		assertThat(alphaBetaResultsBeforeUpdate, contains(alphaBeta));
		
		
		// update to remove tag
		dao.updateEvent(alpha);
		
		final List<Event> alphaResultsAfterUpdate = dao.getEventsByTag("alpha");
		assertThat(alphaResultsAfterUpdate, contains(alpha));
		
		final List<Event> betaResultsAfterUpdate = dao.getEventsByTag("beta");
		assertThat(betaResultsAfterUpdate, is(empty()));
	}
	
	@Test
	@Ignore
	public void getByTag_AfterRemovingEventFromDB() throws Exception
	{
		// In this test file, no two events have exactly the same set of tags
		final EventList loadedEventList = loadTestEvents("/CalDataTestTagsUpdate.json");
		final List<Event> loadedEvents = loadedEventList.getEvents();
		
		final Event alpha = loadedEvents.get(0);
		final Event beta = loadedEvents.get(1);
		dao.addEvent(alpha);
		dao.addEvent(beta);
			
		final List<Event> alphaResults = dao.getEventsByTag("alpha");
		assertThat(alphaResults, contains(alpha));
		
		final List<Event> betaResults = dao.getEventsByTag("beta");
		assertThat(betaResults, contains(beta));
		
		// remove alpha, and verify that it was actually removed
		assertThat(dao.removeEvent(alpha), is(equalTo(alpha)));
		
		// query tags again and verify results
		// alpha should have disappeared, but beta should still be there 
		final List<Event> alphaResultsAfterRemove = dao.getEventsByTag("alpha");
		assertThat(alphaResultsAfterRemove, is(empty()));
		
		final List<Event> betaResultsAfterRemove = dao.getEventsByTag("beta");
		assertThat(betaResultsAfterRemove, contains(beta));
	}
}
