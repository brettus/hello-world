package com.example.util;

import static org.junit.Assert.*;

import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TestCalendarAdapter {
	
	private CalendarAdapter adapter = null;
	
	@Before
	public void setup() {
		this.adapter = new CalendarAdapter();
	}
	
	
	@Test
	public void test1() throws Exception {
		final Calendar t = adapter.unmarshal("2014-01-01T11:00:00Z");
		
		assertEquals("2014-01-01T11:00:00Z", adapter.marshal(t));
	}
}
