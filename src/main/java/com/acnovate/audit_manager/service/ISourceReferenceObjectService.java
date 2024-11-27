package com.acnovate.audit_manager.service;

import com.acnovate.audit_manager.common.interfaces.IService;
import com.acnovate.audit_manager.domain.SourceReferenceObject;
import com.acnovate.audit_manager.dto.request.SourceReferenceObjectRequestDto;
import com.acnovate.audit_manager.dto.response.SourceReferenceObjectResponseDto;

public interface ISourceReferenceObjectService extends IService<SourceReferenceObject> {
	SourceReferenceObjectResponseDto domainToDto(SourceReferenceObject resource);

	SourceReferenceObjectResponseDto createSourceReferenceObject(SourceReferenceObjectRequestDto req);
}
