DROP TABLE IF EXISTS M_MESSAGE, M_USER, M_USER_IN_CHAT, M_CHAT, M_USER_ACTION, M_USER_PROFILE, M_USER_ROLE, M_USER_STATUS, M_LOG CASCADE;

DROP FUNCTION IF EXISTS register, get_messages_for_user_in_chat, get_chats_for_user, register_action,
	add_message_action, add_chat_action, leave_chat_action, join_chat_action;