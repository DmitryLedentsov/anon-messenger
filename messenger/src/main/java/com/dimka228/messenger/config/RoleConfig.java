package com.dimka228.messenger.config;

import java.util.List;

import lombok.Data;

@Data
public class RoleConfig {
    private List<String> permissions;
    private Integer priority;
}