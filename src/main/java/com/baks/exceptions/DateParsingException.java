package com.baks.exceptions;

/**
 * Custom exception for date parsing cases.
 */
public class DateParsingException extends RuntimeException {

  public DateParsingException(final String message) {
    super(message);
  }
}
