package com.acnovate.audit_manager.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acnovate.audit_manager.common.persistence.service.AbstractRawService;
import com.acnovate.audit_manager.domain.AuditAttributeChangeTracker;
import com.acnovate.audit_manager.domain.AuditObjectChangeTracker;
import com.acnovate.audit_manager.dto.request.AuditAttributeChangeRequestDto;
import com.acnovate.audit_manager.dto.response.AuditAttributeChangeTrackerResponseDto;
import com.acnovate.audit_manager.repository.AuditAttributeChangeTrackerRepository;
import com.acnovate.audit_manager.service.IAuditAttributeChangeTrackerService;
import com.acnovate.audit_manager.service.IAuditObjectChangeTrackerService;

@Service
@Transactional
public class AuditAttributeChangeTrackerServiceImpl extends AbstractRawService<AuditAttributeChangeTracker>
		implements IAuditAttributeChangeTrackerService {
	@Autowired
	private AuditAttributeChangeTrackerRepository repo;

	@Autowired
	private IAuditObjectChangeTrackerService auditObjectChangeTrackerService;

	@Override
	protected JpaRepository<AuditAttributeChangeTracker, Long> getDao() {
		return repo;
	}

	@Override
	protected JpaSpecificationExecutor<AuditAttributeChangeTracker> getSpecificationExecutor() {
		return repo;
	}

	@Override
	public AuditAttributeChangeTrackerResponseDto domainToDto(AuditAttributeChangeTracker auditAttributeChangeTracker) {

		AuditAttributeChangeTrackerResponseDto auditAttributeChangeTrackerResponseDto = new AuditAttributeChangeTrackerResponseDto();
		auditAttributeChangeTrackerResponseDto.setAttributeName(auditAttributeChangeTracker.getAttributeName());
		auditAttributeChangeTrackerResponseDto.setChangedBy(auditAttributeChangeTracker.getChangedBy());
		auditAttributeChangeTrackerResponseDto.setId(auditAttributeChangeTracker.getId());
		auditAttributeChangeTrackerResponseDto.setNewValue(auditAttributeChangeTracker.getNewValue());
		auditAttributeChangeTrackerResponseDto.setOldValue(auditAttributeChangeTracker.getOldValue());

		return auditAttributeChangeTrackerResponseDto;
	}

	@Override
	public List<AuditAttributeChangeTracker> findByAuditObjectChangeTracker(
			AuditObjectChangeTracker auditObjectChangeTracker) {

		return repo.findByAuditObjectChangeTracker(auditObjectChangeTracker);
	}

	@Override
	public AuditAttributeChangeTrackerResponseDto createAuditAttributeChangeTracker(
			AuditAttributeChangeRequestDto auditAttributeChangeRequestDto) {
		System.out.println(auditAttributeChangeRequestDto);
		AuditAttributeChangeTracker auditAttributeChangeTracker = new AuditAttributeChangeTracker();
		if(auditAttributeChangeRequestDto.getId()!=null) {
			auditAttributeChangeTracker = findOne(auditAttributeChangeRequestDto.getId());
		}
		auditAttributeChangeTracker.setAttributeName(auditAttributeChangeRequestDto.getAttributeName());
		auditAttributeChangeTracker.setChangedBy(auditAttributeChangeRequestDto.getChangedBy());
		auditAttributeChangeTracker.setOldValue(auditAttributeChangeRequestDto.getOldValue());
		auditAttributeChangeTracker.setNewValue(auditAttributeChangeRequestDto.getNewValue());
		auditAttributeChangeTracker.setAuditObjectChangeTracker(auditObjectChangeTrackerService
				.findOne(auditAttributeChangeRequestDto.getAuditObjectChangeTrackerId()));
		auditAttributeChangeTracker = create(auditAttributeChangeTracker);
		return domainToDto(auditAttributeChangeTracker);
	}

}
