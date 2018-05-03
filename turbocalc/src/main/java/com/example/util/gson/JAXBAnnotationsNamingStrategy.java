package com.example.util.gson;

import java.lang.reflect.Field;

import javax.xml.bind.annotation.XmlElement;

import com.google.gson.FieldNamingStrategy;

/**
 * Allows GSON to parse JAXB annotations and extract serialized field names
 */
public final class JAXBAnnotationsNamingStrategy implements FieldNamingStrategy {
	private static final String JAXB_DEFAULT_FIELD_NAME = "##default";

	@Override
	public String translateName(final Field fieldToTranslate) {
		final XmlElement elAnnotation = fieldToTranslate.getAnnotation(XmlElement.class);
		
		if(null == elAnnotation || JAXB_DEFAULT_FIELD_NAME.equals(elAnnotation.name())) {
			return fieldToTranslate.getName();
		}
		else {
			return elAnnotation.name();
		}
	}
}