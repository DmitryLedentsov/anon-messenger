package com.dimka228.messenger.repositories;

import com.dimka228.messenger.entities.UserProfile;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Integer> {

    @Cacheable(value = "findByUserId", key = "#id")
    public UserProfile findByUserId(Integer id);

    @Caching(evict = {@CacheEvict(cacheNames = "findByUserId", key = "#object.getId()")})
    @Override
    <S extends UserProfile> S save(S object);
}
