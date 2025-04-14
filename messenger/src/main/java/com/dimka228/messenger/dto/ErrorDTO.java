package com.dimka228.messenger.dto;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_DEFAULT)
public class ErrorDTO {

	String message;

	int code;

	String timestamp;

	String reason;

	String error;

	String uri;

	public ErrorDTO(Throwable e) {
		message = e.getMessage();
		error = e.getClass().getSimpleName();
		timestamp = ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME);
	}

}
