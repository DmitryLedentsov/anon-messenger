package com.dimka228.messanger.exceptions;

public class AppException extends RuntimeException{
    public AppException(String chat){
        super(chat);
    }
    public AppException(){super();}
}
