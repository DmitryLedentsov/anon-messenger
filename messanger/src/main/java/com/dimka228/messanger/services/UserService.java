package com.dimka228.messanger.services;

import com.dimka228.messanger.entities.User;
import com.dimka228.messanger.exceptions.UserExistsException;
import com.dimka228.messanger.exceptions.UserNotFoundException;
import com.dimka228.messanger.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository repository;
    private  final  BCryptPasswordEncoder passwordEncoder;
    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder encoder) {
        this.repository = userRepository;
        this.passwordEncoder = encoder;
    }

    public  boolean checkUser(String login){
        return repository.findByLogin(login).isPresent();
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
}
