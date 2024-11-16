package com.dimka228.messanger.controllers;

import com.dimka228.messanger.dto.ChatDTO;
import com.dimka228.messanger.dto.OperationDTO;
import com.dimka228.messanger.entities.Chat;
import com.dimka228.messanger.entities.Message;
import com.dimka228.messanger.entities.User;
import com.dimka228.messanger.entities.UserInChat;
import com.dimka228.messanger.exceptions.UserNotFoundException;
import com.dimka228.messanger.models.MessageInfo;
import com.dimka228.messanger.services.ChatService;
import com.dimka228.messanger.services.UserService;
import com.dimka228.messanger.utils.LoginParser;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpResponse;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Controller
@RequestMapping("/")
public class IndexController {
    private final ChatService chatService;
    private  final UserService userService;
    private final SimpMessagingTemplate msgTemplate;

    @GetMapping("/")
    public String app (Model model, Principal principal) {
       
        return
                "app" ;
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


    //TODO: переименовать в ChatController /chat
    //переи




}