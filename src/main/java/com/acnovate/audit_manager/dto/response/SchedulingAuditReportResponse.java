package com.acnovate.audit_manager.dto.response;

import com.acnovate.audit_manager.domain.SchedulingAuditReport;

import java.util.Arrays;
import java.util.List;

public class SchedulingAuditReportResponse {
  private Long id;
  private Integer reportId;
  private String frequency;
  private Integer schedulingHour;
  private Integer schedulingMinute;
  private String timeMarker;
  private List<String> recipients;

  // No-argument constructor
  public SchedulingAuditReportResponse() {}

  // All-argument constructor
  public SchedulingAuditReportResponse(Long id, Integer reportId, String frequency, Integer schedulingHour, Integer schedulingMinute,
      String timeMarker, List<String> recipients) {
    this.id = id;
    this.reportId = reportId;
    this.frequency = frequency;
    this.schedulingHour = schedulingHour;
    this.schedulingMinute = schedulingMinute;
    this.timeMarker = timeMarker;
    this.recipients = recipients;
  }

  // Constructor that accepts the SchedulingAuditReport entity
  public SchedulingAuditReportResponse(SchedulingAuditReport schedulingAuditReport) {
    this.id = schedulingAuditReport.getId();
    this.reportId = schedulingAuditReport.getReportId();
    this.frequency = schedulingAuditReport.getFrequency();
    this.schedulingHour = schedulingAuditReport.getSchedulingHour();
    this.schedulingMinute = schedulingAuditReport.getSchedulingMinute();
    this.timeMarker = schedulingAuditReport.getTimeMarker();
    // Convert the recipients string back to a list
    this.recipients = Arrays.asList(schedulingAuditReport.getRecipients()
        .split(","));
  }

  // Getters and Setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Integer getReportId() {
    return reportId;
  }

  public void setReportId(Integer reportId) {
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

  // toString method
  @Override
  public String toString() {
    return "SchedulingAuditReportResponse{" + "id=" + id + ", reportId=" + reportId + ", frequency='" + frequency + '\'' + ", schedulingHour="
        + schedulingHour + ", schedulingMinute=" + schedulingMinute + ", timeMarker='" + timeMarker + '\'' + ", recipients=" + recipients + '}';
  }
}
