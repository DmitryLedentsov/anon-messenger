package com.dimka228.messenger.repositories;

import com.dimka228.messenger.entities.UserStatus;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Repository
@Transactional
public interface UserStatusRepository extends JpaRepository<UserStatus, Integer> {

    @Cacheable(value = "findAllByUserId", key = "#id")
    Optional<Set<UserStatus>> findAllByUserId(Integer id);

    @Cacheable(value = "existsByUserIdAndName", key = "#id")
    boolean existsByUserIdAndName(Integer id, String name);

    @Caching(
            evict = {
                @CacheEvict(cacheNames = "findAllByUserId", key = "#id"),
                @CacheEvict(cacheNames = "existsByUserIdAndName", key = "#id")
            })
    @Transactional
    void deleteByUserIdAndName(Integer id, String s);

    @Caching(
            evict = {
                @CacheEvict(cacheNames = "findAllByUserId", key = "#object.getId()"),
                @CacheEvict(cacheNames = "existsByUserIdAndName", key = "#object.getId()")
            })
    @Override
    <S extends UserStatus> S save(S object);
}
