package com.dimka228.messenger.repositories;

import com.dimka228.messenger.entities.UserStatus;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface UserStatusRepository extends JpaRepository<UserStatus, Integer> {
  Optional<Set<UserStatus>> findAllByUserId(Integer id);

  @Transactional
  void deleteByUserIdAndName(Integer id, String s);

  boolean existsByUserIdAndName(Integer id, String name);
}
