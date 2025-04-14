package com.dimka228.messenger.controllers;

import java.security.Principal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dimka228.messenger.dto.MessageDTO;
import com.dimka228.messenger.dto.OperationDTO;
import com.dimka228.messenger.entities.Chat;
import com.dimka228.messenger.entities.Message;
import com.dimka228.messenger.entities.User;
import com.dimka228.messenger.entities.UserInChat;
import com.dimka228.messenger.exceptions.WrongPrivilegesException;
import com.dimka228.messenger.services.ChatService;
import com.dimka228.messenger.services.RoleService;
import com.dimka228.messenger.services.UserService;
import com.dimka228.messenger.services.interfaces.NotificationService;
import com.dimka228.messenger.utils.DateConverter;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping(value = "chat", consumes = { MediaType.APPLICATION_JSON_VALUE },
		produces = { MediaType.APPLICATION_JSON_VALUE })
public class MessageController {

	@Qualifier("notificationService")
	private final NotificationService notificationService;

	private final UserService userService;

	private final ChatService chatService;

	private final RoleService roleService;

	@PostMapping("/{id}/send")
	public MessageDTO sendMessage(@PathVariable Integer id, @RequestBody MessageDTO chatMessage, Principal principal)
			throws InterruptedException {
		User user = userService.getUser(principal.getName());
		Chat chat = chatService.getChat(id);
		UserInChat userInChat = chatService.getUserInChat(user, chat);
		if (!userInChat.getRole().isSendMessage())
			throw new WrongPrivilegesException();
		Message added = chatService.addMessage(userInChat, chatMessage.getMessage());
		MessageDTO fullMsg = new MessageDTO(added.getId(), added.getData(), user.getId(), user.getLogin(),
				DateConverter.format(Instant.now()));
		OperationDTO<MessageDTO> data = new OperationDTO<>(fullMsg, OperationDTO.ADD);
		notificationService.sendMessageOperationToChat(id, data);
		return chatMessage;
	}

	@DeleteMapping("/{id}/message/{msgId}")
	public void deleteMessage(@PathVariable Integer id, @PathVariable Integer msgId, Principal principal) {
		User user = userService.getUser(principal.getName());
		Chat chat = chatService.getChat(id);
		UserInChat userInChat = chatService.getUserInChat(user, chat);
		Message msg = chatService.getMessage(msgId);
		if (!userInChat.getRole().isDeleteMessage() && !Objects.equals(user.getId(), msg.getSender().getId()))
			throw new WrongPrivilegesException();
		chatService.deleteMessageFromUserInChat(userInChat, msg);

		OperationDTO<MessageDTO> data = new OperationDTO<>(new MessageDTO(msg.getId()), OperationDTO.DELETE);
		notificationService.sendMessageOperationToChat(id, data);
	}

	@GetMapping("/{id}/messages")
	@SuppressWarnings("unused")
	List<MessageDTO> messages(@PathVariable Integer id, Principal principal,
			@RequestParam(defaultValue = "0", name = "page") int pageNumber,
			@RequestParam(defaultValue = "100", name = "count") int pageSize,
			@RequestParam(required = false, value = "filter") String filter) {
		Chat chat = chatService.getChat(id);
		User user = userService.getUser(principal.getName());
		Pageable page = PageRequest.of(pageNumber, pageSize, Sort.by("SEND_TIME").descending());
		List<MessageDTO> messages = chatService.getMessagesForUserInChat(chatService.getUserInChat(user, chat), page)
			.stream()
			.filter((m) -> filter != null ? m.getMessage().contains(filter) : true)
			.map(m -> MessageDTO.fromMessageInfo(m))
			.toList();
		// messages = new ArrayList<>(messages);
		// Collections.reverse(messages);
		return messages;
	}

	@DeleteMapping("/{id}/messages")
	@SuppressWarnings("unused")
	void clear(@PathVariable Integer id, Principal principal) {
		Chat chat = chatService.getChat(id);
		User user = userService.getUser(principal.getName());
		UserInChat userInChat = chatService.getUserInChat(user, chat);
		chatService.deleteMessagesFromUserInChat(chatService.getUserInChat(user, chat));
		chatService.getMessagesFromUserInChat(userInChat).forEach((msg) -> {
			OperationDTO<MessageDTO> data = new OperationDTO<>(new MessageDTO(msg.getId()), OperationDTO.DELETE);
			notificationService.sendMessageOperationToChat(id, data);
		});

	}

}
