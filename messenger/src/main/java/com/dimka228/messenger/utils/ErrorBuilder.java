package com.dimka228.messenger.utils;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

public class ErrorBuilder {

	public static Map<String, Object> createExceptionMessage(Throwable e, HttpStatus status, WebRequest webRequest) {

		Map<String, Object> error = new HashMap<>();
		String timestamp = ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME);

		if (webRequest instanceof ServletWebRequest servletWebRequest) {
			error.put("uri", servletWebRequest.getRequest().getRequestURI());
		}
		error.put("message", e.getMessage());
		error.put("code", status.value());
		error.put("timestamp", timestamp);
		error.put("reason", status.getReasonPhrase());
		error.put("error", e.getClass().getSimpleName());
		return error;
	}

	public static Map<String, Object> createExceptionMessage(Throwable e, HttpStatus status) {

		Map<String, Object> error = new HashMap<>();
		String timestamp = ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME);

		error.put("message", e.getMessage());
		error.put("code", status.value());
		error.put("timestamp", timestamp);
		error.put("reason", status.getReasonPhrase());
		error.put("error", e.getClass().getSimpleName());
		return error;
	}

	public static Map<String, Object> createExceptionMessage(Throwable e) {
		Map<String, Object> error = new HashMap<>();
		String timestamp = ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME);
		error.put("message", e.getMessage());
		error.put("timestamp", timestamp);
		error.put("error", e.getClass().getSimpleName());
		return error;
	}

}
