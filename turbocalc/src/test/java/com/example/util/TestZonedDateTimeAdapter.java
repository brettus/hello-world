package com.example.util;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.threeten.bp.ZonedDateTime;

@RunWith(JUnit4.class)
public class TestZonedDateTimeAdapter {
	
	private ZonedDateTimeAdapter adapter = null;
	
	@Before
	public void setup() {
		this.adapter = new ZonedDateTimeAdapter();
	}
	
	
	@Test
	public void test1() throws Exception {
		final ZonedDateTime t = adapter.unmarshal("2014-01-01T11:00:00Z");
		
		assertEquals("2014-01-01T11:00:00Z", adapter.marshal(t));
	}
}
