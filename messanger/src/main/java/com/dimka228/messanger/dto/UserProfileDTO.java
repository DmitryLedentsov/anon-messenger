package com.dimka228.messanger.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Data
public class UserProfileDTO {
    String login;
    String role;
    Integer id;
    Set<String> statuses;
    String registrationTime;
    
}
