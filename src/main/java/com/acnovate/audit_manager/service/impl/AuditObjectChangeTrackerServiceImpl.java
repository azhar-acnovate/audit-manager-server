package com.acnovate.audit_manager.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acnovate.audit_manager.common.dto.FilterDto;
import com.acnovate.audit_manager.common.persistence.service.AbstractRawService;
import com.acnovate.audit_manager.domain.AuditAttributeChangeTracker;
import com.acnovate.audit_manager.domain.AuditObjectChangeTracker;
import com.acnovate.audit_manager.repository.AuditObjectChangeTrackerRepository;
import com.acnovate.audit_manager.response.dto.AuditLogActivityResponseDto;
import com.acnovate.audit_manager.service.IAuditAttributeChangeTrackerService;
import com.acnovate.audit_manager.service.IAuditObjectChangeTrackerService;

@Service
@Transactional
public class AuditObjectChangeTrackerServiceImpl extends AbstractRawService<AuditObjectChangeTracker>
		implements IAuditObjectChangeTrackerService {
	@Autowired
	private AuditObjectChangeTrackerRepository repo;

	@Autowired
	private IAuditAttributeChangeTrackerService auditAttributeChangeTrackerService;

	@Override
	protected JpaRepository<AuditObjectChangeTracker, Long> getDao() {
		return repo;
	}

	@Override
	protected JpaSpecificationExecutor<AuditObjectChangeTracker> getSpecificationExecutor() {
		return repo;
	}

	public AuditLogActivityResponseDto domainToDto(AuditObjectChangeTracker auditObjectChangeTracker) {
		AuditLogActivityResponseDto auditLogActivityResponseDto = new AuditLogActivityResponseDto();
		auditLogActivityResponseDto.setId(auditObjectChangeTracker.getId());
		auditLogActivityResponseDto.setEventOccurence(auditObjectChangeTracker.getEventOccurence());
		auditLogActivityResponseDto.setEventType(auditObjectChangeTracker.getEventType());
		auditLogActivityResponseDto.setRefObjectId(auditObjectChangeTracker.getObjectId());
		List<AuditAttributeChangeTracker> auditAttributeChangeTrackers = auditAttributeChangeTrackerService
				.findByAuditObjectChangeTracker(auditObjectChangeTracker);
		auditLogActivityResponseDto.setAttributeChanges(
				auditAttributeChangeTrackers.stream().map(auditAttributeChangeTrackerService::domainToDto).toList());

		return auditLogActivityResponseDto;
	}

}