package com.dimka228.messenger.event;

import java.security.Principal;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.dimka228.messenger.entities.User;
import com.dimka228.messenger.entities.UserStatus;
import com.dimka228.messenger.services.UserService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@AllArgsConstructor
@Slf4j
public class WebSocketEventListener {

	private final UserService userService;

	@EventListener
	public void handleSessionConnectEvent(SessionConnectEvent event) {
		Principal principal = event.getUser();
		if (principal != null) {
			final User user = userService.getUser(principal.getName());
			userService.addUserStatus(user, UserStatus.ONLINE);

			log.info("New session for user: " + user.getLogin());
		}
	}

	@EventListener
	public void handleSessionDisconnectEvent(SessionDisconnectEvent event) {
		Principal principal = event.getUser();
		if (principal != null) {
			User user = userService.getUser(principal.getName());
			userService.removeUserStatus(user, UserStatus.ONLINE);
			log.info("Session disconnected user: " + user.getLogin());
		}
	}

}
