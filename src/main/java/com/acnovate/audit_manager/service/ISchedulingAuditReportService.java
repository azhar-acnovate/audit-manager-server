package com.acnovate.audit_manager.service;

import com.acnovate.audit_manager.common.dto.LoggedInUserDetails;
import com.acnovate.audit_manager.common.interfaces.IService;
import com.acnovate.audit_manager.domain.SchedulingAuditReport;
import com.acnovate.audit_manager.domain.User;
import com.acnovate.audit_manager.dto.request.SchedulingAuditReportRequest;
import com.acnovate.audit_manager.dto.request.UserRequestDto;
import com.acnovate.audit_manager.dto.response.SchedulingAuditReportResponse;
import com.acnovate.audit_manager.dto.response.UserResponseDto;

public interface ISchedulingAuditReportService extends IService<SchedulingAuditReport> {

	SchedulingAuditReportResponse createSchedulingAuditReport(SchedulingAuditReportRequest request);
}
