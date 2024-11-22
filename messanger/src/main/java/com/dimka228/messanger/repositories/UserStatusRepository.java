package com.dimka228.messanger.repositories;

import com.dimka228.messanger.entities.User;
import com.dimka228.messanger.entities.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@Transactional
public interface UserStatusRepository extends JpaRepository<UserStatus, Integer> {
    Optional<Set<UserStatus>> findAllByUserId(Integer id);
    @Transactional
    void deleteByUserIdAndName(Integer id,String s);
    boolean existsByUserIdAndName(Integer id, String name);
}
