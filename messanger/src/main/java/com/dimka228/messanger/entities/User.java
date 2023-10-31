package com.dimka228.messanger.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "m_user", indexes = {
        @Index(name = "m_user_login_key", columnList = "login", unique = true)
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "password", nullable = false, length = 20)
    private String password;

    @Column(name = "login", nullable = false, length = 20)
    private String login;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}