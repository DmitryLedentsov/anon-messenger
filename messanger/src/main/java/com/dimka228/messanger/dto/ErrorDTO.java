package com.dimka228.messanger.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorDTO {
    String message;
    public ErrorDTO(Exception e){
        setMessage(e.getMessage());
    }
}
