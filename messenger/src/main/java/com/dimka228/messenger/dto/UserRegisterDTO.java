package com.dimka228.messenger.dto;

import com.dimka228.messenger.entities.User;
import com.dimka228.messenger.validation.Password;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_DEFAULT)
public class UserRegisterDTO {
    private String login;

    @Password(message = "password must be string containing characters and numbers")
    private String password;

    public User getUser() {
        User user = new User();
        user.setLogin(this.login);
        user.setPassword(this.password);
        return user;
    }
}
