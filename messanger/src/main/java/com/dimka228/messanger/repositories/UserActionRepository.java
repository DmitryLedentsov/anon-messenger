package com.dimka228.messanger.repositories;

import com.dimka228.messanger.entities.UserAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserActionRepository extends JpaRepository<UserAction,Integer> {
    Optional<List<UserAction>> findAllByUserId(Integer id);
}
