package com.dimka228.messenger.controllers;

import com.dimka228.messenger.entities.User;
import com.dimka228.messenger.exceptions.UserExistsException;
import com.dimka228.messenger.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
@RequestMapping("/oldauth")
public class AuthController {
  private final UserService userService;

  @GetMapping(value = "login")
  public String login() {
    return "login";
  }

  @GetMapping(value = "register")
  public String register() {
    return "register";
  }

  @PostMapping(value = "register")
  public String signupUser(@ModelAttribute User user, Model model) {
    String signupError = null;

    if (userService.checkUser(user.getLogin())) {
      signupError = "The username already exists.";
    }
    model.addAttribute("user", user.clone());

    if (signupError == null) {
      try {
        userService.registerUser(user);
      } catch (UserExistsException e) {
        signupError = "There was an error signing you up. Please try again.";
      }
    }

    if (signupError == null) {
      model.addAttribute("signupSuccess", true);
      return "login";
    } else {
      model.addAttribute("error", signupError);
    }

    return "register";
  }

  @GetMapping(value = "users")
  public String users(Model model) {
    model.addAttribute("users", userService.allUsers());
    return "users";
  }
}
