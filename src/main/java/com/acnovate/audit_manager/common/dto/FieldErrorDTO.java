package com.acnovate.audit_manager.common.dto;

public class FieldErrorDTO {
	private final String field;

	private final String message;

	public FieldErrorDTO(final String field, final String message) {
		this.field = field;
		this.message = message;
	}

	public String getField() {
		return field;
	}

	public String getMessage() {
		return message;
	}
}
