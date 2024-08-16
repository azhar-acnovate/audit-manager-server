package com.acnovate.audit_manager.common.web.exception;

public final class MyConflictException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MyConflictException() {
		super();
	}

	public MyConflictException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public MyConflictException(final String message) {
		super(message);
	}

	public MyConflictException(final Throwable cause) {
		super(cause);
	}

}
