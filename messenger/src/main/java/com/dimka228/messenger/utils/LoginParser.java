package com.dimka228.messenger.utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LoginParser {
  public static List<String> parseLogins(String s) {
    return Arrays.stream(s.trim().split(",")).distinct().collect(Collectors.toList());
  }
}
