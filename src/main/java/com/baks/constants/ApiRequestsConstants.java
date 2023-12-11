package com.baks.constants;


/**
 * Constants for API requests.
 */
public class ApiRequestsConstants {

  private ApiRequestsConstants() {
  }

  public static final String BEARER_HEADER_VALUE = "Bearer %s";
  public static final String USERNAME = "username";
  public static final String PASSWORD = "password";
  public static final String GRANT_TYPE = "grant_type";
  public static final String SCOPE = "scope";
  public static final String DOCUMENT_ID = "documentId";
  public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
  public static final String USER_ID_HEADER = "X-Dm-User-Id";
  public static final String DOCUMENT_TITLE_HEADER = "X-Dm-Document-Title";
  public static final String CONTENT_TYPE_WORD_DOCUMENT =
      "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
  public static final String CONTENT_TYPE_EXCEL_DOCUMENT =
      "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
  public static final String CONTENT_TYPE_PDF = "application/pdf";
  public static final String CONTENT_TYPE_TEST = "application/test";

  public static final String DOCUMENT_CATEGORY_HEADER = "X-Dm-Document-Category";
  public static final String DOCUMENT_CATEGORIES_KEY = "documentCategories";
  public static final String DIGEST_HEADER = "Digest";
  public static final String LOCK_STATUS_HEADER = "X-Dm-Lock-Status";
  public static final String LOCK_STATUS_KEY = "lockStatus";
  public static final String LOCK_STATUS_ACTIVE_VALUE = "ACTIVE";
  public static final String LOCK_STATUS_READ_ONLY_VALUE = "READ_ONLY";
  public static final String CONTENT_DISPOSITION_HEADER = "Content-Disposition";
  public static final String CONTENT_DISPOSITION_VALUE_TEMPLATE = "attachment; filename=%s";
  public static final String EXPIRES_AT_HEADER = "X-Dm-Expires-At";
  public static final String EXPIRES_AT_KEY = "expiresAt";
  public static final String UPLOADED_AT_KEY = "uploadedAt";
  public static final String META_HEADER_TEMPLATE = "X-Dm-Meta-%s";
  public static final String METADATA_KEY = "metadata";
  public static final String PER_PAGE_PARAM = "per_page";
  public static final String PAGE_PARAM = "page";
  public static final String LIMIT_PARAM = "limit";
  public static final String OFFSET_PARAM = "offset";
  public static final String DOCUMENT_CATEGORIES_PATH = "documentCategories";
  public static final String DOCUMENT_CATEGORY_ID = "categoryId";
}
