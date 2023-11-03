package com.dimka228.messanger.repositories;

import com.dimka228.messanger.entities.User;
import com.dimka228.messanger.entities.UserInChat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserInChatRepository extends JpaRepository<UserInChat, Long> {
    Optional<UserInChat> findByUserIdAndChatId(Integer userId, Integer chatId);
}
