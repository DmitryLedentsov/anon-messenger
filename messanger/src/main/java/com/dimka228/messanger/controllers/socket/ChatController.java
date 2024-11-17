package com.dimka228.messanger.controllers.socket;

import com.dimka228.messanger.dto.ChatDTO;
import com.dimka228.messanger.dto.ChatDtoRequest;
import com.dimka228.messanger.dto.MessageDTO;
import com.dimka228.messanger.dto.OperationDTO;
import com.dimka228.messanger.entities.*;
import com.dimka228.messanger.exceptions.CannotBanSelfException;
import com.dimka228.messanger.exceptions.UserNotFoundException;
import com.dimka228.messanger.exceptions.WrongPasswordException;
import com.dimka228.messanger.exceptions.WrongPrivilegesException;
import com.dimka228.messanger.models.MessageInfo;
import com.dimka228.messanger.services.ChatService;
import com.dimka228.messanger.services.SocketMessagingService;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
public class ChatController {
    private SocketMessagingService socketMessagingService;
    private UserService userService;
    private ChatService chatService;
    @PostMapping("/chat")
    //@SendTo("/topic/public")
    public ChatDTO sendChat(@RequestBody ChatDtoRequest chatDtoRequest, Principal principal) {
        User user = userService.getUser(principal.getName());
        Chat chat = chatService.addChat(chatDtoRequest.getName());
        chatService.addUserInChat(user,chat, UserInChat.Roles.CREATOR);

        List<String> logins = chatDtoRequest.getUsers();
        logins = logins.stream().filter(userService::checkUser).collect(Collectors.toList());
        List<User> users = logins.stream().map(userService::getUser).collect(Collectors.toList());
        for(User cur: users){
            chatService.addUserInChat(cur,chat, UserInChat.Roles.REGULAR);
        }

        List<UserInChat> usersInChat = chatService.getUsersInChat(chat);
        System.out.println(usersInChat.get(0).getUser().getId());
       
        chatDtoRequest.getUsers().add(user.getLogin());//добавляем нашего
       
        for(UserInChat cur: usersInChat){
            ChatDTO chatDTO = new ChatDTO(chat.getId(),chat.getName(),cur.getRole(),null,chatDtoRequest.getUsers());
            OperationDTO<ChatDTO> data = new OperationDTO<>(chatDTO,OperationDTO.ADD);
            socketMessagingService.sendChatOperationToUser(cur.getUser().getId(), data);
        }
        
        //return "redirect:/chat/" + chat.getId().toString();
        //msgTemplate.convertAndSend("/topic/user/"+id+"chats", data);
        return new ChatDTO(chat.getId(),chat.getName(),chatService.getUserRoleInChat(user, chat),null,chatDtoRequest.getUsers());
    }

    @DeleteMapping("/chat/{chatId}")
    //@SendTo("/topic/public")
    public ChatDTO deleteChat( @PathVariable Integer chatId,  Principal principal) {
        User user = userService.getUser(principal.getName());
        Chat chat = chatService.getChat(chatId);
        //return "redirect:/chat/" + chat.getId().toString();
        ChatDTO chatDTO = new ChatDTO(chatId,chat.getName(),null,null,null);
        OperationDTO<ChatDTO> data = new OperationDTO<>(chatDTO,OperationDTO.DELETE);
        if(chatService.getUserRoleInChat(user,chat).equals(UserInChat.Roles.CREATOR)){

            List<UserInChat> users = chatService.getUsersInChat(chat);
            System.out.println(users.get(0).getUser().getId());
            chatService.deleteOrLeaveChat(user,chat);
            for(UserInChat cur: users){
                socketMessagingService.sendChatOperationToUser(cur.getUser().getId(), data);
            }
        }
        else{
            chatService.deleteOrLeaveChat(user,chat);
            socketMessagingService.sendChatOperationToUser(user.getId(), data);
        }

        return chatDTO;
    }


    @GetMapping("/chats")
    List<Chat> messages( Principal principal) {
        User user = userService.getUser(principal.getName());
        List<Chat> chats = chatService.getChatsForUser(user);

        return chats;
    }

    @DeleteMapping("/chat/{chatId}/ban/{userId}")
    public void banUser( @PathVariable Integer chatId,@PathVariable Integer userId,  Principal principal){
        User cur = userService.getUser(principal.getName());
        Chat chat = chatService.getChat(chatId);
        User user = userService.getUser(userId);
        UserInChat userInChat = chatService.getUserInChat(cur,chat);

        if(!userInChat.getRole().equals(UserInChat.Roles.CREATOR)) throw new WrongPrivilegesException();
        if(user.getId()==cur.getId()) throw new CannotBanSelfException();
        List<MessageInfo> messages = chatService.getMessagesForUserInChat(user,chat);

        socketMessagingService.sendChatOperationToUser(userId, new OperationDTO<ChatDTO>(
            new ChatDTO(chatId, null, null, null, null), OperationDTO.DELETE));
        chatService.deleteOrLeaveChat(user,chat);
        for (MessageInfo messageInfo : messages) {
            MessageDTO data = new MessageDTO(messageInfo.getId(), messageInfo.getMessage(), userId, user.getLogin(), null);
            OperationDTO<MessageDTO> op = new OperationDTO<MessageDTO>(data, OperationDTO.DELETE);
            socketMessagingService.sendMessageOperationToChat(chatId,op);

        }
       
        
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