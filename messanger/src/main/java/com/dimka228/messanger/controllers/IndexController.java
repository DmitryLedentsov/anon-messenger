package com.dimka228.messanger.controllers;

import com.dimka228.messanger.entities.Chat;
import com.dimka228.messanger.entities.User;
import com.dimka228.messanger.entities.UserInChat;
import com.dimka228.messanger.exceptions.UserNotFoundException;
import com.dimka228.messanger.models.MessageInfo;
import com.dimka228.messanger.services.ChatService;
import com.dimka228.messanger.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@Controller
@RequestMapping("/")
public class IndexController {
    private final ChatService chatService;
    private  final UserService userService;
    @GetMapping(value = "index")
    public String index (Model model) {
        User user = getCurrentUser();
        //TODO: aa
        user = userService.getUser("aboba");
        model.addAttribute("user",user);
        List<Chat> chats = chatService.getChatsForUser(user);
        model.addAttribute("chats", chats);
        return
                "index" ;
    }

    @GetMapping("/chat/{id}")
    public String chat(@PathVariable Integer id, Model model ){
        User user = getCurrentUser();

        //TODO: aa
        user = userService.getUser("aboba");
        id = 1;

        model.addAttribute("user",user);

        Chat chat = chatService.getChat(id);
        UserInChat userInChat = chatService.getUserInChat(user.getId(),chat.getId());
        List<MessageInfo> messages = chatService.getMessagesForUserInChat(user, chat);
        model.addAttribute("messages", messages);
        return "chat";
    }

    @GetMapping("chat/create")
    public String createView (Model model) {
        User user = getCurrentUser();
        //TODO: aa
        user = userService.getUser("aboba");
        model.addAttribute("user",user);
        return
                "chat-create" ;
    }

    @PostMapping("chat/create")
    public String signupUser(@ModelAttribute User user, Model model) {
        return null;
    }

    private User getCurrentUser(){
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUser(login);
        if(user == null){
            throw new UserNotFoundException(login);
        }
        return user;
    }

    //TODO: переименовать в ChatController /chat
    //переи




}