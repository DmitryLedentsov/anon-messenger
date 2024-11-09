package com.dimka228.messanger.controllers.socket;

import com.dimka228.messanger.dto.ChatDTO;
import com.dimka228.messanger.dto.MessageDTO;
import com.dimka228.messanger.dto.OperationDTO;
import com.dimka228.messanger.entities.*;
import com.dimka228.messanger.exceptions.UserNotFoundException;
import com.dimka228.messanger.models.MessageInfo;
import com.dimka228.messanger.services.ChatService;
import com.dimka228.messanger.services.UserService;
import com.dimka228.messanger.utils.LoginParser;
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
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor
public class ChatController {
    private final SimpMessagingTemplate msgTemplate;
    private UserService userService;
    private ChatService chatService;
    @MessageMapping("/user/{id}/chat/create")
    //@SendTo("/topic/public")
    public ChatDTO sendChat(@DestinationVariable Integer id, @Payload ChatDTO chatDTO) {
        User user = userService.getUser(id);
        Chat chat = chatService.addChat(chatDTO.getName());
        chatService.addUserInChat(user,chat, UserInChat.Roles.CREATOR);

        List<String> logins = chatDTO.getUsers();
        logins = logins.stream().filter(userService::checkUser).collect(Collectors.toList());
        List<User> users = logins.stream().map(userService::getUser).collect(Collectors.toList());
        for(User cur: users){
            chatService.addUserInChat(cur,chat, UserInChat.Roles.CREATOR);
        }

        List<UserInChat> usersInChat = chatService.getUsersInChat(chat);
        System.out.println(usersInChat.get(0).getUser().getId());
       
        chatDTO.getUsers().add(user.getLogin());//добавляем нашего
        chatDTO = new ChatDTO(chat.getId(),chat.getName(),null,null,chatDTO.getUsers());
        OperationDTO<ChatDTO> data = new OperationDTO<>(chatDTO,OperationDTO.ADD);
        for(UserInChat cur: usersInChat){
            msgTemplate.convertAndSend("/topic/user/"+cur.getUser().getId()+"/chats", data);
        }
        
        //return "redirect:/chat/" + chat.getId().toString();
        //msgTemplate.convertAndSend("/topic/user/"+id+"chats", data);
        return chatDTO;
    }

    @MessageMapping("/user/{id}/chat/delete/{chatId}")
    //@SendTo("/topic/public")
    public ChatDTO deleteChat(@DestinationVariable Integer id, @DestinationVariable Integer chatId) {
        User user = userService.getUser(id);
        Chat chat = chatService.getChat(chatId);
        //return "redirect:/chat/" + chat.getId().toString();
        ChatDTO chatDTO = new ChatDTO(chatId,chat.getName(),null,null,null);
        OperationDTO<ChatDTO> data = new OperationDTO<>(chatDTO,OperationDTO.DELETE);
        if(chatService.getUserRoleInChat(user,chat).equals(UserInChat.Roles.CREATOR)){

            List<UserInChat> users = chatService.getUsersInChat(chat);
            System.out.println(users.get(0).getUser().getId());
            chatService.deleteOrLeaveChat(user,chat);
            for(UserInChat cur: users){
                msgTemplate.convertAndSend("/topic/user/"+cur.getUser().getId()+"/chats", data);
            }
        }
        else{
            chatService.deleteOrLeaveChat(user,chat);
            msgTemplate.convertAndSend("/topic/user/"+id+"/chats", data);
        }

        return chatDTO;
    }


    @MessageMapping("user/{id}/chats")
    @ResponseBody
    List<Chat> messages(@DestinationVariable Integer id) {
        User user = userService.getUser(id);
        List<Chat> chats = chatService.getChatsForUser(user);
        return chats;
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