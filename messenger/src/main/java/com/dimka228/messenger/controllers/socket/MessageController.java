package com.dimka228.messenger.controllers.socket;

import java.security.Principal;
import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dimka228.messenger.dto.MessageDTO;
import com.dimka228.messenger.dto.OperationDTO;
import com.dimka228.messenger.entities.Chat;
import com.dimka228.messenger.entities.Message;
import com.dimka228.messenger.entities.User;
import com.dimka228.messenger.services.ChatService;
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

	@PostMapping("/{id}/send")
	public MessageDTO sendMessage(@PathVariable Integer id, @RequestBody MessageDTO chatMessage, Principal principal) {
		User user = userService.getUser(principal.getName());
		Chat chat = chatService.getChat(id);

		Message added = chatService.addMessage(user, chat, chatMessage.getMessage());
		MessageDTO fullMsg = new MessageDTO(added.getId(), added.getData(), user.getId(), user.getLogin(),
				DateConverter.format(Instant.now()));
		OperationDTO<MessageDTO> data = new OperationDTO<>(fullMsg, OperationDTO.ADD);
		notificationService.sendMessageOperationToChat(id, data);
		return chatMessage;
	}

	@DeleteMapping("/{id}/message/{msgId}")
	public MessageDTO deleteMessage(@PathVariable Integer id, @PathVariable Integer msgId, Principal principal) {
		User user = userService.getUser(principal.getName());
		Chat chat = chatService.getChat(id);
		Message msg = chatService.getMessage(msgId);
		chatService.deleteMessageFromUserInChat(user, chat, msg);

		MessageDTO fullMsg = new MessageDTO(msgId, "aa", user.getId(), user.getLogin(),
				DateConverter.format(Instant.now()));
		OperationDTO<MessageDTO> data = new OperationDTO<>(fullMsg, OperationDTO.DELETE);
		notificationService.sendMessageOperationToChat(id, data);
		return fullMsg;
	}

	@GetMapping("/{id}/messages")
	List<MessageDTO> messages(@PathVariable Integer id, Principal principal) {
		Chat chat = chatService.getChat(id);
		User user = userService.getUser(principal.getName());
		List<MessageDTO> messages = chatService.getMessagesForUserInChat(user, chat).stream().map(m->MessageDTO.fromMessageInfo(m)).toList();
		return messages;
	}

}
