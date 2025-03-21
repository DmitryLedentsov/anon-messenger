package com.dimka228.messenger.repositories;

import com.dimka228.messenger.entities.UserProfile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Integer> {
    public UserProfile findByUserId(Integer id);
}
