package com.acnovate.audit_manager.domain;

import java.util.Date;
import java.util.List;

import com.acnovate.audit_manager.attributeConverter.ListLongToStringConverter;
import com.acnovate.audit_manager.attributeConverter.ListToStringConverter;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "audit_report")
public class AuditReport extends AuditEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Convert(converter = ListLongToStringConverter.class)
	private List<Long> refObjectIds;

	private String reportName;

	private Date startDateRange;

	private Date endDateRange;

	@Convert(converter = ListToStringConverter.class)
	private List<String> changedUserNames;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public Date getStartDateRange() {
		return startDateRange;
	}

	public void setStartDateRange(Date startDateRange) {
		this.startDateRange = startDateRange;
	}

	public Date getEndDateRange() {
		return endDateRange;
	}

	public void setEndDateRange(Date endDateRange) {
		this.endDateRange = endDateRange;
	}

	public List<String> getChangedUserNames() {
		return changedUserNames;
	}

	public void setChangedUserNames(List<String> changedUserNames) {
		this.changedUserNames = changedUserNames;
	}

	public List<Long> getRefObjectIds() {
		return refObjectIds;
	}

	public void setRefObjectIds(List<Long> refObjectIds) {
		this.refObjectIds = refObjectIds;
	}

	@Override
	public String toString() {
		return "AuditReport [id=" + id + ", refObjectIds=" + refObjectIds + ", reportName=" + reportName
				+ ", startDateRange=" + startDateRange + ", endDateRange=" + endDateRange + ", changedUserNames="
				+ changedUserNames + ", getId()=" + getId() + ", getReportName()=" + getReportName()
				+ ", getStartDateRange()=" + getStartDateRange() + ", getEndDateRange()=" + getEndDateRange()
				+ ", getChangedUserNames()=" + getChangedUserNames() + ", getRefObjectIds()=" + getRefObjectIds()
				+ ", getCreatedAt()=" + getCreatedAt() + ", getUpdatedAt()=" + getUpdatedAt() + ", getCreatedBy()="
				+ getCreatedBy() + ", getUpdatedBy()=" + getUpdatedBy() + ", getActive()=" + getActive()
				+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
				+ "]";
	}
}
