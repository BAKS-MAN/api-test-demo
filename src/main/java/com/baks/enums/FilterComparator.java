package com.baks.enums;

/**
 * ENUM with Filter comparators.
 */
public enum FilterComparator {
  DATE_ON("ON"),
  DATE_BEFORE("BF"),
  DATE_ON_OR_BEFORE("OBF"),
  DATE_AFTER("AF"),
  DATE_ON_OR_AFTER("OAF"),
  EQUALS("EQ"),
  NOT_EQUALS("NE"),
  GREATER_THAN("GT"),
  GREATER_THAN_OR_EQUALS("GTE"),
  LESS_THAN("LT"),
  LESS_THAN_OR_EQUALS("LTE"),
  CONTAINS("CON"),
  DOES_NOT_CONTAIN("DCON"),
  STARTS_WITH("SW"),
  ENDS_WITH("EW"),
  IS("IS"),
  NOT("NOT");

  private final String value;

  FilterComparator(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
