package com.baks.utils;

import com.baks.exceptions.DateParsingException;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Test data generation class.
 */
public final class DateUtil {

  private DateUtil() {
  }

  public static final String ZONED_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSX";
  public static final String ISO_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";
  public static final String DATE_WITHOUT_TIME_FORMAT = "yyyy-MM-dd";
  public static final String RFC_DATE_TIME_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";

  /**
   * Returns a copy of this LocalDateTime as a String in "yyyy-MM-dd" format with the specified
   * number of days added.
   *
   * @param numberOfDaysToPlus the days to add, may be negative
   * @return the formatted date-time string with the days added, not null
   */
  public static String getCurrentFormattedDatePlusDays(int numberOfDaysToPlus) {
    return LocalDateTime.now().plusDays(numberOfDaysToPlus).format(getDateFormatWithoutTime());
  }

  /**
   * Returns a copy of this LocalDateTime as a String with the specified number of months added.
   *
   * @param numberOfMonthsToPlus the months to add, may be negative
   * @return the formatted date-time string with the months added, not null
   */
  public static String getCurrentFormattedDatePlusMonths(int numberOfMonthsToPlus) {
    return LocalDateTime.now().plusMonths(numberOfMonthsToPlus).format(getDateFormatWithoutTime());
  }

  /**
   * Returns a copy of this LocalDateTime as a String with the specified number of days subtracted.
   *
   * @param daysToSubtract the days to subtract, may be negative
   * @return the formatted date-time string with the days subtracted, not null
   */
  public static String getCurrentFormattedDateMinusDays(int daysToSubtract) {
    return LocalDateTime.now().minusDays(daysToSubtract).format(getDateFormatWithoutTime());
  }

  /**
   * Returns current date as a String in format "yyyy-MM-dd".
   */
  public static String getCurrentFormattedDate() {
    return LocalDateTime.now().format(getDateFormatWithoutTime());
  }

  /**
   * Outputs this date-time as a String, such as '2022-12-03T10:15:30' with the specified number of
   * hours subtracted.
   *
   * @param hours the hours to subtract, may be negative
   * @return a string representation of this date-time with the hours subtracted, not null
   */
  public static String getCurrentIsoDateTimeMinusSpecifiedHours(int hours) {
    return LocalDateTime.now(ZoneOffset.UTC)
        .minusHours(hours)
        .format(getIsoDateFormat());
  }

  /**
   * Outputs this date-time as a String, such as '2022-12-03T10:15:30' with the specified number of
   * hours added.
   *
   * @param numberOfHoursToPlus the hours to add, may be negative
   * @return a string representation of this date-time with the hours added, not null
   */
  public static String getCurrentIsoDateTimePlusSpecifiedHours(int numberOfHoursToPlus) {
    return LocalDateTime.now().plusHours(numberOfHoursToPlus)
        .format(getIsoDateFormat());
  }

  /**
   * Outputs this date-time as a String, such as '2022-12-03T10:15:30' with the specified number of
   * days added.
   *
   * @param numberOfDaysToPlus the days to add, may be negative
   * @return a string representation of this date-time with the days added, not null
   */
  public static String getCurrentIsoDateTimePlusSpecifiedDays(int numberOfDaysToPlus) {
    return LocalDateTime.now().plusDays(numberOfDaysToPlus)
        .format(getIsoDateFormat());
  }

  /**
   * Formats string value of date into specified format: "yyyy-MM-dd".
   */
  public static String getFormattedDateFromString(String date) {
    try {
      long dateInMillis = new StdDateFormat().parse(date).getTime();
      return LocalDate.ofEpochDay(Duration.ofMillis(dateInMillis).toDays())
          .format(getDateFormatWithoutTime());
    } catch (ParseException e) {
      throw new DateParsingException(
          String.format("Cannot parse date '%s': not compatible with any of standard forms", date));
    }
  }

  private static DateTimeFormatter getDateFormatWithoutTime() {
    return DateTimeFormatter.ISO_LOCAL_DATE;
  }

  private static DateTimeFormatter getIsoDateFormat() {
    return DateTimeFormatter.ofPattern(ISO_DATE_TIME_FORMAT);
  }

  /**
   * Outputs this date-time as a String using the specified pattern with the specified number of
   * months added.
   *
   * @param numberOfMonthsToPlus the months to add, may be negative
   * @param datePattern          specified date pattern i.e "yyyy-MM-dd"
   * @return a string representation of this date-time with the months added, not null
   */
  public static String getCurrentDatePlusMonthsInSpecifiedFormat(int numberOfMonthsToPlus,
      String datePattern) {
    return ZonedDateTime.now().plusMonths(numberOfMonthsToPlus)
        .format(DateTimeFormatter.ofPattern(datePattern));
  }
}
