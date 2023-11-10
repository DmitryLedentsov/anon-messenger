package com.dimka228.messanger.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class UserProfileDTO {
    String login;
    Integer rating;
    List<String> statusList;
    String registrationTime;
}
