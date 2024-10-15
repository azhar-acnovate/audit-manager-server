package com.acnovate.audit_manager.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
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
    private String recipients;  // Comma-separated emails

}
