package com.acnovate.audit_manager.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public class SchedulingAuditReportRequest {

  @NotNull(message = "Report IDs cannot be null")
  private List<String> reportIds;

  @NotNull(message = "Frequency cannot be null")
  private String frequency;

  @NotNull(message = "Scheduling hour cannot be null")
  private Integer schedulingHour;

  @NotNull(message = "Scheduling minute cannot be null")
  private Integer schedulingMinute;

  @NotNull(message = "Time marker cannot be null")
  private String timeMarker;

  @NotNull(message = "Recipients cannot be null")
  private List<String> recipients;


  public List<String> getReportIds() {
    return reportIds;
  }

  public void setReportId(List<String> reportIds) {
    this.reportIds = reportIds;
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
    return "SchedulingAuditReportRequest{" + "reportIds=" + reportIds + ", frequency='" + frequency + '\'' + ", schedulingHour=" + schedulingHour
        + ", schedulingMinute=" + schedulingMinute + ", timeMarker='" + timeMarker + '\'' + ", recipients=" + recipients + '}';
  }
}
