package com.dimka228.messenger.services;

import com.dimka228.messenger.dto.ChatDTO;
import com.dimka228.messenger.dto.MessageDTO;
import com.dimka228.messenger.dto.OperationDTO;
import com.dimka228.messenger.entities.User;

import lombok.AllArgsConstructor;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SocketMessagingService {
    private final SimpMessagingTemplate msgTemplate;

    public void sendMessageOperationToChat(Integer chatId, OperationDTO<MessageDTO> operationDTO) {
        msgTemplate.convertAndSend("/topic/chat/" + chatId + "/messages", operationDTO);
    }

    public void sendChatOperationToUser(Integer userId, OperationDTO<ChatDTO> operationDTO) {
        msgTemplate.convertAndSend("/topic/user/" + userId + "/chats", operationDTO);
    }

    public void notifyMembersThatChatWasCreated(List<User> users, ChatDTO chatDTO) {}
}
