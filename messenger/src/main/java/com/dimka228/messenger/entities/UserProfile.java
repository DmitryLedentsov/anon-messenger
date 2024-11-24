package com.dimka228.messenger.entities;

import jakarta.persistence.*;

@Entity
@Table(
    name = "m_user_profile",
    indexes = {@Index(name = "m_user_profile_user_id_key", columnList = "user_id", unique = true)})
public class UserProfile {
  @Id
  @Column(name = "user_id")
  private Integer userId;

  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @PrimaryKeyJoinColumn
  private User user;

  @Column(name = "rating")
  private Integer rating;

  public Integer getRating() {
    return rating;
  }

  public void setRating(Integer rating) {
    this.rating = rating;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }
}
