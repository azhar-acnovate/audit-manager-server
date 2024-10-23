package com.acnovate.audit_manager.service;

import java.util.Date;
import java.util.List;

import com.acnovate.audit_manager.common.interfaces.IService;
import com.acnovate.audit_manager.domain.AuditObjectChangeTracker;
import com.acnovate.audit_manager.dto.request.AuditObjectChangeRequestDto;
import com.acnovate.audit_manager.dto.response.AuditLogActivityResponseDto;
import com.acnovate.audit_manager.dto.response.DashboardResponeDto;

public interface IAuditObjectChangeTrackerService extends IService<AuditObjectChangeTracker> {

	AuditLogActivityResponseDto domainToDto(AuditObjectChangeTracker auditObjectChangeTracker);

	AuditLogActivityResponseDto createAuditObjectChangeTracker(AuditObjectChangeRequestDto auditObjectChangeRequestDto);

	List<AuditObjectChangeTracker> getFilteredReportData(List<Long> refObjectIds, Date startDate, Date endDate);

	DashboardResponeDto getDashboardData();

}
