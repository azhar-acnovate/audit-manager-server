package com.acnovate.audit_manager.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "scheduling_audit_report")
public class SchedulingAuditReport {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "report_id", nullable = false)
  private Integer reportId;

  @Column(name = "frequency", nullable = false)
  private String frequency;

  @Column(name = "scheduling_hour", nullable = false)
  private Integer schedulingHour;

  @Column(name = "scheduling_minute", nullable = false)
  private Integer schedulingMinute;

  @Column(name = "time_marker", nullable = false)
  private String timeMarker;

  @Column(name = "recipients", nullable = false)
  private String recipients; // Comma-separated emails

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

  public String getRecipients() {
    return recipients;
  }

  public void setRecipients(String recipients) {
    this.recipients = recipients;
  }

  // toString method
  @Override
  public String toString() {
    return "SchedulingAuditReport{" + "id=" + id + ", reportId=" + reportId + ", frequency='" + frequency + '\'' + ", schedulingHour="
        + schedulingHour + ", schedulingMinute=" + schedulingMinute + ", timeMarker='" + timeMarker + '\'' + ", recipients='" + recipients + '\''
        + '}';
  }
}
