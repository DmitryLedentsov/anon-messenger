package com.dimka228.messenger.controllers;

import java.security.Principal;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dimka228.messenger.dto.ChatDTO;
import com.dimka228.messenger.dto.OperationDTO;
import com.dimka228.messenger.dto.UserProfileDTO;
import com.dimka228.messenger.entities.Chat;
import com.dimka228.messenger.entities.User;
import com.dimka228.messenger.entities.UserInChat;
import com.dimka228.messenger.exceptions.CannotBanSelfException;
import com.dimka228.messenger.exceptions.WrongPrivilegesException;
import com.dimka228.messenger.services.ChatService;
import com.dimka228.messenger.services.RoleService;
import com.dimka228.messenger.services.UserService;
import com.dimka228.messenger.services.interfaces.NotificationService;
import com.dimka228.messenger.utils.DateConverter;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping(consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
public class UsersController {

	private final UserService userService;

	private final ChatService chatService;
	private final RoleService roleService;

	@Qualifier("notificationService")
	private final NotificationService notificationService;

	@GetMapping("/chat/{chatId}/user/{id}")
	public UserProfileDTO profile(@PathVariable Integer chatId, @PathVariable Integer id) {
		User user = userService.getUser(id);
		Chat chat = chatService.getChat(chatId);
		Set<String> userStatuses = userService.getUserStatusList(user)
			.stream()
			.map(s -> s.getName())
			.collect(Collectors.toSet());
		UserInChat userInChat = chatService.getUserInChat(user, chat);
		UserProfileDTO profileDTO = new UserProfileDTO(user.getLogin(), userInChat.getRole(), user.getId(),
				userStatuses, DateConverter.format(userInChat.getJoinTime()));

		return profileDTO;
	}
	@DeleteMapping("/chat/{chatId}/user/{userId}")
	public void banUser(@PathVariable Integer chatId, @PathVariable Integer userId, Principal principal) {
		User cur = userService.getUser(principal.getName());
		Chat chat = chatService.getChat(chatId);
		User user = userService.getUser(userId);
		UserInChat userInChat = chatService.getUserInChat(cur, chat);

		if (!roleService.checkPrivilege(userInChat, "BAN_USER"))
			throw new WrongPrivilegesException();
		if (Objects.equals(user.getId(), cur.getId()))
			throw new CannotBanSelfException();
	
		chatService.deleteOrLeaveChat(chatService.getUserInChat(user, chat));
		notificationService.sendChatOperationToUser(userId, new OperationDTO<>(new ChatDTO(chatId), OperationDTO.DELETE));
		/*List<MessageInfo> messages = chatService.getMessagesForUserInChat(user, chat);

		
		for (MessageInfo messageInfo : messages) {
			MessageDTO data = new MessageDTO(messageInfo.getId(), messageInfo.getMessage(), userId, user.getLogin(),
					null);
			OperationDTO<MessageDTO> op = new OperationDTO<>(data, OperationDTO.DELETE);
			notificationService.sendMessageOperationToChat(chatId, op);
		}*/
	}

	@PostMapping("/chat/{chatId}/user/{login}")
	public void addUserInChat(@PathVariable Integer chatId, @PathVariable String login, Principal principal) {
		User user = userService.getUser(login);
		Chat chat = chatService.getChat(chatId);
		User cur = userService.getUser(principal.getName());
		UserInChat userInChat = chatService.getUserInChat(cur, chat);
		if (!roleService.checkPrivilege(userInChat, "ADD_USER")) throw new WrongPrivilegesException();
		chatService.addUserInChat(user, chat, UserInChat.Roles.REGULAR);

		ChatDTO chatDTO = new ChatDTO(chat.getId(), chat.getName(), UserInChat.Roles.REGULAR);
		OperationDTO<ChatDTO> data = new OperationDTO<>(chatDTO, OperationDTO.ADD);
		notificationService.sendChatOperationToUser(user.getId(), data);
	}

	@GetMapping("/chat/{chatId}/users")
	public List<UserProfileDTO> profiles(@PathVariable Integer chatId, Principal principal) {

		List<UserProfileDTO> profiles = new LinkedList<>();
		Chat chat = chatService.getChat(chatId);
		UserInChat curUserInChat = chatService.getUserInChat(userService.getUser(principal.getName()), chat);
		
		for (UserInChat userInChat : chatService.getUsersInChat(chat)) {
			if (userInChat.getUser().equals(curUserInChat.getUser())) continue; //себя пропускаем
			Set<String> userStatuses = userService.getUserStatusList(userInChat.getUser())
				.stream()
				.map(s -> s.getName())
				.collect(Collectors.toSet());
			UserProfileDTO profileDTO = new UserProfileDTO(userInChat.getUser().getLogin(), userInChat.getRole(),
					userInChat.getUser().getId(), userStatuses, DateConverter.format(userInChat.getJoinTime()));
			profiles.add(profileDTO);

		}
		return profiles;
	}

	@DeleteMapping("user")
	public void deleteUser(Principal principal) {
		User cur = userService.getUser(principal.getName());
		userService.deleteUser(cur.getId());
	}

}
