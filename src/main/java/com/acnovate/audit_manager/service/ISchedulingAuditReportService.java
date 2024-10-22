package com.acnovate.audit_manager.service;

import com.acnovate.audit_manager.common.interfaces.IService;
import com.acnovate.audit_manager.domain.SchedulingAuditReport;
import com.acnovate.audit_manager.dto.request.SchedulingAuditReportRequest;
import com.acnovate.audit_manager.dto.response.SchedulingAuditReportResponse;

public interface ISchedulingAuditReportService extends IService<SchedulingAuditReport> {

	SchedulingAuditReportResponse domainToDto(SchedulingAuditReport schedulingAuditReport);

	SchedulingAuditReportResponse createSchedulingAuditReport(SchedulingAuditReportRequest request);

	void sendScheduledReport(SchedulingAuditReport schedulingAuditReport);
}
