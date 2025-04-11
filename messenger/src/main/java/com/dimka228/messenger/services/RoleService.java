package com.dimka228.messenger.services;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.dimka228.messenger.entities.Role;
import com.dimka228.messenger.entities.UserInChat;
import com.dimka228.messenger.exceptions.RoleNotFoundException;
import com.dimka228.messenger.repositories.RoleRepository;

import lombok.AllArgsConstructor;
@Service
@AllArgsConstructor
public class RoleService {
	private final RoleRepository roleRepository;

	public final Role getRole(String name){
		return roleRepository.findByName(name).orElseThrow(RoleNotFoundException::new);
	}
	public Set<String> getRolesNames(){
		return roleRepository.findAll().stream().map(Role::getName).collect(Collectors.toSet());
	}


	public boolean isHigherPriority(Role firstRole, Role secondRole){
		return firstRole.getPriority() > secondRole.getPriority();
	}
	public boolean isHigherPriority(UserInChat first, UserInChat second){
		return isHigherPriority(first.getRole(), second.getRole());
	}

	public boolean checkRoleExists(String name){
		return roleRepository.findByName(name).isPresent();
	}
}
