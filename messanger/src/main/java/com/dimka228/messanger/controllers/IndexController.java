package com.dimka228.messanger.controllers;

import com.dimka228.messanger.dto.ChatDTO;
import com.dimka228.messanger.dto.OperationDTO;
import com.dimka228.messanger.entities.Chat;
import com.dimka228.messanger.entities.Message;
import com.dimka228.messanger.entities.User;
import com.dimka228.messanger.entities.UserInChat;
import com.dimka228.messanger.exceptions.UserNotFoundException;
import com.dimka228.messanger.models.MessageInfo;
import com.dimka228.messanger.services.ChatService;
import com.dimka228.messanger.services.UserService;
import com.dimka228.messanger.utils.LoginParser;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpResponse;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Controller
@RequestMapping("/")
public class IndexController {
    private final ChatService chatService;
    private  final UserService userService;
    private final SimpMessagingTemplate msgTemplate;
    @GetMapping("/")
    public String index (Model model, Principal principal) {
        User user = userService.getUser(principal.getName());
        //TODO: remove getCurrUser
        //user = userService.getUser("aboba");
        model.addAttribute("user",user);
        List<ChatDTO> chats = chatService.getChatsForUser(user).stream().map(chat -> new ChatDTO(chat.getId(),chat.getName(),chatService.getUserRoleInChat(user,chat),chatService.getLastMessageFromChat(chat),null)).collect(Collectors.toList());
        model.addAttribute("chats", chats);
        return
                "index" ;
    }

    @GetMapping("/chat/{id}")
    public String chat(@PathVariable Integer id, Model model, Principal principal ){
        User user = userService.getUser(principal.getName());

        //TODO: aa
        /*user = userService.getUser("aboba");
        id = 1;*/

        model.addAttribute("user",user);

        Chat chat = chatService.getChat(id);
        UserInChat userInChat = chatService.getUserInChat(user.getId(),chat.getId());
        List<MessageInfo> messages = chatService.getMessagesForUserInChat(user, chat);
        model.addAttribute("messages", messages);
        model.addAttribute("chat", chat);
        return "chat";
    }

    @GetMapping("chat/create")
    public String createView (Model model, Principal principal) {
        User user = userService.getUser(principal.getName());
        //TODO: aa
        user = userService.getUser("aboba");
        model.addAttribute("user",user);
        return
                "chat-create" ;
    }

    @PostMapping("chat/create")
    public String createChat(@ModelAttribute("name") String name,@ModelAttribute("users") String userLoginsListString, Principal principal, Model model) {
        User user = userService.getUser(principal.getName());
        Chat chat = chatService.addChat(name);
        chatService.addUserInChat(user,chat, UserInChat.Roles.CREATOR);

        List<String> logins = LoginParser.parseLogins(userLoginsListString);
        logins = logins.stream().filter(userService::checkUser).collect(Collectors.toList());
        List<User> users = logins.stream().map(userService::getUser).collect(Collectors.toList());
        for(User cur: users){
            chatService.addUserInChat(cur,chat, UserInChat.Roles.REGULAR);
        }


        for(User cur: users){
            ChatDTO chatDTO = new ChatDTO(chat.getId(),chat.getName(),chatService.getUserRoleInChat(cur,chat),chatService.getLastMessageFromChat(chat),null);
            OperationDTO<ChatDTO> data = new OperationDTO<>(chatDTO,OperationDTO.ADD);
            msgTemplate.convertAndSend("/topic/user/"+cur.getId()+"/chats", data);
        }
        return "redirect:/chat/" + chat.getId().toString();
    }

    @PostMapping("chat/{id}/send")
    @ResponseBody
    public ResponseEntity<?> sendMsg(@ModelAttribute("text") String text, @PathVariable("id") Integer id,Principal principal, Model model) {
        User user = userService.getUser(principal.getName());
        Chat chat = chatService.getChat(id);
        UserInChat userInChat = chatService.getUserInChat(user.getId(),chat.getId());
        chatService.addMessage(user,chat,text);
        return new ResponseEntity<String>("send", HttpStatus.OK);
    }

    @GetMapping("chat/{id}/messages")
    @ResponseBody
    List<MessageInfo> messages(@PathVariable Integer id, Principal principal) {
        User user = userService.getUser(principal.getName());

        Chat chat = chatService.getChat(id);
        UserInChat userInChat = chatService.getUserInChat(user.getId(),chat.getId());
        List<MessageInfo> messages = chatService.getMessagesForUserInChat(user, chat);
        return messages;
    }


    //TODO: переименовать в ChatController /chat
    //переи




}