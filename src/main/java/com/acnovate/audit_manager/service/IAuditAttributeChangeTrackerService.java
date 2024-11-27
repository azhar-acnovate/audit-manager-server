package com.acnovate.audit_manager.service;

import java.util.LinkedHashMap;
import java.util.List;

import com.acnovate.audit_manager.common.interfaces.IService;
import com.acnovate.audit_manager.domain.AuditAttributeChangeTracker;
import com.acnovate.audit_manager.domain.AuditObjectChangeTracker;
import com.acnovate.audit_manager.dto.request.AuditAttributeChangeRequestDto;
import com.acnovate.audit_manager.dto.response.AuditAttributeChangeTrackerResponseDto;

public interface IAuditAttributeChangeTrackerService extends IService<AuditAttributeChangeTracker> {

	List<AuditAttributeChangeTracker> findByAuditObjectChangeTracker(AuditObjectChangeTracker auditObjectChangeTracker);

	AuditAttributeChangeTrackerResponseDto domainToDto(AuditAttributeChangeTracker auditAttributeChangeTracker);

	AuditAttributeChangeTrackerResponseDto createAuditAttributeChangeTracker(
			AuditAttributeChangeRequestDto auditAttributeChangeRequestDto);

	Long countAttributeChangesYesterday();

	Long countAttributeChangesDayBeforeYesterday();

	Long countUserChangesLastQuarter();

	Long countUserChangesPreviousQuarter();

	LinkedHashMap<String, Long> top5UserModifyingDataFrequently();

	LinkedHashMap<String, Long> top5ChangedAttributes();
}
