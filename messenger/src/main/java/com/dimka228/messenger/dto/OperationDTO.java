package com.dimka228.messenger.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_DEFAULT)
public class OperationDTO<T> {

	T data;

	String operation;

	public static String ADD = "ADD";

	public static String DELETE = "DELETE";

	public static String UPDATE = "UPDATE";

}
