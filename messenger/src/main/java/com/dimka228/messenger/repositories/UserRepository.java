package com.dimka228.messenger.repositories;

import com.dimka228.messenger.entities.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
  Optional<User> findByLogin(String login);

  void deleteByLogin(String login);
  // @Procedure register(@Param("_login") login , @Param("_password") password)
}
