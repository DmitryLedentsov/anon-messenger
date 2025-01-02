package com.dimka228.messenger.services;

import com.dimka228.messenger.dto.ChatDTO;
import com.dimka228.messenger.entities.Chat;
import com.dimka228.messenger.entities.Message;
import com.dimka228.messenger.entities.User;
import com.dimka228.messenger.entities.UserInChat;
import com.dimka228.messenger.exceptions.ChatNotFoundException;
import com.dimka228.messenger.exceptions.MessageNotFoundException;
import com.dimka228.messenger.exceptions.MessageNotFromUserException;
import com.dimka228.messenger.exceptions.MessageNotInChat;
import com.dimka228.messenger.exceptions.UserNotInChatException;
import com.dimka228.messenger.models.MessageInfo;
import com.dimka228.messenger.repositories.ChatRepository;
import com.dimka228.messenger.repositories.MessageRepository;
import com.dimka228.messenger.repositories.UserInChatRepository;

import jakarta.persistence.EntityNotFoundException;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@AllArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final UserInChatRepository userInChatRepository;

    public List<Chat> getChatsForUser(User user) {
        return chatRepository.getChatsForUser(user.getId());
    }

    public List<MessageInfo> getMessagesForUserInChat(User user, Chat chat) {
        return messageRepository.getMessagesForUserInChat(user.getId(), chat.getId());
    }

    public List<MessageInfo> getMessagesFromChat(Chat chat) {
        return messageRepository.getMessagesFromChat(chat.getId());
    }

    public MessageInfo getLastMessageFromChat(Chat chat) {
        List<MessageInfo> msgs = getMessagesFromChat(chat);
        return msgs.isEmpty() ? null : msgs.get(msgs.size() - 1);
    }

    public List<ChatDTO> getChatListForUser(User usr) {
        return null; // TODO: chatRepository.get(chat.getId());
    }

    public Chat getChat(Integer id) {
        try {
            return chatRepository.findById(id).orElseThrow(() -> new ChatNotFoundException(id));
        } catch (EntityNotFoundException e) {
            throw new ChatNotFoundException(id);
        }
    }

    public Chat addChat(String name) {
        Chat chat = new Chat();
        chat.setName(name);
        return chatRepository.save(chat);
    }

    public Message getMessage(Integer id) {
        try {
            return messageRepository
                    .findById(id)
                    .orElseThrow(() -> new MessageNotFoundException(id));
        } catch (EntityNotFoundException e) {
            throw new MessageNotFoundException(id);
        }
    }

    public UserInChat getUserInChat(Integer userId, Integer chatId) {
        try {
            return userInChatRepository
                    .findByUserIdAndChatId(userId, chatId)
                    .orElseThrow(() -> new UserNotInChatException(userId, chatId));
        } catch (EntityNotFoundException e) {
            throw new UserNotInChatException(userId, chatId);
        }
    }

    public UserInChat getUserInChat(User user, Chat chat) {
        return getUserInChat(user.getId(), chat.getId());
    }

    public void addUserInChat(User user, Chat chat, String role) {
        UserInChat userInChat = new UserInChat();
        userInChat.setUser(user);
        userInChat.setChat(chat);
        userInChat.setRole(role);
        userInChatRepository.save(userInChat);
    }

    public Message addMessage(User sender, Chat chat, String text) {
        Message message = new Message();
        message.setChat(chat);
        message.setSender(sender);
        message.setData(text);
        return messageRepository.save(message);
    }

    private void deleteMessage(Integer id) {
        messageRepository.deleteById(id);
    }

    public void deleteMessageFromUserInChat(User user, Chat chat, Message msg) {
        if (!Objects.equals(user.getId(), msg.getSender().getId())) {
            throw new MessageNotFromUserException(msg.getId(), user.getId());
        }
        if (!Objects.equals(chat.getId(), msg.getChat().getId())) {
            throw new MessageNotInChat(msg.getId(), chat.getId());
        }
        deleteMessage(msg.getId());
    }

    public String getUserRoleInChat(User user, Chat chat) {
        return getUserInChat(user.getId(), chat.getId()).getRole();
    }

    public void deleteOrLeaveChat(User user, Chat chat) {
        UserInChat userInChat = getUserInChat(user.getId(), chat.getId());
        if (userInChat.getRole().equals(UserInChat.Roles.CREATOR)) {

            deleteChat(chat.getId());

        } else {
            userInChatRepository.delete(userInChat);
        }
    }

    public void deleteChat(Integer id) {
        chatRepository.deleteByIdCascading(id);
    }

    public void deleteUserFromChat(UserInChat userInChat) {
        userInChatRepository.delete(userInChat);
    }

    public List<UserInChat> getUsersInChat(Chat chat) {
        return userInChatRepository.findAllByChatId(chat.getId()).orElse(Collections.emptyList());
    }

    public Set<String> getAllRolesInChat(Chat chat) {
        Set<String> list = new HashSet<>();
        for (UserInChat userInChat : getUsersInChat(chat)) {
            list.add(userInChat.getRole());
        }
        return list;
    }
}
