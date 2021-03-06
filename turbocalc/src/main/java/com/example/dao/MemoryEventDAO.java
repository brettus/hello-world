package com.example.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.example.exceptions.NotYetImplementedException;
import com.example.models.Event;

public class MemoryEventDAO implements IEventDAO
{
    private static long nextId = 1;
    private static final List<Event> events = Collections.synchronizedList(new ArrayList<Event>());

    public static void resetMemDB()
    {
        synchronized(MemoryEventDAO.events)
        {
            MemoryEventDAO.nextId = 1;
            MemoryEventDAO.events.clear();
        }
    }


    @Override
    public List<Event> getAllEvents()
    {
        final List<Event> events = new ArrayList<>();

        synchronized(MemoryEventDAO.events)
        {
            for(final Event event : MemoryEventDAO.events)
            {
                events.add(event.copy());
            }
        }

        return events;
    }

    @Override
    public Event getEvent(final long eventID)
    {
        synchronized(MemoryEventDAO.events)
        {
            for(final Event event : MemoryEventDAO.events)
            {
                if(eventID == event.getId())
                {
                    return event;
                }
            }
        }

        return null;
    }

    @Override
    public List<Event> getEventsBetweenPartial(final Calendar searchRangeStart, final Calendar searchRangeEnd)
    {
        if(searchRangeStart.compareTo(searchRangeEnd) > 0)
        {
            throw new IllegalArgumentException("Start time cannot come after end time");
        }

        final List<Event> events = new ArrayList<>();

        synchronized(MemoryEventDAO.events)
        {
            for(final Event event : MemoryEventDAO.events)
            {
                final boolean evStartGood = event.getStartDateTime().compareTo(searchRangeEnd) < 0;
                final boolean evEndGood = event.getEndDateTime().compareTo(searchRangeStart) > 0;

                if(evStartGood && evEndGood)
                {
                    events.add(event.copy());
                }
            }
        }

        return events;
    }

    @Override
    public List<Event> getEventsBetweenComplete(final Calendar searchRangeStart, final Calendar searchRangeEnd)
    {
        if(searchRangeStart.compareTo(searchRangeEnd) > 0)
        {
            throw new IllegalArgumentException("Start time cannot come after end time");
        }

        final List<Event> events = new ArrayList<>();

        synchronized(MemoryEventDAO.events)
        {
            for(final Event event : MemoryEventDAO.events)
            {

                final boolean evStartGood = event.getStartDateTime().compareTo(searchRangeStart) >= 0;
                final boolean evEndGood = event.getEndDateTime().compareTo(searchRangeEnd) <= 0;

                if(evStartGood && evEndGood)
                {
                    events.add(event.copy());
                }
            }
        }

        return events;
    }

    @Override
    public List<Event> getEventsByTag(final String... tags)
    {
        throw new NotYetImplementedException("MemoryEventDAO.getEventsByTag not yet implemented");
    }

    @Override
    public Event addEvent(final Event newEvent)
    {
        synchronized(MemoryEventDAO.events)
        {
            newEvent.setId(MemoryEventDAO.nextId);
            ++MemoryEventDAO.nextId;
            MemoryEventDAO.events.add(newEvent);
        }

        return newEvent;
    }

    @Override
    public Event removeEvent(final long eventId)
    {
        synchronized(MemoryEventDAO.events)
        {
            final Iterator<Event> eventIterator = MemoryEventDAO.events.iterator();

            while(eventIterator.hasNext())
            {
                final Event event = eventIterator.next();
                if(eventId == event.getId())
                {
                    eventIterator.remove();
                    return event;
                }
            }
        }

        return null;
    }

    @Override
    public Event removeEvent(final Event event)
    {
        return removeEvent(event.getId());
    }

    @Override
    public Event updateEvent(final Event eventToUpdate)
    {
        final Event removedEvent = this.removeEvent(eventToUpdate);

        // only add the new event if something with the same ID previously existed
        if(null != removedEvent)
        {
            MemoryEventDAO.events.add(eventToUpdate);
        }

        return removedEvent;
    }

    @Override
    public Event forceAddEvent(final Event eventToAdd)
    {
        final long eventId = eventToAdd.getId();

        synchronized(MemoryEventDAO.events)
        {
            final Iterator<Event> eventIterator = MemoryEventDAO.events.iterator();

            while (eventIterator.hasNext())
            {
                final Event event = eventIterator.next();
                if (eventId == event.getId())
                {
                    eventIterator.remove();
                }
            }

            eventToAdd.setId(MemoryEventDAO.nextId);
            ++MemoryEventDAO.nextId;
            MemoryEventDAO.events.add(eventToAdd);
        }

        return eventToAdd;
    }
}