package com.dimka228.messanger.services;

import com.dimka228.messanger.entities.User;
import com.dimka228.messanger.entities.UserAction;
import com.dimka228.messanger.entities.UserProfile;
import com.dimka228.messanger.entities.UserStatus;
import com.dimka228.messanger.exceptions.ActionNotFoundException;
import com.dimka228.messanger.exceptions.UserExistsException;
import com.dimka228.messanger.exceptions.UserNotFoundException;
import com.dimka228.messanger.repositories.UserActionRepository;
import com.dimka228.messanger.repositories.UserProfileRepository;
import com.dimka228.messanger.repositories.UserRepository;
import com.dimka228.messanger.repositories.UserStatusRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository repository;
    private  final  BCryptPasswordEncoder passwordEncoder;
    private final UserStatusRepository statusRepository;
    private final UserActionRepository actionRepository;
    private final UserProfileRepository profileRepository;
    public  boolean checkUser(String login){
        return repository.findByLogin(login).isPresent();
    }
    public  boolean checkUser(Integer id){
        return repository.findById(id).isPresent();
    }


    public List<User> allUsers() {
        return repository.findAll();
    }
    public User addUser(User newUser) {
        if(checkUser(newUser.getLogin())) throw  new UserExistsException(newUser.getLogin());
        return repository.save(newUser);
    }
    public User registerUser(User newUser) {
        if(checkUser(newUser.getLogin())) throw  new UserExistsException(newUser.getLogin());
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        return repository.save(newUser);
    }

    public User getUser(String login) {
        try {
            return repository.findByLogin(login).orElseThrow(() -> new UserNotFoundException(login));
        }catch (EntityNotFoundException e){
            throw new UserNotFoundException(login);
        }
    }
    public User getUser(Integer id) {
        try {
            return repository.findById(id).orElseThrow(() -> new UserNotFoundException(id.toString()));
        }catch (EntityNotFoundException e){
            throw new UserNotFoundException(id.toString());
        }
    }
    public void deleteUser(Integer id) {
        repository.deleteById(id);
    }

    public List<UserStatus> getUserStatusList(User user){
        return statusRepository.findAllByUserId(user.getId()).orElse(Collections.emptyList());
    }
    public boolean checkUserStatus(User user, String status){
        return getUserStatusList(user).stream().anyMatch(s->s.getName().equals(status));
    }
    public void addUserStatus(User u, String s){
        if(statusRepository.existsByUserIdAndName(u.getId(),s)) return;
        UserStatus status = new UserStatus();
        status.setName(s);
        status.setUser(u);
        statusRepository.save(status);
    }
    public void removeUserStatus(User u, String s){
        if(!statusRepository.existsByUserIdAndName(u.getId(),s)) return;
        statusRepository.deleteByUserIdAndName(u.getId(),s);
    }


    public List<UserAction> getUserActionList(Integer id){
        if(!checkUser(id)) throw new UserNotFoundException(id.toString());
        return actionRepository.findAllByUserId(id).orElse(Collections.emptyList());
    }

    public UserProfile getUserProfile(User user){
        return profileRepository.findByUserId(user.getId());
    }

    public Instant getLastUserActionTime(User user, String name){
        try {
            return actionRepository.findFirstByUserIdAndNameOrderByTimeDesc(user.getId(),name).orElseThrow(() -> new ActionNotFoundException(name)).getTime();
        }catch (EntityNotFoundException e){
            throw new ActionNotFoundException(name);
        }
    }
}
