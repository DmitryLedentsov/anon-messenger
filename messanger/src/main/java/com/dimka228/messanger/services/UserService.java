package com.dimka228.messanger.services;

import com.dimka228.messanger.entities.User;
import com.dimka228.messanger.exceptions.UserExistsException;
import com.dimka228.messanger.exceptions.UserNotFoundException;
import com.dimka228.messanger.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
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
        return repository.findById(login).isPresent();
    }

    public List<User> allUsers() {
        return repository.findAll();
    }
    public User addUser(User newUser) {
        if(checkUser(newUser.getLogin())) throw  new UserExistsException(newUser.getLogin());
        return repository.save(newUser);
    }

    public User getUser(String login) {
        try {
            return repository.findById(login).orElseThrow(() -> new UserNotFoundException(login));
        }catch (EntityNotFoundException e){
            throw new UserNotFoundException(login);
        }
    }
    public void deleteUser(String login) {
        repository.deleteById(login);
    }
}
