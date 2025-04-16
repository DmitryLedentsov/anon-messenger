package com.dimka228.messenger.controllers;

import java.security.Principal;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dimka228.messenger.dto.ChatCreateDTO;
import com.dimka228.messenger.dto.ChatDTO;
import com.dimka228.messenger.dto.MessageDTO;
import com.dimka228.messenger.dto.OperationDTO;
import com.dimka228.messenger.entities.Chat;
import com.dimka228.messenger.entities.User;
import com.dimka228.messenger.entities.UserInChat;
import com.dimka228.messenger.exceptions.WrongPrivilegesException;
import com.dimka228.messenger.services.ChatService;
import com.dimka228.messenger.services.RoleService;
import com.dimka228.messenger.services.UserService;
import com.dimka228.messenger.services.interfaces.NotificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
public class ChatController {

	@Qualifier("notificationService")
	private final NotificationService notificationService;

	private final UserService userService;

	private final ChatService chatService;

	private final RoleService roleService;

	@PostMapping("/chat")
	public ChatDTO sendChat(@RequestBody ChatCreateDTO chatDtoRequest, Principal principal) {
		User user = userService.getUser(principal.getName());
		Chat chat = chatService.addChat(chatDtoRequest.getName());
		chatService.addUserInChat(user, chat, roleService.getRole(UserInChat.Roles.CREATOR));
		
		List<String> logins = chatDtoRequest.getUsers();
		if(logins==null){
			ChatDTO chatDTO = new ChatDTO(chat.getId(), chat.getName(), UserInChat.Roles.CREATOR);
			OperationDTO<ChatDTO> data = new OperationDTO<>(chatDTO, OperationDTO.ADD);
			notificationService.sendChatOperationToUser(user.getId(), data);
			return new ChatDTO(chat.getId(), chat.getName(), UserInChat.Roles.CREATOR);
		}
		logins = logins.stream().filter((login)->!login.equals(user.getLogin())).toList();
		chatService.addUsersInChat(chat, logins);

		List<UserInChat> usersInChat = chatService.getUsersInChat(chat);

		chatDtoRequest.getUsers().add(user.getLogin()); // добавляем нашего

		for (UserInChat cur : usersInChat) {
			ChatDTO chatDTO = new ChatDTO(chat.getId(), chat.getName(), cur.getRole().getName());
			OperationDTO<ChatDTO> data = new OperationDTO<>(chatDTO, OperationDTO.ADD);
			notificationService.sendChatOperationToUser(cur.getUser().getId(), data);
		}
		return new ChatDTO(chat.getId(), chat.getName(), chatService.getUserRoleInChat(user, chat).getName());
	}
	@PostMapping("/chat/create/{name}")
	public ChatDTO createEmptyChat(@PathVariable String name, Principal principal) {
		User user = userService.getUser(principal.getName());
		Chat chat = chatService.addChat(name);
		chatService.addUserInChat(user, chat, roleService.getRole(UserInChat.Roles.CREATOR));
		
		ChatDTO chatDTO = new ChatDTO(chat.getId(), chat.getName(), UserInChat.Roles.CREATOR);
		OperationDTO<ChatDTO> data = new OperationDTO<>(chatDTO, OperationDTO.ADD);
		notificationService.sendChatOperationToUser(user.getId(), data);
		return new ChatDTO(chat.getId(), chat.getName(), chatService.getUserRoleInChat(user, chat).getName());
	}


	@DeleteMapping("/chat/{chatId}")
	public void deleteChat(@PathVariable Integer chatId, Principal principal) {
		User user = userService.getUser(principal.getName());
		Chat chat = chatService.getChat(chatId);
		UserInChat userInChat = chatService.getUserInChat(user, chat);
		ChatDTO chatDTO = new ChatDTO(chatId, chat.getName(), null);
		OperationDTO<ChatDTO> data = new OperationDTO<>(chatDTO, OperationDTO.DELETE);
		List<UserInChat> users = chatService.getUsersInChat(chat);

		if (userInChat.getRole().getName().equals(UserInChat.Roles.CREATOR)) {
			chatService.deleteOrLeaveChat(userInChat);
			for (UserInChat cur : users) {
				notificationService.sendChatOperationToUser(cur.getUser().getId(), data);
			}

		}
		else {
			notificationService.sendChatOperationToUser(user.getId(), data);
			chatService.getMessagesFromUserInChat(userInChat).forEach((msg) -> {
				notificationService.sendMessageOperationToChat(chatId,
						new OperationDTO<>(new MessageDTO(msg.getId()), OperationDTO.DELETE));
			});
			chatService.deleteMessagesFromUserInChat(userInChat);
			chatService.deleteOrLeaveChat(userInChat);
		}

	}

	@GetMapping("/chats")
	@SuppressWarnings("unused")
	List<ChatDTO> getChats(Principal principal) {
		User user = userService.getUser(principal.getName());
		List<Chat> chats = chatService.getChatsForUser(user);
		List<ChatDTO> result = new LinkedList<>();
		for (Chat chat : chats) {
			UserInChat userInChat = chatService.getUserInChat(user, chat);
			result.add(new ChatDTO(chat.getId(), chat.getName(), userInChat.getRole().getName()));

		}
		// List<ChatDTO> result = chats.stream().map(chat->chatService.getUserInChat(user,
		// chat)).map(userInChat->new ChatDTO(userInChat.getChat().getId(),
		// userInChat.getChat().getName(), userInChat.getRole())).toList();
		return result;
	}

	@GetMapping("/chat/{chatId}")
	@SuppressWarnings("unused")
	ChatDTO getUserInChat(Principal principal, @PathVariable Integer chatId) {
		User user = userService.getUser(principal.getName());
		Chat chat = chatService.getChat(chatId);
		UserInChat userInChat = chatService.getUserInChat(user, chat);

		return new ChatDTO(chat.getId(), chat.getName(), userInChat.getRole().getName());
	}


	@PostMapping("/chat/{chatId}/set-name/{name}")
	public void editChatName(@PathVariable Integer chatId, @PathVariable String name, Principal principal) {
		User user = userService.getUser(principal.getName());
		Chat chat = chatService.getChat(chatId);
		UserInChat userInChat = chatService.getUserInChat(user, chat);
		
		boolean chatNameChange = !name.equals(chat.getName());
		if(!chatNameChange) return;
	
		if (!userInChat.getRole().isEditChat()) throw new WrongPrivilegesException();
		chatService.updateChat(chat, (c) -> c.setName(name));
		
		for (UserInChat cur : chatService.getUsersInChat(chat)) {
			ChatDTO chatDTO = new ChatDTO(chat.getId(), name, cur.getRole().getName());
			OperationDTO<ChatDTO> data = new OperationDTO<>(chatDTO, OperationDTO.UPDATE);
			notificationService.sendChatOperationToUser(cur.getUser().getId(), data);
		}		
	}


}
