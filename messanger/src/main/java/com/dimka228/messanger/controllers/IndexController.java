package com.dimka228.messanger.controllers;

import com.dimka228.messanger.dto.ChatDTO;
import com.dimka228.messanger.entities.Chat;
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
        List<Chat> chats = chatService.getChatsForUser(user);
        model.addAttribute("chats", chats);
        return
                "index" ;
    }

    @GetMapping("/chat/{id}")
    public String chat(@PathVariable Integer id, Model model ){
        User user = getCurrentUser();

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
    public String createView (Model model) {
        User user = getCurrentUser();
        //TODO: aa
        user = userService.getUser("aboba");
        model.addAttribute("user",user);
        return
                "chat-create" ;
    }

    @PostMapping("chat/create")
    public String createChat(@ModelAttribute("name") String name,@ModelAttribute("users") String userLoginsListString, Model model) {
        User user = getCurrentUser();
        Chat chat = chatService.addChat(name);
        chatService.addUserInChat(user,chat, UserInChat.Roles.CREATOR);

        List<String> logins = LoginParser.parseLogins(userLoginsListString);
        logins = logins.stream().filter(userService::checkUser).collect(Collectors.toList());
        List<User> users = logins.stream().map(userService::getUser).collect(Collectors.toList());
        for(User cur: users){
            chatService.addUserInChat(cur,chat, UserInChat.Roles.REGULAR);
        }

        ChatDTO chatDTO = new ChatDTO(user.getId(),chat.getName(),null,null);
        for(User cur: users){
            msgTemplate.convertAndSend("/topic/user/"+cur.getId()+"/chat/list", chatDTO);
        }
        return "redirect:/chat/" + chat.getId().toString();
    }

    @PostMapping("chat/{id}/send")
    @ResponseBody
    public ResponseEntity<?> sendMsg(@ModelAttribute("text") String text, @PathVariable("id") Integer id, Model model) {
        User user = getCurrentUser();
        Chat chat = chatService.getChat(id);
        UserInChat userInChat = chatService.getUserInChat(user.getId(),chat.getId());
        chatService.addMessage(user,chat,text);
        return new ResponseEntity<String>("send", HttpStatus.OK);
    }

    @GetMapping("chat/{id}/messages")
    @ResponseBody
    List<MessageInfo> messages(@PathVariable Integer id) {
        User user = getCurrentUser();

        Chat chat = chatService.getChat(id);
        UserInChat userInChat = chatService.getUserInChat(user.getId(),chat.getId());
        List<MessageInfo> messages = chatService.getMessagesForUserInChat(user, chat);
        return messages;
    }

    public User getCurrentUser(){
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUser(login);
        if(user == null){
            throw new UserNotFoundException(login);
        }
        return user;
    }




    //TODO: переименовать в ChatController /chat
    //переи




}