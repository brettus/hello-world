package com.example.util.gson;

import java.lang.reflect.Type;
import java.util.Calendar;

import com.example.util.CalendarAdapter;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Utility class for converting date/time strings to/from Calendar instances for
 * GSON
 */
public final class CalendarGsonAdapter implements JsonSerializer<Calendar>, JsonDeserializer<Calendar> {
	private final CalendarAdapter adapter = new CalendarAdapter();
	
	@Override
	public Calendar deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context)
			throws JsonParseException {
		
		try {
			return adapter.unmarshal(json.getAsString());
		} catch (final Exception e) {
			throw new JsonParseException(e);
		}
	}
	
	@Override
	public JsonElement serialize(final Calendar src, final Type typeOfSrc, final JsonSerializationContext context) {
		try {
			return new JsonPrimitive(adapter.marshal(src));
		} catch (final Exception e) {
			throw new IllegalArgumentException("Given Calendar object was not serializable", e);
		}
	}
}