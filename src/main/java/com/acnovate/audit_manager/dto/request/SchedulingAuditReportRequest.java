package com.acnovate.audit_manager.dto.request;

import java.util.List;

public class SchedulingAuditReportRequest {
  private List<Integer> reportId; // List of report IDs
  private String frequency;
  private Integer schedulingHour;
  private Integer schedulingMinute;
  private String timeMarker;
  private List<String> recipients;

  // Getters and Setters

  public List<Integer> getReportId() {
    return reportId;
  }

  public void setReportId(List<Integer> reportId) {
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
}
