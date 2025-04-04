package com.dimka228.messenger.controllers.error;

import com.dimka228.messenger.exceptions.AppException;
import com.dimka228.messenger.exceptions.DBException;
import com.dimka228.messenger.exceptions.WrongTokenException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;

import jakarta.persistence.EntityNotFoundException;

import lombok.Data;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;

@Data
@RestControllerAdvice
public class GlobalErrorHandler {

	private String message;

	@ExceptionHandler(NumberFormatException.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	public Map<String, Object> handleNumberFormatException(NumberFormatException e, WebRequest webRequest) {
		return createExceptionMessage(e, HttpStatus.BAD_REQUEST, webRequest);
	}

	@ExceptionHandler(EntityNotFoundException.class)
	@ResponseStatus(code = HttpStatus.NOT_FOUND)
	public Map<String, Object> handleEntityNotFoundException(EntityNotFoundException e, WebRequest webRequest) {
		return createExceptionMessage(e, HttpStatus.NOT_FOUND, webRequest);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	public Map<String, Object> handleIllegalArgumentException(IllegalArgumentException e, WebRequest webRequest) {
		return createExceptionMessage(e, HttpStatus.BAD_REQUEST, webRequest);
	}

	@ExceptionHandler(UsernameNotFoundException.class)
	@ResponseStatus(code = HttpStatus.FORBIDDEN)
	public Map<String, Object> handleUsernameNotFoundException(UsernameNotFoundException e, WebRequest webRequest) {
		return createExceptionMessage(e, HttpStatus.FORBIDDEN, webRequest);
	}

	@ExceptionHandler(SignatureException.class)
	@ResponseStatus(code = HttpStatus.FORBIDDEN)
	public Map<String, Object> handleSignatureException(SignatureException e, WebRequest webRequest) {
		return createExceptionMessage(new WrongTokenException(), HttpStatus.FORBIDDEN, webRequest);
	}

	@ExceptionHandler(JwtException.class)
	@ResponseStatus(code = HttpStatus.FORBIDDEN)
	public Map<String, Object> handleJwtException(JwtException e, WebRequest webRequest) {
		return createExceptionMessage(e, HttpStatus.FORBIDDEN, webRequest);
	}

	@ExceptionHandler(MalformedJwtException.class)
	@ResponseStatus(code = HttpStatus.FORBIDDEN)
	public Map<String, Object> handleMalformedJwtException(MalformedJwtException e, WebRequest webRequest) {
		return createExceptionMessage(e, HttpStatus.FORBIDDEN, webRequest);
	}

	@ExceptionHandler(ExpiredJwtException.class)
	@ResponseStatus(code = HttpStatus.FORBIDDEN)
	public Map<String, Object> handleExpiredJwtException(ExpiredJwtException e, WebRequest webRequest) {
		return createExceptionMessage(e, HttpStatus.FORBIDDEN, webRequest);
	}

	@ExceptionHandler(BadCredentialsException.class)
	@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
	public Map<String, Object> handleBadCredentialsException(BadCredentialsException e, WebRequest webRequest) {
		return createExceptionMessage(e, HttpStatus.UNAUTHORIZED, webRequest);
	}

	@ExceptionHandler(AppException.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	public Map<String, Object> handleAppException(AppException e, WebRequest webRequest) {
		return createExceptionMessage(e, HttpStatus.BAD_REQUEST, webRequest);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	public Map<String, Object> handleDbIntegrityException(DataIntegrityViolationException e, WebRequest webRequest) {

		return createExceptionMessage(new DBException(), HttpStatus.BAD_REQUEST, webRequest);
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
	public Map<String, Object> handleAppException(Exception e, WebRequest webRequest) {
		return createExceptionMessage(e, HttpStatus.INTERNAL_SERVER_ERROR, webRequest);
	}

	private Map<String, Object> createExceptionMessage(Exception e, HttpStatus status, WebRequest webRequest) {

		Map<String, Object> error = new HashMap<>();
		String timestamp = ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME);

		if (webRequest instanceof ServletWebRequest) {
			error.put("uri", ((ServletWebRequest) webRequest).getRequest().getRequestURI());
		}
		error.put("message", e.getMessage());
		error.put("code", status.value());
		error.put("timestamp", timestamp);
		error.put("reason", status.getReasonPhrase());
		error.put("type", e.getClass().getSimpleName());
		return error;
	}

}
