package com.baks.enums;

import static com.baks.constants.ApiRequestsConstants.DOCUMENT_CATEGORY_ID;
import static com.baks.constants.ApiRequestsConstants.METADATA_KEY;

/**
 * ENUM to store response error message models and their texts.
 */
public enum ResponseErrorModel {
  USER_ID_ERROR("", "Invalid or missing UserId"),
  NOT_FOUND("", "Not found"),
  INVALID_DOCUMENT_CATEGORY(DOCUMENT_CATEGORY_ID, "Unable to process request. Invalid categoryId."),
  INVALID_DOCUMENT_TITLE("documentTitle", "Unable to process request. Invalid document title."),
  FILE_LENGTH("fileLength", "The file length is a mandatory parameter. "
      + "Please provide the exact length of the file in bytes."),
  FILE_NAME("", "File name is of incorrect length or contains invalid characters"),
  DELETED_DOCUMENT_DELETE("deletedDocumentDelete", "Unable to delete a deleted document."),
  DELETED_DOCUMENT_DOWNLOAD("deletedDocumentDownload", "Unable to download a deleted document."),
  RECOVERED_DOCUMENT_RECOVER("recoveredDocumentRecover",
      "Unable to recover an undeleted document."),
  READ_ONLY_DOCUMENT("readOnlyDocument",
      "Unable to update the document as lock status is set to READ_ONLY."),
  INVALID_LOCK_TYPE("invalidLockType", "The lock type provided is invalid."),
  EMPTY_METADATA(METADATA_KEY, "Metadata is empty."),
  MANDATORY_METADATA_MISSING("mandatory", "Mandatory metadata key '%s' is missing."),
  METADATA_INVALID_TYPE(METADATA_KEY, "Provided metadata with the key '%s' "
      + "does not match the type defined in the document category metadata."),
  NOT_DEFINED_METADATA_KEY(METADATA_KEY,
      "Provided metadata key '%s' is not defined in the document category metadata"),
  EXPIRATION_DATE("expiry", "The expiry value should be provided as a future date."),
  NON_EXISTENT_DOCUMENT("nonExistentDocument", "Unable to find the requested document."),
  DOCUMENT_NOT_FOUND("", "Unable to find the requested document."),
  USER_NOT_REGISTERED("nonExistentUser", "Unable to find the requested user. "
      + "Please register user using the user registration endpoint."),
  INVALID_FILTER("invalidFilter", "Invalid value for search parameter."),
  INVALID_FILTER_FIELD("invalidFilter", "Unable to process field."),
  INVALID_FIELD_COMPARATOR("invalidFieldComparator",
      "The comparator is not supported for the field."),
  INVALID_REQUEST("", "The request couldn't be process as it has errors"),
  NOT_ACCEPTABLE("", "Server does not support requested types"),
  UNSUPPORTED_CONTENT_TYPE("", "Server does not support content type"),
  CONTENT_TYPE_HEADER_MISSED("", "Content-Type Header is required"),
  DOCUMENT_IDS_INVALID_ARRAY("jsonBodyInvalidArray",
      "The request body should be a JSON array of documentIds in the format "
          + "[\"id1\",\"id2\"]"),
  DOCUMENT_IDS_EMPTY_ARRAY("jsonBodyEmptyArray",
      "The request body should be a JSON array of documentIds in the format "
          + "[\"id1\",\"id2\"] and can't be empty.");


  private final String modelStateName;
  private final String message;

  ResponseErrorModel(String modelStateName, String message) {
    this.modelStateName = modelStateName;
    this.message = message;
  }

  public String getModelStateName() {
    return modelStateName;
  }

  public String getMessage() {
    return message;
  }
}
