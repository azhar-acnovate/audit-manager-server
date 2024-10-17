package com.acnovate.audit_manager.service;

import java.util.LinkedHashMap;
import java.util.List;

import com.acnovate.audit_manager.domain.AuditObjectChangeTracker;

public interface IFileGenerationService {

	byte[] generateAuditReport(LinkedHashMap<String, String> headerInfo,
			List<AuditObjectChangeTracker> auditObjectList);

	byte[] generateFile(String fileType, LinkedHashMap<String, String> headerInfo,
			List<AuditObjectChangeTracker> auditObjectList);
}
