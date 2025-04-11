package com.dimka228.messenger.controllers;

import java.security.Principal;
import java.util.Set;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dimka228.messenger.config.RoleConfig;
import com.dimka228.messenger.entities.*;
import com.dimka228.messenger.exceptions.WrongPrivilegesException;
import com.dimka228.messenger.services.ChatService;
import com.dimka228.messenger.services.RoleService;
import com.dimka228.messenger.services.UserService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping(consumes = { MediaType.APPLICATION_JSON_VALUE },
		produces = { MediaType.APPLICATION_JSON_VALUE })
public class RolesController {
    private final UserService userService;
	private final ChatService chatService;
    private final RoleService roleService;

    @PostMapping("chat/{chatId}/user/{userId}/set-role/{role}")
	public void setRole(@PathVariable Integer chatId, @PathVariable Integer userId, @PathVariable String role, Principal principal) {
		Chat chat = chatService.getChat(chatId);
		User user = userService.getUser(userId);
        User cur = userService.getUser(principal.getName());
        UserInChat userInChat = chatService.getUserInChat(user, chat);
        if(!roleService.isHigherPriority(chatService.getUserInChat(cur, chat), userInChat)) throw new WrongPrivilegesException();//проверяем что текущий пользователь выше в ранге чем изменяемый
        if(!roleService.isHigherPriority(roleService.getRoleConfig(chatService.getUserInChat(cur, chat)), roleService.getRoleConfig(role))) throw new WrongPrivilegesException(); //проверяем что текущий пользователь выше в ранге чем новая роль
        chatService.updateUserRoleInChat(userInChat, role);
        
	}

    @GetMapping("chat/{chatId}/roles")
	public Set<String> getRoles(@PathVariable Integer chatId, Principal principal) {
		Chat chat = chatService.getChat(chatId);
        chatService.getUserInChat(userService.getUser(principal.getName()),chat);

		//return chatService.getAllRolesInChat(chat);
        return roleService.getRolesNames();
	}
    @GetMapping("chat/{chatId}/role/{role}")
	public RoleConfig getRoleConfig(@PathVariable Integer chatId, @PathVariable String role, Principal principal) {
		Chat chat = chatService.getChat(chatId);
        chatService.getUserInChat(userService.getUser(principal.getName()),chat);
		return roleService.getRoleConfig(role);
	}
}
