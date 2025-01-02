package com.dimka228.messenger.repositories;

import com.dimka228.messenger.entities.User;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Cacheable(value = "findByLogin", key = "#login")
    Optional<User> findByLogin(String login);

    @CacheEvict(cacheNames = "findByLogin", key = "#login")
    void deleteByLogin(String login);

    @Caching(evict = {@CacheEvict(cacheNames = "findByLogin", key = "#object.getUsername()")})
    @Override
    <S extends User> S save(S object);
}
