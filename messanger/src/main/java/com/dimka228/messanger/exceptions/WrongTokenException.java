package com.dimka228.messanger.exceptions;

public class WrongTokenException extends AppException{
    public WrongTokenException(){
        super("wrong token");
    }
    public WrongTokenException(String msg){
        super(msg);
    }
}