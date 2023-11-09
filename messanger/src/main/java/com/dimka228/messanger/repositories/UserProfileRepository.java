package com.dimka228.messanger.repositories;

import com.dimka228.messanger.entities.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile,Long> {
    public UserProfile findByUserId(Integer id);
}
