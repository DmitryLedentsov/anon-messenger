package com.dimka228.messenger.services;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.dimka228.messenger.config.RoleConfig;
import com.dimka228.messenger.config.properties.RolesProperties;
import com.dimka228.messenger.entities.UserInChat;
import com.dimka228.messenger.exceptions.RoleNotFoundException;

import lombok.AllArgsConstructor;
@Service
@AllArgsConstructor
public class RoleService {
    private final RolesProperties rolesConfig;
	public boolean checkPrivilege(UserInChat userInChat, String permission){
		return rolesConfig.getPermissionsForRole(userInChat.getRole()).contains(permission);
	}
	public RoleConfig getRoleConfig(String name){
		if(!checkRoleExists(name)) throw new RoleNotFoundException();
		return rolesConfig.getRoleConfig(name);
	}
	public RoleConfig getRoleConfig(UserInChat user){
		return getRoleConfig(user.getRole());
	}
	public Set<String> getRolesNames(){
		return rolesConfig.getRoles().keySet();
	}

	public boolean isHigherPriority(RoleConfig firstRole, RoleConfig secondRole){
		return firstRole.getPriority() > secondRole.getPriority();
	}
	public boolean isHigherPriority(UserInChat first, UserInChat second){
		RoleConfig firstRole = getRoleConfig(first);
		RoleConfig secondRole = getRoleConfig(second);
		return isHigherPriority(firstRole, secondRole);
	}

	public boolean checkRoleExists(String name){
		return rolesConfig.getRoles().containsKey(name);
	}
}
