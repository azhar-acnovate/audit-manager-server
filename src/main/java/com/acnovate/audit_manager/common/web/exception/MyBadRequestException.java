package com.acnovate.audit_manager.common.web.exception;

public final class MyBadRequestException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MyBadRequestException() {
		super();
	}

	public MyBadRequestException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public MyBadRequestException(final String message) {
		super(message);
	}

	public MyBadRequestException(final Throwable cause) {
		super(cause);
	}

}
