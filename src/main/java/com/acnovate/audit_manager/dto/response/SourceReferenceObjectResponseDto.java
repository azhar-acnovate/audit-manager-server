package com.acnovate.audit_manager.dto.response;

import java.util.List;

import com.acnovate.audit_manager.domain.SourceAdditionalInfo;

public class SourceReferenceObjectResponseDto extends AuditEntityDto {

	private Long id;

	private String sourceReferenceName;

	private String sourceReferenceKey;

	private List<SourceAdditionalInfo> additionalInfo;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSourceReferenceName() {
		return sourceReferenceName;
	}

	public void setSourceReferenceName(String sourceReferenceName) {
		this.sourceReferenceName = sourceReferenceName;
	}

	public List<SourceAdditionalInfo> getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(List<SourceAdditionalInfo> additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public String getSourceReferenceKey() {
		return sourceReferenceKey;
	}

	public void setSourceReferenceKey(String sourceReferenceKey) {
		this.sourceReferenceKey = sourceReferenceKey;
	}
}