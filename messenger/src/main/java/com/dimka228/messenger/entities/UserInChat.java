package com.dimka228.messenger.entities;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(
    name = "m_user_in_chat",
    indexes = {@Index(name = "m_user_in_chat_user_id_idx", columnList = "user_id")})
@IdClass(UserInChat.ID.class)
public class UserInChat {
  public static class ID implements Serializable {
    private User user;
    private Chat chat;
  }

  @Id
  @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.MERGE)
  @JoinColumn(name = "user_id", nullable = false)
  // @OnDelete(action = OnDeleteAction.CASCADE)
  private User user;

  // https://stackoverflow.com/questions/7197181/jpa-unidirectional-many-to-one-and-cascading-delete
  @Id
  @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.MERGE)
  @JoinColumn(name = "chat_id", nullable = false)
  // @OnDelete(action = OnDeleteAction.CASCADE)
  private Chat chat;

  @Column(name = "role", nullable = false, length = 20)
  private String role;

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public Chat getChat() {
    return chat;
  }

  public void setChat(Chat chat) {
    this.chat = chat;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public void clear() {
    this.user = null;
    this.chat = null;
  }

  public static class Roles {
    public static final String CREATOR = "CREATOR";
    public static final String REGULAR = "REGULAR";
    public static final String ADMIN = "ADMIN";
  }
}
