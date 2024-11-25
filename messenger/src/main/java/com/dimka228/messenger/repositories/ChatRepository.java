package com.dimka228.messenger.repositories;

import com.dimka228.messenger.entities.Chat;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Integer> {
  @Query(nativeQuery = true, value = "select id, name from get_chats_for_user(:_user_id)")
  List<Chat> getChatsForUser(@Param("_user_id") Integer id);

  Optional<Chat> findById(Integer id);

  @Transactional
  void deleteById(Integer id);

  @Transactional
  @Modifying
  @Query(nativeQuery = true, value = "delete from m_chat  where id=?1")
  void deleteByIdCascading(Integer chatId);
}
