package com.acnovate.audit_manager.common.persistence.exception;

public final class MyEntityNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5787731864050360964L;

	public MyEntityNotFoundException() {
		super();
	}

	public MyEntityNotFoundException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public MyEntityNotFoundException(final String message) {
		super(message);
	}

	public MyEntityNotFoundException(final Throwable cause) {
		super(cause);
	}
}
