package com.dimka228.messanger.repositories;

import com.dimka228.messanger.entities.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    @Query(nativeQuery = true, value = "select id, name from get_chats_for_user(:_user_id)")
    List<Chat> getChatsForUser(@Param("_user_id") Integer id);
    Optional<Chat> findById(Integer id);
    @Transactional
    void deleteById(Integer id);
}
