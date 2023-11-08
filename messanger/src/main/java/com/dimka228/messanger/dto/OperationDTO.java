package com.dimka228.messanger.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class OperationDTO <T>{
    T data;
    String operation;

    public static String ADD = "ADD";
    public static String DELETE = "DELETE";
    public  static String UPDATE = "UPDATE";
}
