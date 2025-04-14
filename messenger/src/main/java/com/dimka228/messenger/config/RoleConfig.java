package com.dimka228.messenger.config;

import java.util.Set;

import lombok.Data;

@Data
public class RoleConfig {

	private Set<String> permissions;

	private Integer priority;

}