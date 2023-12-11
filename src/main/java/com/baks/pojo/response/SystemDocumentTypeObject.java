package com.baks.pojo.response;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * POJO for System Document Types objects.
 */
public class SystemDocumentTypeObject {

  private String sdtId;
  private String sdtName;
  private String defaultCategoryCode;
  private String countryCode;
  private int retentionPeriod;
  private List<MetadataObject> metadata;

  /**
   * Returns collection of Document Type fields.
   */
  public static Set<String> getDocumentTypeFields() {
    return Arrays.stream(SystemDocumentTypeObject.class.getDeclaredFields()).map(Field::getName)
        .collect(Collectors.toSet());
  }

  public String getSdtId() {
    return sdtId;
  }

  public String getSdtName() {
    return sdtName;
  }

  public String getCountryCode() {
    return countryCode;
  }

  public String getDefaultCategoryCode() {
    return defaultCategoryCode;
  }

  public int getRetentionPeriod() {
    return retentionPeriod;
  }

  public List<MetadataObject> getMetadata() {
    return metadata;
  }
}
