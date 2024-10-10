package com.acnovate.audit_manager.dto.response;

import java.util.Date;

import com.acnovate.audit_manager.constant.MyConstant;
import com.fasterxml.jackson.annotation.JsonFormat;

public class AuditEntityDto {

	@JsonFormat(pattern = MyConstant.REQUEST_DATE_FORMAT)
	private Date createdAt;

	@JsonFormat(pattern = MyConstant.REQUEST_DATE_FORMAT)
	private Date updatedAt;

	private Long createdBy;

	private Long updatedBy;

	private Boolean active = true;

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	public Long getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(Long updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}
}
