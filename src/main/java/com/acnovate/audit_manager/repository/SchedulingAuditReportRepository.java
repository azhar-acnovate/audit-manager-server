package com.acnovate.audit_manager.repository;

import com.acnovate.audit_manager.domain.SchedulingAuditReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SchedulingAuditReportRepository extends JpaRepository<SchedulingAuditReport, Long>, JpaSpecificationExecutor<SchedulingAuditReport> {
}
