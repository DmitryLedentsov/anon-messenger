package com.dimka228.messenger.repositories;

import com.dimka228.messenger.entities.UserStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Repository
@Transactional
public interface UserStatusRepository extends JpaRepository<UserStatus, Integer> {

	Optional<Set<UserStatus>> findAllByUserId(Integer id);

	@Transactional
	@Modifying
	@Query(nativeQuery = true, value = "call add_user_status_unique(:_user_id, :_status)")
	void insertUnique(@Param("_user_id") Integer userId, @Param("_status") String _status);
	@Transactional
	void deleteByUserIdAndName(Integer id, String s);

	boolean existsByUserIdAndName(Integer id, String name);

}
