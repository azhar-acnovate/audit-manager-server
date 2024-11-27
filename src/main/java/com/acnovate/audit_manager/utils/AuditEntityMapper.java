package com.acnovate.audit_manager.utils;

import com.acnovate.audit_manager.domain.AuditEntity;
import com.acnovate.audit_manager.dto.response.AuditEntityDto;

public class AuditEntityMapper {
	public static void mapAuditEntityToDto(AuditEntity auditEntity, AuditEntityDto auditEntityDto) {
		if (auditEntity != null && auditEntityDto != null) {
			auditEntityDto.setCreatedAt(auditEntity.getCreatedAt());
			auditEntityDto.setUpdatedAt(auditEntity.getUpdatedAt());
			auditEntityDto.setCreatedBy(auditEntity.getCreatedBy());
			auditEntityDto.setUpdatedBy(auditEntity.getUpdatedBy());
			auditEntityDto.setActive(auditEntity.getActive());
		}
	}
}
