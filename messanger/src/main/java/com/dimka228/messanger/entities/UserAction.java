package com.dimka228.messanger.entities;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "m_user_action", indexes = {
        @Index(name = "m_user_action_user_id_idx", columnList = "user_id")
})
public class UserAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @Column(name = "description", length = 20)
    private String description;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "\"time\"")
    private Instant time;

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static String MESSAGE_SENT = "message sent";
    public static String REGISTER = "user registered";
}