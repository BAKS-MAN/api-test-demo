package com.baks.enums;

/**
 * ENUM with Metadata types.
 */
public enum MetadataType {
  STRING("STR", "STRING"),
  DATE("DATE", "DATE"),
  DATETIME("", "DATETIME"),
  DECIMAL("DEC", "DECIMAL"),
  INTEGER("INT", "INTEGER"),
  BOOLEAN("BOOL", "BOOLEAN");

  private final String value;
  private final String systemValue;

  MetadataType(String value, String systemValue) {
    this.value = value;
    this.systemValue = systemValue;
  }

  public String getValue() {
    return value;
  }

  public String getSystemValue() {
    return systemValue;
  }
}
