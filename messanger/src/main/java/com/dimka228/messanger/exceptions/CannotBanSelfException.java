package com.dimka228.messanger.exceptions;

public class CannotBanSelfException  extends WrongPrivilegesException{
    public CannotBanSelfException(){
        super("unable to ban yourself");
    }
}
