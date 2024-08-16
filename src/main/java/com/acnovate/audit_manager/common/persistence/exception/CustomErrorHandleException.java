package com.acnovate.audit_manager.common.persistence.exception;

public class CustomErrorHandleException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5787731864050360964L;

	public CustomErrorHandleException() {
		super();
	}

	public CustomErrorHandleException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public CustomErrorHandleException(final String message) {
		super(message);
	}

	public CustomErrorHandleException(final Throwable cause) {
		super(cause);
	}

}
