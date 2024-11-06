package com.acnovate.audit_manager.common.dto;

import java.util.Date;

import com.acnovate.audit_manager.constant.MyConstant;
import com.fasterxml.jackson.annotation.JsonFormat;

public class LoggedInUserDetails {
	private Long id;
	private String userName;
	private Boolean status = true;
	private String fullName;
	private String profileImageName;

	@JsonFormat(pattern = MyConstant.RESPONSE_DATE_FORMAT)
	private Date lastUpdatedDate;

	private String accessToken;

	private String refreshToken;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX", timezone = "UTC")
	private Date issuedAt;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX", timezone = "UTC")
	private Date expireAt;

	private String userRole;

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

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public String getProfileImageName() {
		return profileImageName;
	}

	public void setProfileImageName(String profileImageName) {
		this.profileImageName = profileImageName;
	}

	public Date getLastUpdatedDate() {
		return lastUpdatedDate;
	}

	public void setLastUpdatedDate(Date lastUpdatedDate) {
		this.lastUpdatedDate = lastUpdatedDate;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public Date getIssuedAt() {
		return issuedAt;
	}

	public void setIssuedAt(Date issuedAt) {
		this.issuedAt = issuedAt;
	}

	public Date getExpireAt() {
		return expireAt;
	}

	public void setExpireAt(Date expireAt) {
		this.expireAt = expireAt;
	}

	@Override
	public String toString() {
		return "LoggedInUserDetails [id=" + id + ", userName=" + userName + ", fullName=" + fullName + ",status=" + status + ", profileImageName="
				+ profileImageName + ", lastUpdatedDate=" + lastUpdatedDate + ", accessToken=" + accessToken
				+ ", issuedAt=" + issuedAt + ", expireAt=" + expireAt + "]";
	}

	public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
}
