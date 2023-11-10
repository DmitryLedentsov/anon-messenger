package com.dimka228.messanger.repositories;

import com.dimka228.messanger.entities.User;
import com.dimka228.messanger.entities.UserInChat;
import com.dimka228.messanger.entities.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface UserInChatRepository extends JpaRepository<UserInChat, Integer> {
    Optional<UserInChat> findByUserIdAndChatId(Integer userId, Integer chatId);
    Optional<List<UserInChat>> findAllByChatId(Integer id);
}
