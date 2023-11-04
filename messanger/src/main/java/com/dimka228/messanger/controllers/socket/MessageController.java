package com.dimka228.messanger.controllers.socket;

import com.dimka228.messanger.dto.MessageDTO;
import com.dimka228.messanger.entities.Chat;
import com.dimka228.messanger.entities.Message;
import com.dimka228.messanger.entities.User;
import com.dimka228.messanger.entities.UserInChat;
import com.dimka228.messanger.exceptions.UserNotFoundException;
import com.dimka228.messanger.models.MessageInfo;
import com.dimka228.messanger.services.ChatService;
import com.dimka228.messanger.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Controller
@AllArgsConstructor
public class MessageController {
    private final SimpMessagingTemplate msgTemplate;
    private UserService userService;
    private ChatService chatService;
    @MessageMapping("/chat/{id}/send")
    //@SendTo("/topic/public")
    public MessageDTO sendMessage(@DestinationVariable Integer id, @Payload MessageDTO chatMessage) {
        User user = userService.getUser(chatMessage.getSenderId());
        Chat chat = chatService.getChat(id);
        UserInChat userInChat = chatService.getUserInChat(user.getId(),chat.getId());

        Message added = chatService.addMessage(user,chat,chatMessage.getMessage());
        MessageDTO fullMsg = new MessageDTO(added.getId(),added.getData(),user.getId(),user.getLogin());
        msgTemplate.convertAndSend("/topic/chat/"+id+"/messages", fullMsg);
        return chatMessage;
    }


    @MessageMapping("chat/{id}/messages")
    @ResponseBody
    List<MessageInfo> messages(@DestinationVariable Integer id) {
        Chat chat = chatService.getChat(id);
        List<MessageInfo> messages = chatService.getMessagesFromChat(chat);
        return messages;
    }
    /*@MessageMapping("/chat/.addUser")
    @SendTo("/topic/public")
    public MessageInfo addUser(@Payload MessageInfo chatMessage,
                               SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }*/


    @EventListener
    public void handleSessionConnectEvent(SessionConnectEvent event) {
        //при подключении высылаем список сообщений
        //UPD: сделал через thmyleaf
        User user = userService.getUser(event.getUser().getName());

        System.out.println("Session Connect Event");
    }

    @EventListener
    public void handleSessionDisconnectEvent(SessionDisconnectEvent event) {
        System.out.println("Session Disconnect Event");
    }


}