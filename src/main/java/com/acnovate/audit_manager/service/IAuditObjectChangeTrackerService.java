package com.acnovate.audit_manager.service;

import com.acnovate.audit_manager.common.interfaces.IService;
import com.acnovate.audit_manager.domain.AuditObjectChangeTracker;
import com.acnovate.audit_manager.response.dto.AuditLogActivityResponseDto;

public interface IAuditObjectChangeTrackerService extends IService<AuditObjectChangeTracker> {

	AuditLogActivityResponseDto domainToDto(AuditObjectChangeTracker auditObjectChangeTracker);

}
