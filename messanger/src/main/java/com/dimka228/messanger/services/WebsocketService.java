package com.dimka228.messanger.services;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class WebsocketService {
    private final SimpMessagingTemplate msgTemplate;
    
    
}
