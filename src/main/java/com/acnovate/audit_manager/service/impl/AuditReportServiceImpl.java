package com.acnovate.audit_manager.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acnovate.audit_manager.common.persistence.exception.CustomErrorHandleException;
import com.acnovate.audit_manager.common.persistence.service.AbstractRawService;
import com.acnovate.audit_manager.constant.MyConstant;
import com.acnovate.audit_manager.domain.AuditObjectChangeTracker;
import com.acnovate.audit_manager.domain.AuditReport;
import com.acnovate.audit_manager.domain.SourceReferenceObject;
import com.acnovate.audit_manager.domain.User;
import com.acnovate.audit_manager.dto.request.AuditReportRequestDto;
import com.acnovate.audit_manager.dto.response.AuditReportResponseDto;
import com.acnovate.audit_manager.repository.AuditReportRepository;
import com.acnovate.audit_manager.service.IAuditObjectChangeTrackerService;
import com.acnovate.audit_manager.service.IAuditReportService;
import com.acnovate.audit_manager.service.IFileGenerationService;
import com.acnovate.audit_manager.service.ISourceReferenceObjectService;
import com.acnovate.audit_manager.service.IUserService;
import com.acnovate.audit_manager.utils.AuditEntityMapper;

@Service
@Transactional
public class AuditReportServiceImpl extends AbstractRawService<AuditReport> implements IAuditReportService {
	@Autowired
	private AuditReportRepository repo;

	@Autowired
	private ISourceReferenceObjectService sourceReferenceObjectService;

	@Autowired
	private IAuditObjectChangeTrackerService auditObjectChangeTrackerService;

	@Autowired
	private IFileGenerationService fileGenerationService;

	@Autowired
	private IUserService userService;

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
		if (resource == null) {
			throw new CustomErrorHandleException(MyConstant.EXCEPTION_MESSAGE_RESOURCE_NOT_FOUND);
		}
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

		if (auditReport == null) {
			throw new CustomErrorHandleException("Resource not found");
		}

		auditReport.setChangedUserNames(req.getChangedUserNames());

		auditReport.setRefObjectIds(req.getRefObjectIds());
		auditReport.setReportName(req.getReportName());
		String startDate = req.getStartDateRange();
		if (startDate != null && startDate.contains(" ")) {
			startDate = startDate.split(" ")[0];
			startDate += " 00:00:00";
		}

		String endDate = req.getEndDateRange();
		if (endDate != null && endDate.contains(" ")) {
			endDate = endDate.split(" ")[0];
			endDate += " 23:59:59";
		}

		try {
			SimpleDateFormat utcFormatter = new SimpleDateFormat(MyConstant.REQUEST_DATE_FORMAT);
			utcFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));

			auditReport.setStartDateRange(utcFormatter.parse(startDate));
			auditReport.setEndDateRange(utcFormatter.parse(endDate));
		} catch (ParseException e) {
			throw new CustomErrorHandleException("Format date issue: " + e.getMessage());
		}
		auditReport = create(auditReport);
		return domainToDto(auditReport);
	}

	@Override
	public byte[] genereteReport(Long exportedById, List<Long> reportIds, String fileType) {
		List<AuditReport> reports = findAllById(reportIds);
		List<AuditObjectChangeTracker> list = new ArrayList<>();

		LinkedHashMap<String, String> headerInfo = new LinkedHashMap<>();
		StringBuilder filterList = new StringBuilder();
		for (AuditReport report : reports) {

			list.addAll(auditObjectChangeTrackerService.getFilteredReportData(report.getRefObjectIds(),
					report.getStartDateRange(), report.getEndDateRange()));
			filterList.append(report.getReportName()).append("|");
		}

		// Sorting by eventOccurence in ascending order (old to new)
		Collections.sort(list, Comparator.comparing(AuditObjectChangeTracker::getEventOccurence));
		User user = userService.findOne(exportedById);
		String exportedBy = user.getUserEmail();

		headerInfo.put("Exported By", exportedBy);
		// Remove last "|"
		if (filterList.length() > 0) {
			filterList.deleteCharAt(filterList.length() - 1);
		}
		headerInfo.put("Filter Criteria - List", filterList.toString());
		return fileGenerationService.generateFile(fileType, headerInfo, list);
	}

	@Override
	public List<String> getReportNamesByIds(List<Long> reportIds) {

		return repo.findReportNamesByIds(reportIds);
	}

}
