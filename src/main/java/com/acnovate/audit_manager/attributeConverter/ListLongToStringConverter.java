package com.acnovate.audit_manager.attributeConverter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.AttributeConverter;

public class ListLongToStringConverter implements AttributeConverter<List<Long>, String> {

	private static final String SEPARATOR = ","; // Define a separator for list items

	// Convert List<Long> to String (for storing in DB)
	@Override
	public String convertToDatabaseColumn(List<Long> attribute) {
		if (attribute == null || attribute.isEmpty()) {
			return null;
		}
		return attribute.stream().map(String::valueOf) // Convert Long to String
				.collect(Collectors.joining(SEPARATOR)); // Join with separator
	}

	// Convert String from DB to List<Long>
	@Override
	public List<Long> convertToEntityAttribute(String dbData) {
		if (dbData == null || dbData.isEmpty()) {
			return null;
		}
		return Arrays.stream(dbData.split(SEPARATOR)) // Split the string
				.map(Long::valueOf) // Convert String to Long
				.collect(Collectors.toList());
	}
}
