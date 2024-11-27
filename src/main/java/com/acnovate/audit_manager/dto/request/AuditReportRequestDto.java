package com.acnovate.audit_manager.dto.request;

import java.util.List;

public class AuditReportRequestDto {
	private Long id;

	private List<Long> refObjectIds;

	private String reportName;

	// @JsonFormat(pattern = MyConstant.REQUEST_DATE_FORMAT)
	private String startDateRange;

	// @JsonFormat(pattern = MyConstant.REQUEST_DATE_FORMAT)
	private String endDateRange;

	private List<String> changedUserNames;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public List<String> getChangedUserNames() {
		return changedUserNames;
	}

	public void setChangedUserNames(List<String> changedUserNames) {
		this.changedUserNames = changedUserNames;
	}

	public List<Long> getRefObjectIds() {
		return refObjectIds;
	}

	public void setRefObjectIds(List<Long> refObjectIds) {
		this.refObjectIds = refObjectIds;
	}

	public String getStartDateRange() {
		return startDateRange;
	}

	public void setStartDateRange(String startDateRange) {
		this.startDateRange = startDateRange;
	}

	public String getEndDateRange() {
		return endDateRange;
	}

	public void setEndDateRange(String endDateRange) {
		this.endDateRange = endDateRange;
	}
}