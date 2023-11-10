package com.dimka228.messanger.controllers;

import com.dimka228.messanger.dto.UserProfileDTO;
import com.dimka228.messanger.entities.*;
import com.dimka228.messanger.models.MessageInfo;
import com.dimka228.messanger.services.UserService;
import com.dimka228.messanger.utils.DateConverter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Controller
@RequestMapping("/users")
public class UsersController {
    private final UserService userService;
    @GetMapping("/profile/{id}")
    public String chat(@PathVariable Integer id, Model model ){
        //TODO: aa
        /*user = userService.getUser("aboba");
        id = 1;*/

        User user = userService.getUser(id);
        UserProfile profile = userService.getUserProfile(user);
        List<String> userStatuses = userService.getUserStatusList(user).stream().map(s->s.getName()).collect(Collectors.toList());
        Instant registrationTime = userService.getLastUserActionTime(user,UserAction.REGISTER);
        UserProfileDTO profileDTO = new UserProfileDTO(user.getLogin(),profile.getRating(),userStatuses, DateConverter.format(registrationTime));

        model.addAttribute("profile", profileDTO);
        return "profile";
    }
}
