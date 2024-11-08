package com.dimka228.messanger.exceptions;

public class WronPasswordException  extends AppException{
    public WronPasswordException(){
        super("wrong password");
    }
}
