package com.dimka228.messanger.dto;

import com.dimka228.messanger.entities.User;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class UserDto {
    private String login;
    private String password;

    public User getUser(){
        User user = new User();
        user.setLogin(this.login);
        user.setPassword(this.password);
        return user;
    }
}
