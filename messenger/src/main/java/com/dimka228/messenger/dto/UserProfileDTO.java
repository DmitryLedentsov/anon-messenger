package com.dimka228.messenger.dto;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UserProfileDTO {
  String login;
  String role;
  Integer id;
  Set<String> statuses;
  String registrationTime;
}
