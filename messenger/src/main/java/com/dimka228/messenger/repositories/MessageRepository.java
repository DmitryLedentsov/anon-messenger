package com.dimka228.messenger.repositories;

import com.dimka228.messenger.entities.Message;
import com.dimka228.messenger.models.MessageInfo;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {

    @Cacheable("getMessagesForUserInChat")
    @Query(
            nativeQuery = true,
            value =
                    "select id, sender_id as senderId, sender, message, send_time as sendTime from"
                            + " get_messages_for_user_in_chat(:_user_id, :_chat_id)")
    List<MessageInfo> getMessagesForUserInChat(
            @Param("_user_id") Integer userId, @Param("_chat_id") Integer chatId);

    @Cacheable("getMessagesFromChat")
    @Query(
            nativeQuery = true,
            value =
                    "select id, sender_id as senderId, sender, message, send_time as sendTime from"
                            + " get_messages_from_chat(:_chat_id)")
    List<MessageInfo> getMessagesFromChat(@Param("_chat_id") Integer chatId);

    @Caching(
            evict = {
                @CacheEvict(cacheNames = "getMessagesForUserInChat", allEntries = true),
                @CacheEvict(cacheNames = "getMessagesFromChat", allEntries = true),
                @CacheEvict(cacheNames = "findById", key = "#id")
            })
    @Transactional
    @Override
    void deleteById(Integer id);

    @Cacheable(value = "findById", key = "#id")
    @Override
    Optional<Message> findById(Integer id);

    @Caching(
            evict = {
                @CacheEvict(cacheNames = "getMessagesForUserInChat", allEntries = true),
                @CacheEvict(cacheNames = "getMessagesFromChat", allEntries = true),
                @CacheEvict(cacheNames = "findById", key = "#object.getId()")
            })
    @Override
    <S extends Message> S save(S object);
}
