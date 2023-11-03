package com.dimka228.messanger.repositories;

import com.dimka228.messanger.entities.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    @Query(nativeQuery = true, value = "select id, name from get_chats_for_user(1)")
    List<Chat> getChatsForUser(@Param("_user_id") Integer id);
}
