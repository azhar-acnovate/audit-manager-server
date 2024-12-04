package com.acnovate.audit_manager.dto.response;

import java.util.List;

public class SchedulingAuditReportResponse extends AuditEntityDto {
	private Long id;
	private List<Long> reportId; // Change to List<Long>
	private String frequency;
	private String frequencyType;
	private Integer schedulingHour;
	private Integer schedulingMinute;
	private String timeMarker;
	private List<String> recipients;
	private List<String> reportsName;

	private String readableCron;

	public List<String> getReportsName() {
		return reportsName;
	}

	public void setReportsName(List<String> reportsName) {
		this.reportsName = reportsName;
	}

	// Getters and Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<Long> getReportId() {
		return reportId;
	}

	public void setReportId(List<Long> reportId) {
		this.reportId = reportId;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public Integer getSchedulingHour() {
		return schedulingHour;
	}

	public void setSchedulingHour(Integer schedulingHour) {
		this.schedulingHour = schedulingHour;
	}

	public Integer getSchedulingMinute() {
		return schedulingMinute;
	}

	public void setSchedulingMinute(Integer schedulingMinute) {
		this.schedulingMinute = schedulingMinute;
	}

	public String getTimeMarker() {
		return timeMarker;
	}

	public void setTimeMarker(String timeMarker) {
		this.timeMarker = timeMarker;
	}

	public List<String> getRecipients() {
		return recipients;
	}

	public void setRecipients(List<String> recipients) {
		this.recipients = recipients;
	}

	@Override
	public String toString() {
		return "SchedulingAuditReportResponse{" + "id=" + id + ", reportId=" + reportId + ", frequency='" + frequency
				+ '\'' + ", schedulingHour=" + schedulingHour + ", schedulingMinute=" + schedulingMinute
				+ ", timeMarker='" + timeMarker + '\'' + ", recipients=" + recipients + '}';
	}

	public String getFrequencyType() {
		return frequencyType;
	}

	public void setFrequencyType(String frequencyType) {
		this.frequencyType = frequencyType;
	}

	public String getReadableCron() {
		return readableCron;
	}

	public void setReadableCron(String readableCron) {
		this.readableCron = readableCron;
	}
}
