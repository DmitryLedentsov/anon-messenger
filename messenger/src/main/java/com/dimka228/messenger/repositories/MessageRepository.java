package com.dimka228.messenger.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.dimka228.messenger.entities.Message;
import com.dimka228.messenger.models.MessageInfo;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {

	@Query(nativeQuery = true, value = "select id, sender_id as senderId, sender, message, send_time as sendTime from"
			+ " get_messages_for_user_in_chat(:_user_id, :_chat_id)")
	List<MessageInfo> getMessagesForUserInChat(@Param("_user_id") Integer userId, @Param("_chat_id") Integer chatId);

	@Query(nativeQuery = true, value = "select id, sender_id as senderId, sender, message, send_time as sendTime from"
			+ " get_messages_from_user_in_chat(:_user_id, :_chat_id)")
	List<MessageInfo> getMessagesFromUserInChat(@Param("_user_id") Integer userId, @Param("_chat_id") Integer chatId);

	@Query(nativeQuery = true, value = "select id, sender_id as senderId, sender, message, send_time as sendTime from"
			+ " get_messages_from_chat(:_chat_id)")
	List<MessageInfo> getMessagesFromChat(@Param("_chat_id") Integer chatId);
	

	@Transactional
	@Override
	void deleteById(Integer id);

	@Transactional
	@Modifying
	@Query(nativeQuery = true, value = "call delete_messages_from_user(:_user_id, :_chat_id)")
	void deleteAllMessages(@Param("_user_id") Integer userId, @Param("_chat_id") Integer chatId);
	@Override
	Optional<Message> findById(Integer id);

}
