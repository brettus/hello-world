package com.example.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class CalendarAdapter extends XmlAdapter<String, Calendar> {

	@Override
    public String marshal(final Calendar c) throws Exception {
    	final DateFormat fmt = new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm:ssX");
    	fmt.setCalendar(c);
    	fmt.setTimeZone(TimeZone.getTimeZone("UTC"));
    	
    	return fmt.format(c.getTime());
    }

    @Override
    public Calendar unmarshal(final String d) throws Exception {
    	final Calendar cal = new GregorianCalendar();
    	final DateFormat fmt = new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm:ssX");
    	
    	cal.setTime(fmt.parse(d));
    	cal.setTimeZone(TimeZone.getTimeZone("UTC"));
    	
    	return cal;
    }

}