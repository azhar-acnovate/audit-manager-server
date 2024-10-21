package com.acnovate.audit_manager.service;

import java.util.List;

import com.acnovate.audit_manager.common.interfaces.IService;
import com.acnovate.audit_manager.domain.AuditReport;
import com.acnovate.audit_manager.dto.request.AuditReportRequestDto;
import com.acnovate.audit_manager.dto.response.AuditReportResponseDto;

public interface IAuditReportService extends IService<AuditReport> {
	AuditReportResponseDto domainToDto(AuditReport resource);

	AuditReportResponseDto createAuditReport(AuditReportRequestDto req);

	byte[] genereteReport(Long exportedBy, List<Long> reportIds, String fileType);

	List<String> getReportNamesByIds(List<Long> reportIds);
}
