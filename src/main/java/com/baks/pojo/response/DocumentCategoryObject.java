package com.baks.pojo.response;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * POJO for Document Categories API.
 */
public class DocumentCategoryObject {

  private String categoryId;
  private String categoryName;
  private String categoryCode;
  private String categoryDescription;
  private String sdtName;
  private String countryCode;
  private int retentionPeriod;
  private List<MetadataObject> metadata;
  private boolean deleted;

  public String getCategoryId() {
    return categoryId;
  }

  public String getCategoryName() {
    return categoryName;
  }

  public String getCategoryCode() {
    return categoryCode;
  }

  public String getSdtName() {
    return sdtName;
  }

  public String getCategoryDescription() {
    return categoryDescription;
  }

  public String getCountryCode() {
    return countryCode;
  }

  public int getRetentionPeriod() {
    return retentionPeriod;
  }

  public List<MetadataObject> getMetadata() {
    return metadata;
  }

  public boolean isDeleted() {
    return deleted;
  }
}
