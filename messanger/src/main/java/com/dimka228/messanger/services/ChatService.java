package com.dimka228.messanger.services;

import com.dimka228.messanger.entities.Chat;
import com.dimka228.messanger.entities.User;
import com.dimka228.messanger.repositories.ChatRepository;
import com.dimka228.messanger.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ChatService {
    private final ChatRepository repository;

    public List<Chat> getChatsForUser(User user){
        return repository.getChatsForUser(user.getId());
    }
}
