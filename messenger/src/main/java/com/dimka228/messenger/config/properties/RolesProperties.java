package com.dimka228.messenger.config.properties;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.dimka228.messenger.config.RoleConfig;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "messenger.security")
public class RolesProperties {
    private Map<String, RoleConfig> roles = new HashMap<>();

    public List<String> getPermissionsForRole(String roleName) {
        return roles.getOrDefault(roleName, new RoleConfig()).getPermissions();
    }
    public RoleConfig getRoleConfig(String name){
        return roles.getOrDefault(name, new RoleConfig());
    }
}