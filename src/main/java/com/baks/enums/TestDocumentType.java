package com.baks.enums;

import com.baks.constants.ApiRequestsConstants;
import java.util.Arrays;
import org.apache.http.entity.ContentType;

/**
 * ENUM to store info about documents used in API requests.
 */
public enum TestDocumentType {
  PDF("PDF", ".pdf", ApiRequestsConstants.CONTENT_TYPE_PDF),
  WORD("WORD", ".docx", ApiRequestsConstants.CONTENT_TYPE_WORD_DOCUMENT),
  EXCEL("EXCEL", ".xlsx", ApiRequestsConstants.CONTENT_TYPE_EXCEL_DOCUMENT),
  JPEG("JPEG", ".jpg", ContentType.IMAGE_JPEG.getMimeType()),
  PNG("PNG", ".png", ContentType.IMAGE_PNG.getMimeType()),
  INFECTED("INFECTED", ".txt", ContentType.TEXT_PLAIN.getMimeType());

  private final String type;
  private final String extension;
  private final String requestHeaderContentType;

  TestDocumentType(String type, String extension, String requestHeaderContentType) {
    this.type = type;
    this.extension = extension;
    this.requestHeaderContentType = requestHeaderContentType;
  }

  /**
   * Get Document extension By document type.
   *
   * @param documentType type of document, stored in test data folder.
   * @return document extension i.e .pdf
   */
  public static String getDocumentExtensionByType(String documentType) {
    final String notFoundMessage = String.format("Document with type '%s' is not supported by test",
        documentType);
    return Arrays.stream(TestDocumentType.values())
        .filter(value -> value.getType().equalsIgnoreCase(documentType))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException(notFoundMessage)).getExtension();
  }

  public String getType() {
    return type;
  }

  public String getExtension() {
    return extension;
  }

  public String getRequestHeaderContentType() {
    return requestHeaderContentType;
  }
}
