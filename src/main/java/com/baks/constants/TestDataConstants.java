package com.baks.constants;


import java.util.List;

/**
 * Test data Constants.
 */
public class TestDataConstants {

  private TestDataConstants() {
  }

  public static final String TEST_DOCUMENT_CATEGORY_CODE = "AQATEST";
  public static final String SYSTEM_DOCUMENT_TYPE_NAME = "AQA document";
  public static final String USER_ID_VALUE = "aa119c2e-6066-11ed-9b6a-0242ac120002";
  public static final String INVALID_USER_ID = "invalid-5106-test";
  public static final String DOCUMENT_TITLE_VALUE = "Test document";
  public static final String INVALID_DOCUMENT_ID = "c3be314f-22d5-4056-Test";
  public static final List<String> ILLEGAL_FILE_NAME_CHARACTERS =
      List.of(":", ">", "<", "/", "\\", "..", "*", "%", "$");
  public static final String ANTIVIRUS_SCAN_STATUS_OK = "OK";
  public static final String ANTIVIRUS_SCAN_STATUS_INFECTED = "INFECTED";
  public static final String ANTIVIRUS_SCAN_STATUS_IN_PROGRESS = "IN_PROGRESS";
  public static final String METADATA_STRING_VALUE = "IMPORTANT";
  public static final String METADATA_OTHER_STRING_VALUE = "BKS";
  public static final String TEST = "TEST";
}
