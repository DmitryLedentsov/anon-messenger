package com.dimka228.messanger.event;

import com.dimka228.messanger.entities.User;
import com.dimka228.messanger.entities.UserStatus;
import com.dimka228.messanger.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@AllArgsConstructor
public class WebSocketEventListener {
    private final UserService userService;
    @EventListener
    public void handleSessionConnectEvent(SessionConnectEvent event) {
        //при подключении высылаем список сообщений
        //UPD: сделал через thmyleaf
        if(event.getUser()==null) return;
        User user = userService.getUser(event.getUser().getName());
        userService.addUserStatus(user, UserStatus.ONLINE);

        System.out.println("new session for user: " + user.getLogin());
    }

    @EventListener
    public void handleSessionDisconnectEvent(SessionDisconnectEvent event) {
        if(event.getUser()==null) return;
        User user = userService.getUser(event.getUser().getName());
        userService.removeUserStatus(user, UserStatus.ONLINE);
        System.out.println("session disconnected user: " + user.getLogin());
    }
}
