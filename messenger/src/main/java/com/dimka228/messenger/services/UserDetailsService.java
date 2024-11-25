package com.dimka228.messenger.services;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserDetailsService
    implements org.springframework.security.core.userdetails.UserDetailsService {

  private final UserService userService;

  @Override
  public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
    com.dimka228.messenger.entities.User user = userService.getUser(login);

    return User.withUsername(login)
        .password(user.getPassword())
        .roles("USER")
        .accountExpired(false)
        .accountLocked(false)
        .credentialsExpired(false)
        .disabled(false)
        // .authorities( List.of(new SimpleGrantedAuthority("ROLE_USER")))
        .build();
  }
}
