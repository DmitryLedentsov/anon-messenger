package com.dimka228.messenger.services;

import com.dimka228.messenger.entities.User;
import com.dimka228.messenger.entities.UserProfile;
import com.dimka228.messenger.entities.UserStatus;
import com.dimka228.messenger.exceptions.UserExistsException;
import com.dimka228.messenger.exceptions.UserNotFoundException;
import com.dimka228.messenger.repositories.UserProfileRepository;
import com.dimka228.messenger.repositories.UserRepository;
import com.dimka228.messenger.repositories.UserStatusRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
  private final UserRepository repository;
  private final BCryptPasswordEncoder passwordEncoder;
  private final UserStatusRepository statusRepository;
  private final UserProfileRepository profileRepository;

  public boolean checkUser(String login) {
    return repository.findByLogin(login).isPresent();
  }

  public boolean checkUser(Integer id) {
    return repository.findById(id).isPresent();
  }

  public List<User> allUsers() {
    return repository.findAll();
  }

  public User addUser(User newUser) {
    if (checkUser(newUser.getLogin())) throw new UserExistsException();
    return repository.save(newUser);
  }

  public User registerUser(User newUser) {
    if (checkUser(newUser.getLogin())) throw new UserExistsException();
    newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
    return repository.save(newUser);
  }

  public User getUser(String login) {
    return getUserByLogin(login);
  }

  public User getUserByLogin(String login) {
    try {
      return repository.findByLogin(login).orElseThrow(() -> new UserNotFoundException());
    } catch (EntityNotFoundException e) {
      throw new UserNotFoundException(login);
    }
  }

  public User getUser(Integer id) {
    try {
      return repository.findById(id).orElseThrow(() -> new UserNotFoundException());
    } catch (EntityNotFoundException e) {
      throw new UserNotFoundException();
    }
  }

  public void deleteUser(Integer id) {
    repository.deleteById(id);
  }

  public Set<UserStatus> getUserStatusList(User user) {
    return statusRepository.findAllByUserId(user.getId()).orElse(Collections.emptySet());
  }

  public boolean checkUserStatus(User user, String status) {
    return getUserStatusList(user).stream().anyMatch(s -> s.getName().equals(status));
  }

  public void addUserStatus(User u, String s) {
    if (statusRepository.existsByUserIdAndName(u.getId(), s)) return;
    UserStatus status = new UserStatus();
    status.setName(s);
    status.setUser(u);
    statusRepository.save(status);
  }

  public void removeUserStatus(User u, String s) {
    if (!statusRepository.existsByUserIdAndName(u.getId(), s)) return;
    statusRepository.deleteByUserIdAndName(u.getId(), s);
  }

  public UserProfile getUserProfile(User user) {
    return profileRepository.findByUserId(user.getId());
  }
}
