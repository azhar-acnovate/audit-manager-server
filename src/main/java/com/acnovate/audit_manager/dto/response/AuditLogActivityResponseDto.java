package com.acnovate.audit_manager.dto.response;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.acnovate.audit_manager.constant.MyConstant;
import com.fasterxml.jackson.annotation.JsonFormat;

public class AuditLogActivityResponseDto {

	private Long id;
	private Long refObjectId;
	private String eventType;
	@JsonFormat(pattern = MyConstant.REQUEST_DATE_FORMAT)
	private Date eventOccurence;

	private List<AuditAttributeChangeTrackerResponseDto> attributeChanges = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getRefObjectId() {
		return refObjectId;
	}

	public void setRefObjectId(Long refObjectId) {
		this.refObjectId = refObjectId;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public Date getEventOccurence() {
		return eventOccurence;
	}

	public void setEventOccurence(Date eventOccurence) {
		this.eventOccurence = eventOccurence;
	}

	public List<AuditAttributeChangeTrackerResponseDto> getAttributeChanges() {
		return attributeChanges;
	}

	public void setAttributeChanges(List<AuditAttributeChangeTrackerResponseDto> attributeChanges) {
		this.attributeChanges = attributeChanges;
	}

}
