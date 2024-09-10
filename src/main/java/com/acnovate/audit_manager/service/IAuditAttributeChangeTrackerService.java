package com.acnovate.audit_manager.service;

import java.util.List;

import com.acnovate.audit_manager.common.interfaces.IService;
import com.acnovate.audit_manager.domain.AuditAttributeChangeTracker;
import com.acnovate.audit_manager.domain.AuditObjectChangeTracker;
import com.acnovate.audit_manager.response.dto.AuditAttributeChangeTrackerResponseDto;

public interface IAuditAttributeChangeTrackerService extends IService<AuditAttributeChangeTracker> {
	
	List<AuditAttributeChangeTracker> findByAuditObjectChangeTracker(AuditObjectChangeTracker auditObjectChangeTracker);

	AuditAttributeChangeTrackerResponseDto domainToDto(AuditAttributeChangeTracker auditAttributeChangeTracker);
}