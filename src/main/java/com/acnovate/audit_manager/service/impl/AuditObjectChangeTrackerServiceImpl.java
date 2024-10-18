package com.acnovate.audit_manager.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acnovate.audit_manager.common.persistence.service.AbstractRawService;
import com.acnovate.audit_manager.domain.AuditAttributeChangeTracker;
import com.acnovate.audit_manager.domain.AuditObjectChangeTracker;
import com.acnovate.audit_manager.domain.SourceReferenceObject;
import com.acnovate.audit_manager.dto.request.AuditObjectChangeRequestDto;
import com.acnovate.audit_manager.dto.response.AuditLogActivityResponseDto;
import com.acnovate.audit_manager.repository.AuditObjectChangeTrackerRepository;
import com.acnovate.audit_manager.service.IAuditAttributeChangeTrackerService;
import com.acnovate.audit_manager.service.IAuditObjectChangeTrackerService;
import com.acnovate.audit_manager.service.ISourceReferenceObjectService;

@Service
@Transactional
public class AuditObjectChangeTrackerServiceImpl extends AbstractRawService<AuditObjectChangeTracker>
		implements IAuditObjectChangeTrackerService {
	@Autowired
	private AuditObjectChangeTrackerRepository repo;

	@Autowired
	private ISourceReferenceObjectService sourceReferenceObjectService;

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
		auditLogActivityResponseDto.setRefObjectId(auditObjectChangeTracker.getRefObjectId());
		List<AuditAttributeChangeTracker> auditAttributeChangeTrackers = auditAttributeChangeTrackerService
				.findByAuditObjectChangeTracker(auditObjectChangeTracker);
		auditLogActivityResponseDto.setAttributeChanges(
				auditAttributeChangeTrackers.stream().map(auditAttributeChangeTrackerService::domainToDto).toList());
		SourceReferenceObject sourceReference = sourceReferenceObjectService
				.findOne(auditObjectChangeTracker.getRefObjectId());
		auditLogActivityResponseDto.setSourceReference(sourceReferenceObjectService.domainToDto(sourceReference));
		return auditLogActivityResponseDto;
	}

	@Override
	public AuditLogActivityResponseDto createAuditObjectChangeTracker(
			AuditObjectChangeRequestDto auditObjectChangeRequestDto) {
		AuditObjectChangeTracker auditObjectChangeTracker = new AuditObjectChangeTracker();
		if (auditObjectChangeRequestDto.getId() != null) {
			auditObjectChangeTracker = findOne(auditObjectChangeRequestDto.getId());
		}
		auditObjectChangeTracker.setRefObjectId(auditObjectChangeRequestDto.getRefObjectId());
		auditObjectChangeTracker.setEventType(auditObjectChangeRequestDto.getEventType());
		auditObjectChangeTracker.setEventOccurence(auditObjectChangeRequestDto.getEventOccurence());
		auditObjectChangeTracker = create(auditObjectChangeTracker);
		return domainToDto(auditObjectChangeTracker);
	}

	@Override
	public List<AuditObjectChangeTracker> getFilteredReportData(List<Long> refObjectIds, Date startDate, Date endDate) {
		// TODO Auto-generated method stub
		return repo.findByRefObjectIdInAndEventOccurenceBetween(refObjectIds, startDate, endDate);
	}

}
