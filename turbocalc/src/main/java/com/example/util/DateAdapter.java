package com.example.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class DateAdapter extends XmlAdapter<String, Date> {

    @Override
    public String marshal(final Date d) throws Exception {
    	final DateFormat fmt = new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm:ssX");
    	fmt.setTimeZone(TimeZone.getTimeZone("UTC"));
    	
    	return fmt.format(d);
    }

    @Override
    public Date unmarshal(final String d) throws Exception {
    	final DateFormat fmt = new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm:ssX");
    	
    	return fmt.parse(d);
    }

}