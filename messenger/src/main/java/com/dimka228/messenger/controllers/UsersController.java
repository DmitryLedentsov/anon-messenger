package com.dimka228.messenger.controllers;

import java.security.Principal;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dimka228.messenger.dto.UserProfileDTO;
import com.dimka228.messenger.entities.Chat;
import com.dimka228.messenger.entities.User;
import com.dimka228.messenger.entities.UserInChat;
import com.dimka228.messenger.services.ChatService;
import com.dimka228.messenger.services.UserService;
import com.dimka228.messenger.utils.DateConverter;

import lombok.AllArgsConstructor;


@AllArgsConstructor
@RestController
@RequestMapping(consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
public class UsersController {

	private final UserService userService;

	private final ChatService chatService;

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

	@GetMapping("/chat/{chatId}/users")
	public List<UserProfileDTO> profiles(@PathVariable Integer chatId,Principal principal) {
		User cur = userService.getUser(principal.getName());

		List<UserProfileDTO> profiles = new LinkedList<>();
		Chat chat = chatService.getChat(chatId);
		UserInChat cUserInChat = chatService.getUserInChat(cur, chat);
		for (UserInChat userInChat : chatService.getUsersInChat(chat)) {
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
