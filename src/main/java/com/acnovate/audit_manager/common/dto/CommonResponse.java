package com.acnovate.audit_manager.common.dto;

public class CommonResponse {
	private int status;
	private Object data;
	private String message;
	private String devMessage;

	public String getDevMessage() {
		return devMessage;
	}

	public void setDevMessage(String devMessage) {
		this.devMessage = devMessage;
	}

	private boolean error = false;

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "CommonResponse [status=" + status + ", data=" + data + ", message=" + message + ", devMessage="
				+ devMessage + ", error=" + error + "]";
	}

}
