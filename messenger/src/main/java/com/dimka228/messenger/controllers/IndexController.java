package com.dimka228.messenger.controllers;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dimka228.messenger.config.properties.FrontendSettings;
import com.dimka228.messenger.config.properties.ServerProperties;
import com.dimka228.messenger.config.properties.WebSocketProperties;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Controller
@RequestMapping("/")
public class IndexController {

	private final WebSocketProperties socketProperties;

	private final ServerProperties serverProperties;

	@GetMapping("/")
	public String app(Model model, Principal principal) {
		FrontendSettings settings = new FrontendSettings();
		settings.setServerUrl(serverProperties.getPublicUrl());	
		model.addAttribute("settings", settings);
		return "app";
	}

	@GetMapping("/welcome")
	public String intro(Model model, Principal principal) {
		return "intro";
	}

}
