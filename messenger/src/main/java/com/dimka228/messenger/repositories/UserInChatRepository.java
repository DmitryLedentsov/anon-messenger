package com.dimka228.messenger.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.dimka228.messenger.entities.UserInChat;

@Repository
public interface UserInChatRepository extends JpaRepository<UserInChat, Integer> {

	Optional<UserInChat> findByUserIdAndChatId(Integer userId, Integer chatId);

	Optional<List<UserInChat>> findAllByChatId(Integer id);

	@Transactional
	@Override
	void delete(UserInChat userInChat);

}
