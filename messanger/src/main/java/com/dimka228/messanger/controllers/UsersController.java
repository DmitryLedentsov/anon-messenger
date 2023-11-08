package com.dimka228.messanger.controllers;

import com.dimka228.messanger.entities.Chat;
import com.dimka228.messanger.entities.User;
import com.dimka228.messanger.entities.UserInChat;
import com.dimka228.messanger.models.MessageInfo;
import com.dimka228.messanger.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@AllArgsConstructor
@Controller
@RequestMapping("/users")
public class UsersController {
    private final UserService userService;
    @GetMapping("/profile/{id}")
    public String chat(@PathVariable Integer id, Model model ){
        //TODO: aa
        /*user = userService.getUser("aboba");
        id = 1;*/


        model.addAttribute("profile", null);
        return "profile";
    }
}
