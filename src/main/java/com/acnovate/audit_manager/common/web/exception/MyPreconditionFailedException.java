package com.acnovate.audit_manager.common.web.exception;

public final class MyPreconditionFailedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MyPreconditionFailedException() {
		super();
	}

	public MyPreconditionFailedException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public MyPreconditionFailedException(final String message) {
		super(message);
	}

	public MyPreconditionFailedException(final Throwable cause) {
		super(cause);
	}

}
