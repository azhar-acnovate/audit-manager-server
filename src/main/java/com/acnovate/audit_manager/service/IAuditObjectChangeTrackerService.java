package com.acnovate.audit_manager.service;

import com.acnovate.audit_manager.common.interfaces.IService;
import com.acnovate.audit_manager.domain.AuditObjectChangeTracker;
import com.acnovate.audit_manager.dto.request.AuditObjectChangeRequestDto;
import com.acnovate.audit_manager.dto.response.AuditLogActivityResponseDto;

public interface IAuditObjectChangeTrackerService extends IService<AuditObjectChangeTracker> {

	AuditLogActivityResponseDto domainToDto(AuditObjectChangeTracker auditObjectChangeTracker);

	AuditLogActivityResponseDto createAuditObjectChangeTracker(AuditObjectChangeRequestDto auditObjectChangeRequestDto);

}
