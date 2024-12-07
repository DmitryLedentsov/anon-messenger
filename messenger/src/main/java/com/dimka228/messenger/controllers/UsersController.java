package com.dimka228.messenger.controllers;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dimka228.messenger.dto.UserProfileDTO;
import com.dimka228.messenger.entities.Chat;
import com.dimka228.messenger.entities.User;
import com.dimka228.messenger.entities.UserInChat;
import com.dimka228.messenger.entities.UserProfile;
import com.dimka228.messenger.services.ChatService;
import com.dimka228.messenger.services.UserService;
import com.dimka228.messenger.utils.DateConverter;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
public class UsersController {
  private final UserService userService;
  private final ChatService chatService;

  @GetMapping("/chat/{chatId}/user/{id}")
  public UserProfileDTO profile(@PathVariable Integer chatId, @PathVariable Integer id) {
    // TODO: aa
    /*user = userService.getUser("aboba");
    id = 1;*/

    User user = userService.getUser(id);
    Chat chat = chatService.getChat(chatId);
    UserProfile profile = userService.getUserProfile(user);
    Set<String> userStatuses =
        userService.getUserStatusList(user).stream()
            .map(s -> s.getName())
            .collect(Collectors.toSet());
    UserInChat userInChat = chatService.getUserInChat(user, chat);
    UserProfileDTO profileDTO =
        new UserProfileDTO(
            user.getLogin(),
            userInChat.getRole(),
            user.getId(),
            userStatuses,
            DateConverter.format(userInChat.getJoinTime()));

    return profileDTO;
  }
}
