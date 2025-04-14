package com.dimka228.messenger.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "m_user_role")
@Data
public class Role {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Integer id;

	@Column(name = "priority", nullable = false)
	private Integer priority;

	@Column(name = "name", nullable = false, length = 20)
	private String name;

	@Column(name = "description", nullable = false, length = 20)
	private String description;

	@Column(name = "send_message")
	private boolean sendMessage;

	@Column(name = "delete_message")
	private boolean deleteMessage;

	@Column(name = "add_user")
	private boolean addUser;

	@Column(name = "ban_user")
	private boolean banUser;

	@Column(name = "edit_chat")
	private boolean editChat;

}
