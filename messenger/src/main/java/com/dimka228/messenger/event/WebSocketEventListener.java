package com.dimka228.messenger.event;

import com.dimka228.messenger.entities.User;
import com.dimka228.messenger.entities.UserStatus;
import com.dimka228.messenger.services.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@AllArgsConstructor
@Slf4j
public class WebSocketEventListener {
  private final UserService userService;

  @EventListener
  public void handleSessionConnectEvent(SessionConnectEvent event) {
    // при подключении высылаем список сообщений
    // UPD: сделал через thmyleaf
    if (event.getUser() == null) return;
    User user = userService.getUser(event.getUser().getName());
    userService.addUserStatus(user, UserStatus.ONLINE);

    log.info("new session for user: " + user.getLogin());
  }

  @EventListener
  public void handleSessionDisconnectEvent(SessionDisconnectEvent event) {
    if (event.getUser() == null) return;
    User user = userService.getUser(event.getUser().getName());
    userService.removeUserStatus(user, UserStatus.ONLINE);
    log.info("session disconnected user: " + user.getLogin());
  }
}