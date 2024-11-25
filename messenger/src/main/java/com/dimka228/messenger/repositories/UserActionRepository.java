package com.dimka228.messenger.repositories;

import com.dimka228.messenger.entities.UserAction;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserActionRepository extends JpaRepository<UserAction, Integer> {
  Optional<List<UserAction>> findAllByUserId(Integer id);

  Optional<UserAction> findFirstByUserIdAndNameOrderByTimeDesc(Integer id, String name);
}
