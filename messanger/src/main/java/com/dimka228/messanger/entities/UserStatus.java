package com.dimka228.messanger.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "m_user_status", indexes = {
        @Index(name = "m_user_status_user_id_idx", columnList = "user_id"),
        @Index(name = "m_user_status_name_key", columnList = "name", unique = true)
})
public class UserStatus {
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

    public static final String ONLINE =  "ONLINE";

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}