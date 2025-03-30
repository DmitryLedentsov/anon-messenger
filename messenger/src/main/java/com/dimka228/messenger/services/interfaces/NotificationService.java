package com.dimka228.messenger.services.interfaces;

import com.dimka228.messenger.dto.ChatDTO;
import com.dimka228.messenger.dto.MessageDTO;
import com.dimka228.messenger.dto.OperationDTO;

public interface NotificationService {

	public void sendMessageOperationToChat(Integer chatId, OperationDTO<MessageDTO> operationDTO);

	public void sendChatOperationToUser(Integer userId, OperationDTO<ChatDTO> operationDTO);

}
