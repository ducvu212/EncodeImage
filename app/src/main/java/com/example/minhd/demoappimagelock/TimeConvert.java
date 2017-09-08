package com.example.minhd.demoappimagelock;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimeConvert
{
  public static String convertMilisecondsToDate(String paramString)
  {
    SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("MMM dd,yyyy");
    Calendar localCalendar = Calendar.getInstance();
    localCalendar.setTimeInMillis(Long.parseLong(paramString));
    return localSimpleDateFormat.format(localCalendar.getTime());
  }

  public static String currentTime()
  {
    return String.valueOf(System.currentTimeMillis());
  }
}