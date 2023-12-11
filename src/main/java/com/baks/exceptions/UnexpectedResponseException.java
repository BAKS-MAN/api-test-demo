package com.baks.exceptions;

/**
 * Custom exception for cases when API Response or it's content does not meet the requirements. i.e.
 * response doesn't contain expected data.
 */
public class UnexpectedResponseException extends RuntimeException {

  public UnexpectedResponseException(final String message) {
    super(message);
  }
}
