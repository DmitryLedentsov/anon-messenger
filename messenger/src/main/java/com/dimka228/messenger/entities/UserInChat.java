package com.dimka228.messenger.entities;

import java.io.Serializable;
import java.time.Instant;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "m_user_in_chat", indexes = { @Index(name = "m_user_in_chat_user_id_idx", columnList = "user_id") })
@IdClass(UserInChat.ID.class)
@Data
public class UserInChat {

	@EqualsAndHashCode
	public static class ID implements Serializable {

		private User user;

		private Chat chat;

	}

	@Id
	@ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.MERGE)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Id
	@ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.MERGE)
	@JoinColumn(name = "chat_id", nullable = false)
	private Chat chat;

	@ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.MERGE)
	@JoinColumn(name = "role_id", nullable = false)
	private Role role;


	@Column(name = "join_time")
	private Instant joinTime;

	public static class Roles {

		public static final String CREATOR = "CREATOR";

		public static final String REGULAR = "REGULAR";

		public static final String ADMIN = "ADMIN";

	}

}
