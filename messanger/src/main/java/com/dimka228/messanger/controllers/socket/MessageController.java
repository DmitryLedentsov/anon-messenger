package com.dimka228.messanger.controllers.socket;

import com.dimka228.messanger.dto.ErrorDTO;
import com.dimka228.messanger.dto.MessageDTO;
import com.dimka228.messanger.dto.OperationDTO;
import com.dimka228.messanger.entities.*;
import com.dimka228.messanger.exceptions.AppException;
import com.dimka228.messanger.exceptions.UserNotFoundException;
import com.dimka228.messanger.models.MessageInfo;
import com.dimka228.messanger.services.ChatService;
import com.dimka228.messanger.services.SocketMessagingService;
import com.dimka228.messanger.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("chat")
public class MessageController {
    private SocketMessagingService socketMessagingService;
    private UserService userService;
    private ChatService chatService;
    @PostMapping("/{id}/send")
    //@SendTo("/topic/public")
    public MessageDTO sendMessage(@PathVariable Integer id, @RequestBody MessageDTO chatMessage, Principal principal) {
        User user = userService.getUser(principal.getName());
        Chat chat = chatService.getChat(id);
        UserInChat userInChat = chatService.getUserInChat(user.getId(),chat.getId());

        Message added = chatService.addMessage(user,chat,chatMessage.getMessage());
        MessageDTO fullMsg = new MessageDTO(added.getId(),added.getData(),user.getId(),user.getLogin(),Instant.now().toString());
        OperationDTO<MessageDTO> data = new OperationDTO<>(fullMsg, OperationDTO.ADD);
        socketMessagingService.sendMessageOperationToChat(id, data);
        return chatMessage;
    }

    @DeleteMapping("/{id}/message/{msgId}")
    //@SendTo("/topic/public")
    public MessageDTO deleteMessage(@PathVariable Integer id, @PathVariable Integer msgId, Principal principal) {
        User user = userService.getUser(principal.getName());
        Chat chat = chatService.getChat(id);
        Message msg = chatService.getMessage(msgId);
        UserInChat userInChat = chatService.getUserInChat(user.getId(),chat.getId());
        chatService.deleteMessageFromUserInChat(user,chat, msg);

        MessageDTO fullMsg = new MessageDTO(msgId,"aa",user.getId(),user.getLogin(), Instant.now().toString());
        OperationDTO<MessageDTO> data = new OperationDTO<>(fullMsg,OperationDTO.DELETE);
        socketMessagingService.sendMessageOperationToChat(id, data);
        return fullMsg;
    }



    @GetMapping("/{id}/messages")
    
    List<MessageInfo> messages(@PathVariable Integer id,Principal principal) {
        Chat chat = chatService.getChat(id);
        User user = userService.getUser(principal.getName());
        List<MessageInfo> messages = chatService.getMessagesForUserInChat(user,chat);

        /*for (MessageInfo msg : messages) {
            MessageDTO fullMsg = new MessageDTO(msg.getId(),msg.getMessage(),msg.getSenderId(),msg.getSender(), msg.getSendTime());
            OperationDTO<MessageDTO> data = new OperationDTO<>(fullMsg,OperationDTO.ADD);
            msgTemplate.convertAndSend("/topic/chat/"+id+"/messages", data);
            msgTemplate.convertAndSend("/topic/user/"+user.getId()+"/chat/", data);
        }*/
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





}