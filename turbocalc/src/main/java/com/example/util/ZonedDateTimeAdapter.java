package com.example.util;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

public class ZonedDateTimeAdapter extends XmlAdapter<String, ZonedDateTime> {

    private final DateTimeFormatter formatter = 
		DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneId.of("UTC"));

    @Override
    public String marshal(final ZonedDateTime d) throws Exception {
    	final String formatted = d.format(formatter);
    	return formatted;
    }

    @Override
    public ZonedDateTime unmarshal(final String d) throws Exception {
    	try {
    		final ZonedDateTime parsed = ZonedDateTime.parse(d, formatter);
            return parsed;
    	}
    	catch(final Exception e) {
    		e.printStackTrace();
    		throw new RuntimeException(e);
    	}
    }

}