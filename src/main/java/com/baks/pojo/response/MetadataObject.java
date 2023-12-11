package com.baks.pojo.response;

import java.util.Objects;

/**
 * POJO for Metadata object.
 */
public class MetadataObject {

  private String key;
  private String name;
  private String type;
  private boolean required;

  @Override
  public boolean equals(Object object) {
    if (object instanceof MetadataObject) {
      MetadataObject metadataObject = (MetadataObject) object;
      return Objects.equals(this.key, metadataObject.key)
          && Objects.equals(this.name, metadataObject.name)
          && Objects.equals(this.type, metadataObject.type)
          && Objects.equals(this.required, metadataObject.required);
    }
    return false;
  }

  @Override
  public int hashCode() {
    if (key == null || name == null || type == null) {
      return 0;
    } else {
      return key.hashCode() * name.hashCode() * type.hashCode();
    }
  }

  /**
   * Returns metadata key value in lower case.
   */
  public String getKey() {
    if (key != null) {
      return key.toLowerCase();
    }
    return null;
  }

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }

  public boolean isRequired() {
    return required;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setRequired(boolean required) {
    this.required = required;
  }
}
