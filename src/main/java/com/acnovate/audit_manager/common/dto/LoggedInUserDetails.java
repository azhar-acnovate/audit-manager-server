package com.acnovate.audit_manager.common.dto;

import java.time.Instant;
import java.util.Date;

import com.acnovate.audit_manager.constant.Constant;
import com.fasterxml.jackson.annotation.JsonFormat;

public class LoggedInUserDetails {
	private Long id;
	private String userName;
	private Boolean status = true;

	private String profileImageName;

	@JsonFormat(pattern = Constant.RESPONSE_DATE_FORMAT)
	private Date lastUpdatedDate;

	private String accessToken;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX", timezone = "UTC")
	private Instant issuedAt;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX", timezone = "UTC")
	private Instant expireAt;

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

	public Instant getIssuedAt() {
		return issuedAt;
	}

	public void setIssuedAt(Instant issuedAt) {
		this.issuedAt = issuedAt;
	}

	public Instant getExpireAt() {
		return expireAt;
	}

	public void setExpireAt(Instant expireAt) {
		this.expireAt = expireAt;
	}
}
