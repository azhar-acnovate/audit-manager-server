package com.acnovate.audit_manager.dto.response;

import java.util.Date;

import com.acnovate.audit_manager.constant.MyConstant;
import com.fasterxml.jackson.annotation.JsonFormat;

public class UserResponseDto {
	private Long id;
	private String userName;
	private Boolean active = true;
	private String userRole;
	private String userEmail;
	@JsonFormat(pattern = MyConstant.REQUEST_DATE_FORMAT)
	private Date createdAt;

	@JsonFormat(pattern = MyConstant.REQUEST_DATE_FORMAT)
	private Date updatedAt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

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

	public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	@Override
	public String toString() {
		return "UserResponseDto [id=" + id + ", userName=" + userName + ", active=" + active + ", userRole=" + userRole
				+ ", userEmail=" + userEmail + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
	}
}
