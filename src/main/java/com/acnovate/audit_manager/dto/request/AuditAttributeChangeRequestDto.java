package com.acnovate.audit_manager.dto.request;

public class AuditAttributeChangeRequestDto {
	private Long id;
	private String attributeName;
	private String oldValue;
	private String newValue;
	private String changedBy;
	private Long auditObjectChangeTrackerId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public String getOldValue() {
		return oldValue;
	}

	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

	public String getChangedBy() {
		return changedBy;
	}

	public void setChangedBy(String changedBy) {
		this.changedBy = changedBy;
	}

	public Long getAuditObjectChangeTrackerId() {
		return auditObjectChangeTrackerId;
	}

	public void setAuditObjectChangeTrackerId(Long auditObjectChangeTrackerId) {
		this.auditObjectChangeTrackerId = auditObjectChangeTrackerId;
	}

	@Override
	public String toString() {
		return "AuditAttributeChangeRequestDto [id=" + id + ", attributeName=" + attributeName + ", oldValue="
				+ oldValue + ", newValue=" + newValue + ", changedBy=" + changedBy + ", auditObjectChangeTrackerId="
				+ auditObjectChangeTrackerId + "]";
	}
}
