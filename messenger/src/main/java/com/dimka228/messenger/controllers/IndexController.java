package com.dimka228.messenger.controllers;

import com.dimka228.messenger.services.ChatService;
import com.dimka228.messenger.services.UserService;

import java.security.Principal;

import lombok.AllArgsConstructor;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.dimka228.messenger.config.properties.ServerProperties;
import com.dimka228.messenger.config.properties.WebSocketProperties;

@AllArgsConstructor
@Controller
@RequestMapping("/")
public class IndexController {
  private final ChatService chatService;
  private final UserService userService;
  private final SimpMessagingTemplate msgTemplate;
  private final WebSocketProperties socketProperties;
  private final ServerProperties serverProperties;
  @GetMapping("/")
  public String app(Model model, Principal principal) {
    model.addAttribute("socket", socketProperties);
    model.addAttribute("server", serverProperties);
    return "app";
  }
}
