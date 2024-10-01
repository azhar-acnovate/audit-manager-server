package com.acnovate.audit_manager.dto.response;

import java.util.Date;
import java.util.List;

import com.acnovate.audit_manager.constant.MyConstant;
import com.fasterxml.jackson.annotation.JsonFormat;

public class AuditReportResponseDto extends AuditEntityDto {
	private Long id;

	private Long refObjectId;

	private String reportName;

	@JsonFormat(pattern = MyConstant.REQUEST_DATE_FORMAT)
	private Date startDateRange;

	@JsonFormat(pattern = MyConstant.REQUEST_DATE_FORMAT)
	private Date endDateRange;

	private List<String> changedUserNames;

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

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public Date getStartDateRange() {
		return startDateRange;
	}

	public void setStartDateRange(Date startDateRange) {
		this.startDateRange = startDateRange;
	}

	public Date getEndDateRange() {
		return endDateRange;
	}

	public void setEndDateRange(Date endDateRange) {
		this.endDateRange = endDateRange;
	}

	public List<String> getChangedUserNames() {
		return changedUserNames;
	}

	public void setChangedUserNames(List<String> changedUserNames) {
		this.changedUserNames = changedUserNames;
	}
}