package com.dimka228.messenger.repositories;

import com.dimka228.messenger.entities.UserInChat;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserInChatRepository extends JpaRepository<UserInChat, Integer> {

    @Cacheable(value = "findByUserIdAndChatId", key = "#chatId")
    Optional<UserInChat> findByUserIdAndChatId(Integer userId, Integer chatId);

    @Cacheable(value = "findAllByChatId", key = "#id")
    Optional<List<UserInChat>> findAllByChatId(Integer id);

    @Caching(
            evict = {
                @CacheEvict(
                        cacheNames = "findByUserIdAndChatId",
                        key = "#object.getChat().getId()"),
                @CacheEvict(cacheNames = "findAllByChatId", key = "#object.getChat().getId()")
            })
    @Override
    <S extends UserInChat> S save(S object);
}
