package com.dimka228.messenger.controllers.socket;

import com.dimka228.messenger.dto.ChatDTO;
import com.dimka228.messenger.dto.ChatCreateDTO;
import com.dimka228.messenger.dto.MessageDTO;
import com.dimka228.messenger.dto.OperationDTO;
import com.dimka228.messenger.entities.Chat;
import com.dimka228.messenger.entities.User;
import com.dimka228.messenger.entities.UserInChat;
import com.dimka228.messenger.exceptions.CannotBanSelfException;
import com.dimka228.messenger.exceptions.WrongPrivilegesException;
import com.dimka228.messenger.models.MessageInfo;
import com.dimka228.messenger.services.ChatService;

import com.dimka228.messenger.services.UserService;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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

	@PostMapping("/chat")
	public ChatDTO sendChat(@RequestBody ChatCreateDTO chatDtoRequest, Principal principal) {
		User user = userService.getUser(principal.getName());
		Chat chat = chatService.addChat(chatDtoRequest.getName());
		chatService.addUserInChat(user, chat, UserInChat.Roles.CREATOR);

		List<String> logins = chatDtoRequest.getUsers();
		logins = logins.stream().filter(userService::checkUser).collect(Collectors.toList());
		List<User> users = logins.stream().map(userService::getUser).collect(Collectors.toList());
		for (User cur : users) {
			if(Objects.equals(cur.getId(), user.getId())) continue;
			chatService.addUserInChat(cur, chat, UserInChat.Roles.REGULAR);
		}

		List<UserInChat> usersInChat = chatService.getUsersInChat(chat);

		chatDtoRequest.getUsers().add(user.getLogin()); // добавляем нашего

		for (UserInChat cur : usersInChat) {
			ChatDTO chatDTO = new ChatDTO(chat.getId(), chat.getName(), cur.getRole());
			OperationDTO<ChatDTO> data = new OperationDTO<>(chatDTO, OperationDTO.ADD);
			notificationService.sendChatOperationToUser(cur.getUser().getId(), data);
		}
		return new ChatDTO(chat.getId(), chat.getName(), chatService.getUserRoleInChat(user, chat));
	}

	@PutMapping("/chat/{chatId}")
	public ChatDTO editChat(@RequestBody ChatCreateDTO chatDtoRequest, @PathVariable Integer chatId,  Principal principal) {
		User user = userService.getUser(principal.getName());
		Chat chat = chatService.getChat(chatId);
		UserInChat userInChat = chatService.getUserInChat(user, chat);
		if (!userInChat.getRole().equals(UserInChat.Roles.CREATOR))
			throw new WrongPrivilegesException();
		boolean chatNameChange = !chatDtoRequest.getName().equals(chat.getName());
		if(chatNameChange){
			chatService.updateChat(chat, (c)->c.setName(chatDtoRequest.getName()));
		}
		List<String> logins = chatDtoRequest.getUsers();
		logins = logins.stream().filter(userService::checkUser).collect(Collectors.toList());
		List<User> updatedUsers = logins.stream().map(userService::getUser).collect(Collectors.toList());
		List<UserInChat> users = chatService.getUsersInChat(chat).stream().collect(Collectors.toList());

		for(UserInChat cur: users){
			if(Objects.equals(user.getId(), cur.getUser().getId())) continue;
			if (!updatedUsers.stream().filter(o -> o.getId().equals(cur.getUser().getId())).findFirst().isPresent()){
				chatService.deleteUserFromChat(cur);
				ChatDTO chatDTO = new ChatDTO(chat.getId());
				OperationDTO<ChatDTO> data = new OperationDTO<>(chatDTO, OperationDTO.DELETE);
				notificationService.sendChatOperationToUser(cur.getUser().getId(), data);
			}
		}
		for (User cur : updatedUsers) {
			if (chatService.isUserInChat(cur, chat)) {
				if(!chatNameChange) continue;
				ChatDTO chatDTO = new ChatDTO(chat.getId(), chatDtoRequest.getName(), chatService.getUserRoleInChat(cur, chat));
				OperationDTO<ChatDTO> data = new OperationDTO<>(chatDTO, OperationDTO.UPDATE);
				notificationService.sendChatOperationToUser(cur.getId(), data);
			} else {
				chatService.addUserInChat(cur, chat, UserInChat.Roles.REGULAR);
				ChatDTO chatDTO = new ChatDTO(chat.getId(), chatDtoRequest.getName(), UserInChat.Roles.REGULAR);
				OperationDTO<ChatDTO> data = new OperationDTO<>(chatDTO, OperationDTO.ADD);
				notificationService.sendChatOperationToUser(cur.getId(), data);
			}
		}

		return new ChatDTO(chat.getId(), chat.getName(), chatService.getUserRoleInChat(user, chat));
	}

	@DeleteMapping("/chat/{chatId}")
	public ChatDTO deleteChat(@PathVariable Integer chatId, Principal principal) {
		User user = userService.getUser(principal.getName());
		Chat chat = chatService.getChat(chatId);
		UserInChat userInChat = chatService.getUserInChat(user, chat);
		ChatDTO chatDTO = new ChatDTO(chatId, chat.getName(), null);
		OperationDTO<ChatDTO> data = new OperationDTO<>(chatDTO, OperationDTO.DELETE);
		List<UserInChat> users = chatService.getUsersInChat(chat);
	
		if (userInChat.getRole().equals(UserInChat.Roles.CREATOR)) {
			chatService.deleteOrLeaveChat(userInChat);
			for (UserInChat cur : users) {
				notificationService.sendChatOperationToUser(cur.getUser().getId(), data);
			}
	
			
		}
		else {
			notificationService.sendChatOperationToUser(user.getId(), data);
			chatService.getMessagesFromUserInChat(userInChat).forEach((msg)->{
				notificationService.sendMessageOperationToChat(chatId, new OperationDTO<>(new MessageDTO(msg.getId()),OperationDTO.DELETE));
			});
			chatService.deleteMessagesFromUserInChat(userInChat);
			chatService.deleteOrLeaveChat(userInChat);
		}
		
		return chatDTO;
	}

	@GetMapping("/chats")
	List<ChatDTO> getChats(Principal principal) {
		User user = userService.getUser(principal.getName());
		List<Chat> chats = chatService.getChatsForUser(user);
		List<ChatDTO> result = new LinkedList<>();
		for(Chat chat: chats){
			UserInChat userInChat = chatService.getUserInChat(user, chat);
			result.add(new ChatDTO(chat.getId(), chat.getName(), userInChat.getRole()));

		}
		//List<ChatDTO> result = chats.stream().map(chat->chatService.getUserInChat(user, chat)).map(userInChat->new ChatDTO(userInChat.getChat().getId(), userInChat.getChat().getName(), userInChat.getRole())).toList();
		return result;
	}

	@GetMapping("/chat/{chatId}")
	ChatDTO getChat(Principal principal, @PathVariable Integer chatId) {
		User user = userService.getUser(principal.getName());
		Chat chat = chatService.getChat(chatId);
		UserInChat userInChat = chatService.getUserInChat(user, chat);

		return new ChatDTO(chat.getId(), chat.getName(), userInChat.getRole());
	}

	@DeleteMapping("/chat/{chatId}/ban/{userId}")
	public void banUser(@PathVariable Integer chatId, @PathVariable Integer userId, Principal principal) {
		User cur = userService.getUser(principal.getName());
		Chat chat = chatService.getChat(chatId);
		User user = userService.getUser(userId);
		UserInChat userInChat = chatService.getUserInChat(cur, chat);

		if (!userInChat.getRole().equals(UserInChat.Roles.CREATOR))
			throw new WrongPrivilegesException();
		if (Objects.equals(user.getId(), cur.getId()))
			throw new CannotBanSelfException();
		List<MessageInfo> messages = chatService.getMessagesForUserInChat(user, chat);

		notificationService.sendChatOperationToUser(userId,
				new OperationDTO<>(new ChatDTO(chatId), OperationDTO.DELETE));
		chatService.deleteOrLeaveChat(chatService.getUserInChat(user, chat));
		for (MessageInfo messageInfo : messages) {
			MessageDTO data = new MessageDTO(messageInfo.getId(), messageInfo.getMessage(), userId, user.getLogin(),
					null);
			OperationDTO<MessageDTO> op = new OperationDTO<MessageDTO>(data, OperationDTO.DELETE);
			notificationService.sendMessageOperationToChat(chatId, op);
		}
	}

	@PostMapping("/chat/{chatId}/add/{login}")
	public void addUserInChat(@PathVariable Integer chatId, @PathVariable String login) {
		User user = userService.getUser(login);
		Chat chat = chatService.getChat(chatId);
		chatService.addUserInChat(user, chat, UserInChat.Roles.REGULAR);

		ChatDTO chatDTO = new ChatDTO(chat.getId(), chat.getName(), UserInChat.Roles.REGULAR);
		OperationDTO<ChatDTO> data = new OperationDTO<>(chatDTO, OperationDTO.ADD);
		notificationService.sendChatOperationToUser(user.getId(), data);
	}

	@GetMapping("/chat/{chatId}/roles")
	public Set<String> getRoles(@PathVariable Integer chatId) {
		Chat chat = chatService.getChat(chatId);
		return chatService.getAllRolesInChat(chat);
	}


}
