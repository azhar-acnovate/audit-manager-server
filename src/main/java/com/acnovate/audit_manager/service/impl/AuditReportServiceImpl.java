package com.acnovate.audit_manager.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acnovate.audit_manager.common.persistence.service.AbstractRawService;
import com.acnovate.audit_manager.domain.AuditReport;
import com.acnovate.audit_manager.domain.SourceReferenceObject;
import com.acnovate.audit_manager.dto.request.AuditReportRequestDto;
import com.acnovate.audit_manager.dto.response.AuditReportResponseDto;
import com.acnovate.audit_manager.repository.AuditReportRepository;
import com.acnovate.audit_manager.service.IAuditReportService;
import com.acnovate.audit_manager.service.ISourceReferenceObjectService;
import com.acnovate.audit_manager.utils.AuditEntityMapper;

@Service
@Transactional
public class AuditReportServiceImpl extends AbstractRawService<AuditReport> implements IAuditReportService {
	@Autowired
	private AuditReportRepository repo;

	@Autowired
	private ISourceReferenceObjectService sourceReferenceObjectService;

	@Override
	protected JpaRepository<AuditReport, Long> getDao() {
		return repo;
	}

	@Override
	protected JpaSpecificationExecutor<AuditReport> getSpecificationExecutor() {
		return repo;
	}

	@Override
	public AuditReportResponseDto domainToDto(AuditReport resource) {
		AuditReportResponseDto auditReportResponseDto = new AuditReportResponseDto();

		auditReportResponseDto.setChangedUserNames(resource.getChangedUserNames());
		auditReportResponseDto.setEndDateRange(resource.getEndDateRange());
		auditReportResponseDto.setId(resource.getId());
		auditReportResponseDto.setRefObjectIds(resource.getRefObjectIds());
		auditReportResponseDto.setReportName(resource.getReportName());
		auditReportResponseDto.setStartDateRange(resource.getStartDateRange());
		List<SourceReferenceObject> sourceReferences = sourceReferenceObjectService
				.findAllById(resource.getRefObjectIds());
		auditReportResponseDto
				.setSourceReferences(sourceReferences.stream().map(sourceReferenceObjectService::domainToDto).toList());
		AuditEntityMapper.mapAuditEntityToDto(resource, auditReportResponseDto);

		return auditReportResponseDto;
	}

	@Override
	public AuditReportResponseDto createAuditReport(AuditReportRequestDto req) {
		AuditReport auditReport = new AuditReport();
		if (req.getId() != null) {
			auditReport = findOne(req.getId());
		}
		auditReport.setChangedUserNames(req.getChangedUserNames());
		auditReport.setEndDateRange(req.getEndDateRange());
		auditReport.setRefObjectIds(req.getRefObjectIds());
		auditReport.setReportName(req.getReportName());
		auditReport.setStartDateRange(req.getStartDateRange());
		auditReport = create(auditReport);
		return domainToDto(auditReport);
	}

}
