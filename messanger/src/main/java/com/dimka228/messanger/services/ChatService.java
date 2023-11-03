package com.dimka228.messanger.services;

import com.dimka228.messanger.entities.Chat;
import com.dimka228.messanger.entities.User;
import com.dimka228.messanger.entities.UserInChat;
import com.dimka228.messanger.exceptions.ChatNotFoundException;
import com.dimka228.messanger.exceptions.UserNotInChatException;
import com.dimka228.messanger.models.MessageInfo;
import com.dimka228.messanger.repositories.ChatRepository;
import com.dimka228.messanger.repositories.MessageRepository;
import com.dimka228.messanger.repositories.UserInChatRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final UserInChatRepository userInChatRepository;
    public List<Chat> getChatsForUser(User user){
        return chatRepository.getChatsForUser(user.getId());
    }

    public  List<MessageInfo> getMessagesForUserInChat(User user, Chat chat){
        return messageRepository.getMessagesForUserInChat(user.getId(),chat.getId());
    }

    public Chat getChat(Integer id){
        try {
            return chatRepository.findById(id).orElseThrow(() -> new ChatNotFoundException(id));
        }catch (EntityNotFoundException e){
            throw new ChatNotFoundException(id);
        }
    }

    public  Chat addChat(String name){
        Chat chat = new Chat();
        chat.setName(name);
        return chatRepository.save(chat);
    }

    public UserInChat getUserInChat(Integer userId, Integer chatId){
        try {
            return userInChatRepository.findByUserIdAndChatId(userId,chatId).orElseThrow(() -> new UserNotInChatException(chatId, userId));
        }catch (EntityNotFoundException e){
            throw new UserNotInChatException(chatId, userId);
        }
    }

    public void addUserInChat(User user, Chat chat, String role) {
        UserInChat userInChat = new UserInChat();
        userInChat.setUser(user);
        userInChat.setChat(chat);
        userInChat.setRole("REGULAR");
        userInChatRepository.save(userInChat);
    }
}
