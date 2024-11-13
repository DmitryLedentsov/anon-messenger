package com.dimka228.messanger.crypt;

import java.util.Objects;

import org.springframework.security.crypto.password.PasswordEncoder;

public class GostPasswordEncoder implements PasswordEncoder{

    private static Gost.Crypt gost = new Gost.Crypt();
    @Override
    public String encode(CharSequence rawPassword) {
      return gost.encryptText(rawPassword.toString());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
       
        return Objects.equals(rawPassword.toString(), gost.decryptText(encodedPassword));
    }
}