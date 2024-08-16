package com.acnovate.audit_manager.common.web;

import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
//import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.acnovate.audit_manager.common.dto.CommonResponse;
import com.acnovate.audit_manager.common.dto.ValidationErrorDTO;
import com.acnovate.audit_manager.common.persistence.exception.CustomErrorHandleException;
import com.acnovate.audit_manager.common.persistence.exception.MyEntityNotFoundException;
import com.acnovate.audit_manager.common.web.exception.MyConflictException;
import com.acnovate.audit_manager.common.web.exception.MyResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	protected final ResponseEntity<Object> handleHttpMessageNotReadable(final HttpMessageNotReadableException ex,
			final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
		logger.info("Bad Request: " + ex.getMessage());
		logger.debug("Bad Request: ", ex);

		final CommonResponse apiError = message(HttpStatus.BAD_REQUEST, ex);
		return handleExceptionInternal(ex, apiError, headers, HttpStatus.OK, request);
	}

	protected final ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex,
			final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
		logger.info("Bad Request: " + ex.getMessage());
		logger.debug("Bad Request: ", ex);

		final BindingResult result = ex.getBindingResult();
		final List<FieldError> fieldErrors = result.getFieldErrors();
		final ValidationErrorDTO dto = processFieldErrors(fieldErrors);

		return handleExceptionInternal(ex, dto, headers, HttpStatus.OK, request);
	}

	@ExceptionHandler(value = { DataIntegrityViolationException.class })
	protected final ResponseEntity<Object> handleBadRequest(final RuntimeException ex, final WebRequest request) {
		logger.info("Bad Request: " + ex.getLocalizedMessage());
		logger.info("Bad Request: ", ex);

		if (ExceptionUtils.getRootCauseMessage(ex).contains("duplicate")) {
			final CommonResponse apiError = message(HttpStatus.CONFLICT, ex);
			return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.OK, request);
		}

		final CommonResponse apiError = message(HttpStatus.BAD_REQUEST, ex);
		return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.OK, request);
	}

	// -- 403 Forbidden
	@ExceptionHandler({ AccessDeniedException.class })
	protected ResponseEntity<Object> handleEverything(final AccessDeniedException ex, final WebRequest request) {
		logger.info("403 Status Code", ex);

		final CommonResponse apiError = message(HttpStatus.FORBIDDEN, ex);

		return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.OK, request);
	}

	// -- 404
	@ExceptionHandler({ EntityNotFoundException.class, MyEntityNotFoundException.class,
			MyResourceNotFoundException.class })
	protected ResponseEntity<Object> handleNotFound(final RuntimeException ex, final WebRequest request) {
		logger.info("Not Found: " + ex.getMessage());

		final CommonResponse apiError = message(HttpStatus.NOT_FOUND, ex);
		return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.OK, request);
	}

	// -- 200
	@ExceptionHandler({ CustomErrorHandleException.class })
	protected ResponseEntity<Object> customErrorHandleException(final RuntimeException ex, final WebRequest request) {
		logger.info("Not Found: " + ex.getMessage());

		final CommonResponse apiError = message(HttpStatus.INTERNAL_SERVER_ERROR, ex);
		return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.OK, request);
	}

//	// -- 400
//	@ExceptionHandler({ InvalidRequestException.class })
//	protected ResponseEntity<Object> InvalidRequestException(final RuntimeException ex, final WebRequest request) {
//		logger.info("Not Found: " + ex.getMessage());
//
//		final CommonResponse apiError = message(HttpStatus.OK, ex);
//		return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.OK, request);
//	}

	// --409
	@ExceptionHandler({ InvalidDataAccessApiUsageException.class, DataAccessException.class,
			MyConflictException.class })
	protected ResponseEntity<Object> handleConflict(final RuntimeException ex, final WebRequest request) {
		logger.info("Conflict: " + ex.getMessage());
		logger.info("Conflict: " + ExceptionUtils.getStackTrace(ex));

		final CommonResponse apiError = message(HttpStatus.CONFLICT, ex);
		return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.OK, request);
	}

	// -- 500
	@ExceptionHandler({ NullPointerException.class, IllegalArgumentException.class, IllegalStateException.class })
	protected ResponseEntity<Object> handle500s(final RuntimeException ex, final WebRequest request) {
		logger.info("500 Status Code", ex);
		final CommonResponse apiError = message(HttpStatus.INTERNAL_SERVER_ERROR, ex);
		return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.OK, request);
	}

	// -- 500
	@ExceptionHandler(Exception.class)
	protected ResponseEntity<Object> handleException(final RuntimeException ex, final WebRequest request) {
		logger.error("500 Status Code", ex);
		final CommonResponse apiError = message(HttpStatus.INTERNAL_SERVER_ERROR, ex);
		return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.OK, request);
	}

	private ValidationErrorDTO processFieldErrors(final List<FieldError> fieldErrors) {
		final ValidationErrorDTO dto = new ValidationErrorDTO();

		for (final FieldError fieldError : fieldErrors) {
			final String localizedErrorMessage = fieldError.getDefaultMessage();
			dto.addFieldError(fieldError.getField(), localizedErrorMessage);
		}

		return dto;
	}

	private CommonResponse message(final HttpStatus httpStatus, final Exception ex) {
		final String message = ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage();
		// final String devMessage = ex.getClass().getSimpleName();
		final String devMessage = ExceptionUtils.getStackTrace(ex);
		CommonResponse res = new CommonResponse();
		res.setStatus(httpStatus.value());
		res.setDevMessage(devMessage);
		res.setMessage(message);
		res.setError(true);
		return res;
	}
}
