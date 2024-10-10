package com.acnovate.audit_manager.attributeConverter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.AttributeConverter;

public class ListToStringConverter implements AttributeConverter<List<String>, String> {

	private static final String SEPARATOR = ","; // Define a separator for list items

	// Convert List<String> to String (for storing in DB)
	@Override
	public String convertToDatabaseColumn(List<String> attribute) {
		if (attribute == null || attribute.isEmpty()) {
			return null;
		}
		return attribute.stream().collect(Collectors.joining(SEPARATOR));
	}

	// Convert String from DB to List<String>
	@Override
	public List<String> convertToEntityAttribute(String dbData) {
		if (dbData == null || dbData.isEmpty()) {
			return null;
		}
		return Arrays.asList(dbData.split(SEPARATOR));
	}
}
