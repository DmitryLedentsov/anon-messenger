package com.dimka228.messenger.security;

import com.dimka228.messenger.entities.User;
import com.dimka228.messenger.repositories.UserRepository;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserRepository userRepository;

	@Autowired
	public UserDetailsServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) {
		User user = userRepository.findByLogin(username)
			.orElseThrow(() -> new UsernameNotFoundException("User not found in DB"));
		log.debug("User {} found in DB", username);
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
				user.getAuthorities());
	}

}
