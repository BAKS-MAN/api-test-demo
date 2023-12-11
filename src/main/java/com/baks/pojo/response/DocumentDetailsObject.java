package com.baks.pojo.response;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * POJO for APi response with Document Details.
 */
public class DocumentDetailsObject {

  boolean deleted;
  private String documentId;
  private String createdAt;
  private String updatedAt;
  private String createdBy;
  private String updatedBy;
  private String documentCategoryCode;
  private String documentTitle;
  private String lockStatus;
  private String fileName;
  private String contentType;
  private int pageCount;
  private int size;
  private String md5;
  private String crc32;
  private String downloadUrl;
  private String uploadedAt;
  private String expiresAt;
  private String scanStatus;
  private boolean available;
  private String unavailabilityReason;
  private Map<String, String> metadata;

  /**
   * Returns collection of document details mandatory fields.
   */
  public static Set<String> getDocumentDetailsMandatoryFields() {
    Field[] documentDetailsObjectFields = DocumentDetailsObject.class.getDeclaredFields();
    Set<String> allFields = Arrays.stream(documentDetailsObjectFields).map(Field::getName).collect(
        Collectors.toSet());
    Set<String> optionalFields =
        Set.of("personId", "md5", "crc32", "expiresAt", "unavailabilityReason");
    allFields.removeAll(optionalFields);
    return allFields;
  }

  public Map<String, String> getMetadata() {
    return metadata;
  }

  public String getDocumentId() {
    return documentId;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public String getUpdatedAt() {
    return updatedAt;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public String getUpdatedBy() {
    return updatedBy;
  }

  public String getDocumentCategoryCode() {
    return documentCategoryCode;
  }

  public String getDocumentTitle() {
    return documentTitle;
  }

  public String getLockStatus() {
    return lockStatus;
  }

  public String getFileName() {
    return fileName;
  }

  public String getContentType() {
    return contentType;
  }

  public int getPageCount() {
    return pageCount;
  }

  public String getMd5() {
    return md5;
  }

  public String getCrc32() {
    return crc32;
  }

  public boolean isDeleted() {
    return deleted;
  }

  public String getDownloadUrl() {
    return downloadUrl;
  }

  public String getUploadedAt() {
    return uploadedAt;
  }

  public String getExpiresAt() {
    return expiresAt;
  }

  public int getSize() {
    return size;
  }

  public String getScanStatus() {
    return scanStatus;
  }

  public boolean isAvailable() {
    return available;
  }

  public String getUnavailabilityReason() {
    return unavailabilityReason;
  }
}
