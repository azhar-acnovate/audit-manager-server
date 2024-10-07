package com.acnovate.audit_manager.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acnovate.audit_manager.common.persistence.service.AbstractRawService;
import com.acnovate.audit_manager.domain.SourceReferenceObject;
import com.acnovate.audit_manager.dto.request.SourceReferenceObjectRequestDto;
import com.acnovate.audit_manager.dto.response.SourceReferenceObjectResponseDto;
import com.acnovate.audit_manager.repository.SourceReferenceObjectRepository;
import com.acnovate.audit_manager.service.ISourceReferenceObjectService;
import com.acnovate.audit_manager.utils.AuditEntityMapper;

@Service
@Transactional
public class SourceReferenceObjectServiceImpl extends AbstractRawService<SourceReferenceObject>
		implements ISourceReferenceObjectService {
	@Autowired
	private SourceReferenceObjectRepository repo;

	@Override
	protected JpaRepository<SourceReferenceObject, Long> getDao() {
		return repo;
	}

	@Override
	protected JpaSpecificationExecutor<SourceReferenceObject> getSpecificationExecutor() {
		return repo;
	}

	@Override
	public SourceReferenceObjectResponseDto domainToDto(SourceReferenceObject resource) {
		SourceReferenceObjectResponseDto sourceReferenceObjectResponseDto = new SourceReferenceObjectResponseDto();
		sourceReferenceObjectResponseDto.setId(resource.getId());
		sourceReferenceObjectResponseDto.setSourceReferenceKey(resource.getSourceReferenceKey());
		sourceReferenceObjectResponseDto.setSourceReferenceName(resource.getSourceReferenceName());
		sourceReferenceObjectResponseDto.setAdditionalInfo(resource.getAdditionalInfo());
		AuditEntityMapper.mapAuditEntityToDto(resource, sourceReferenceObjectResponseDto);
		return sourceReferenceObjectResponseDto;
	}

	@Override
	public SourceReferenceObjectResponseDto createSourceReferenceObject(SourceReferenceObjectRequestDto req) {
		SourceReferenceObject sourceReferenceObject = new SourceReferenceObject();
		if (req.getId() != null) {
			sourceReferenceObject = findOne(req.getId());
		}

		sourceReferenceObject.setSourceReferenceKey(req.getSourceReferenceKey());
		sourceReferenceObject.setSourceReferenceName(req.getSourceReferenceName());
		sourceReferenceObject.setAdditionalInfo(req.getAdditionalInfo());
		sourceReferenceObject = create(sourceReferenceObject);
		return domainToDto(sourceReferenceObject);
	}

}
