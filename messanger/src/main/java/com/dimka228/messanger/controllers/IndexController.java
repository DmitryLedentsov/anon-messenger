package com.dimka228.messanger.controllers;

import com.dimka228.messanger.entities.Chat;
import com.dimka228.messanger.entities.User;
import com.dimka228.messanger.exceptions.UserNotFoundException;
import com.dimka228.messanger.services.ChatService;
import com.dimka228.messanger.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@Controller
@RequestMapping("/")
public class IndexController {
    private final ChatService chatService;
    private  final UserService userService;
    @GetMapping(value = "index")
    public String index (Model model) {
        User user = userService.getUser(SecurityContextHolder.getContext().getAuthentication().getName());
        if(user == null){
            throw new UserNotFoundException("");
        }
        model.addAttribute("user",user);
        List<Chat> chats = chatService.getChatsForUser(user);
        model.addAttribute("chats", chats);
        return
                "index" ;
    }

    @GetMapping("/chat/{id}")
    public String chat(@PathVariable int id){

        return "chat";
    }

}