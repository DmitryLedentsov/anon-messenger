package com.dimka228.messenger.controllers;

import com.dimka228.messenger.services.ChatService;
import com.dimka228.messenger.services.UserService;
import java.security.Principal;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@Controller
@RequestMapping("/")
public class IndexController {
  private final ChatService chatService;
  private final UserService userService;
  private final SimpMessagingTemplate msgTemplate;

  @GetMapping("/")
  public String app(Model model, Principal principal) {

    return "app";
  }

  /* @GetMapping("chat/{id}/messages")
  @ResponseBody
  List<MessageInfo> messages(@PathVariable Integer id, Principal principal) {
      User user = userService.getUser(principal.getName());

      Chat chat = chatService.getChat(id);
      UserInChat userInChat = chatService.getUserInChat(user.getId(),chat.getId());
      List<MessageInfo> messages = chatService.getMessagesForUserInChat(user, chat);
      return messages;
  }*/

  // TODO: переименовать в ChatController /chat
  // переи

}
