package com.baks.exceptions;

/**
 * Custom exception for cases when test file is not found.
 */
public class TestDataFileNotFoundException extends RuntimeException {

  public TestDataFileNotFoundException(final String message) {
    super(message);
  }
}
