package com.dimka228.messenger.utils;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class DateConverter {
  private static final SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

  public static String format(Instant instant) {
    Date date = Date.from(instant);
    return formatter.format(date);
  }
}
