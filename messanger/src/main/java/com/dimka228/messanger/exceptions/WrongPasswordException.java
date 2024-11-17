package com.dimka228.messanger.exceptions;

public class WrongPasswordException  extends AppException{
    public WrongPasswordException(){
        super("wrong passwd");
    }
    public WrongPasswordException(String s){
        super(s);
    }
}
