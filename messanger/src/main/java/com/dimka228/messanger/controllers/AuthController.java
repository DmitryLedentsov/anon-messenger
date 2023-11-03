package com.dimka228.messanger.controllers;

import com.dimka228.messanger.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private  final UserService userService;
    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "login")
    public String login () {
        return
                "login" ;
    }

    @GetMapping(value = "register")
    public String register () {
        return
                "register" ;
    }

    @GetMapping(value = "users")
    public String users (Model model) {
        model.addAttribute("users",userService.allUsers());
        return
                "users" ;
    }

    //@PostMapping(value = "register")

}