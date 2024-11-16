package com.dimka228.messanger.exceptions;

public class WrongPrivilegesException extends AppException {
    public WrongPrivilegesException(){
        super("not enough privileges");
    }
    public WrongPrivilegesException(String s){
        super(s);
    }
}
