package com.acnovate.audit_manager.domain;

import java.util.Objects;

public class SourceAdditionalInfo {
	private String fieldName;
	private String fieldValue;

	public SourceAdditionalInfo() {

	}

	public SourceAdditionalInfo(String fieldName, String fieldValue) {
		this.fieldName = fieldName;
		this.fieldValue = fieldValue;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldValue() {
		return fieldValue;
	}

	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}

	@Override
	public int hashCode() {
		return Objects.hash(fieldName, fieldValue);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SourceAdditionalInfo other = (SourceAdditionalInfo) obj;
		return Objects.equals(fieldName, other.fieldName) && Objects.equals(fieldValue, other.fieldValue);
	}

}
