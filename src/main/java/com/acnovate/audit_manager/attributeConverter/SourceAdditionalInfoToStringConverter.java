package com.acnovate.audit_manager.attributeConverter;

import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.acnovate.audit_manager.domain.SourceAdditionalInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;

public class SourceAdditionalInfoToStringConverter implements AttributeConverter<List<SourceAdditionalInfo>, String> {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	private ObjectMapper mapper = new ObjectMapper();

	@Override
	public String convertToDatabaseColumn(List<SourceAdditionalInfo> attribute) {
		try {
			if (attribute != null) {
				return mapper.writeValueAsString(attribute);
			}
		} catch (Exception e) {
			logger.error("Error while convertToDatabaseColumn :: Exception : {}", ExceptionUtils.getStackTrace(e));
		}
		return "{}";
	}

	@Override
	public List<SourceAdditionalInfo> convertToEntityAttribute(String dbData) {
		if (dbData != null && !dbData.equals("{}")) {
			try {
				// Use TypeReference to deserialize a List of SoruceAdditionalInfo
				return mapper.readValue(dbData, new TypeReference<List<SourceAdditionalInfo>>() {
				});
			} catch (Exception e) {
				logger.error("Error while convertToEntityAttribute :: Exception :{}", ExceptionUtils.getStackTrace(e));
			}
		}
		return null; // Return null if dbData is empty or deserialization fails
	}

}
