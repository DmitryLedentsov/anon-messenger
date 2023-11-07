package com.dimka228.messanger.services;

import com.dimka228.messanger.dto.ChatDTO;
import com.dimka228.messanger.dto.MessageDTO;
import com.dimka228.messanger.entities.User;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SocketMessagingService {
    private final SimpMessagingTemplate msgTemplate;
    public void sendToChat(Integer id, MessageDTO msg){

    }

    public void notifyMembersThatChatWasCreated(List<User> users, ChatDTO chatDTO){

    }



}
