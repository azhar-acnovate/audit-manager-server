package com.acnovate.audit_manager.domain;

import java.util.List;

import com.acnovate.audit_manager.attributeConverter.SourceAdditionalInfoToStringConverter;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "source_reference_object")
public class SourceReferenceObject extends AuditEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String sourceReferenceName;

	private String sourceReferenceKey;

	@Convert(converter = SourceAdditionalInfoToStringConverter.class)
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
