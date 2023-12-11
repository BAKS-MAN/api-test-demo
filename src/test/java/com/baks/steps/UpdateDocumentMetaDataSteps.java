package com.baks.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * Step layer for update document metadata actions.
 */
@Component
public class UpdateDocumentMetaDataSteps extends BaseDocumentSteps {

  @Step("Send PATCH request to update document Metadata")
  public Response updateDocumentMetadata(String documentId, Map<String, Object> metadata) {
    return documentPlatformService.updateDocumentMetadataUsingCustomSpecification(documentId,
        metadata, documentPlatformService.getAuthorizedDpSpecification());
  }

  @Step("Send PATCH request to update document Metadata without metadata header")
  public Response updateDocumentMetadataWithoutMetadataHeader(String documentId) {
    return documentPlatformService.updateDocumentMetadataWithoutMetadataHeader(documentId);
  }

  @Step("Send PUT request to replace document Metadata")
  public Response replaceDocumentMetadata(String documentId, Map<String, Object> metadata) {
    return documentPlatformService.replaceDocumentMetadataUsingCustomSpecification(documentId,
        metadata, documentPlatformService.getAuthorizedDpSpecification());
  }

  @Step("Send PUT request to replace document Metadata without metadata header")
  public Response replaceDocumentMetadataWithoutMetadataHeader(String documentId) {
    return documentPlatformService.replaceDocumentMetadataWithoutMetadataHeader(documentId);
  }

  @Step("Send PATCH request to update document Metadata using expired authorization token")
  public Response updateDocumentMetadataUsingExpiredAuthorizationToken(String documentId,
      Map<String, Object> metadata) {
    return documentPlatformService.updateDocumentMetadataUsingCustomSpecification(documentId,
        metadata, documentPlatformService.getDpSpecificationWithExpiredToken());
  }

  @Step("Send PATCH request to update document Metadata using invalid authorization token")
  public Response updateDocumentMetadataUsingInvalidAuthorizationToken(String documentId,
      Map<String, Object> metadata) {
    return documentPlatformService.updateDocumentMetadataUsingCustomSpecification(documentId,
        metadata, documentPlatformService.getDpSpecificationWithInvalidAuthorizationToken());
  }

  @Step("Send PATCH request to update document Metadata with empty authorization token value")
  public Response updateDocumentMetadataUsingEmptyAuthorizationToken(String documentId,
      Map<String, Object> metadata) {
    return documentPlatformService.updateDocumentMetadataUsingCustomSpecification(documentId,
        metadata, documentPlatformService.getSpecificationWithEmptyAuthorizationToken());
  }

  @Step("Send PATCH request to update document Metadata without authorization token")
  public Response updateDocumentMetadataWithoutAuthorizationToken(String documentId,
      Map<String, Object> metadata) {
    return documentPlatformService.updateDocumentMetadataUsingCustomSpecification(documentId,
        metadata, documentPlatformService.getSpecificationWithoutAuthorizationToken());
  }

  @Step("Send PATCH request to update document Metadata using invalid User Id")
  public Response updateDocumentMetadataUsingInvalidUserId(String documentId,
      Map<String, Object> metadata) {
    return documentPlatformService.updateDocumentMetadataUsingCustomSpecification(documentId,
        metadata, documentPlatformService.getSpecificationWithInvalidUserId());
  }

  @Step("Send PATCH request to update document Metadata using empty User Id value")
  public Response updateDocumentMetadataUsingEmptyUserId(String documentId,
      Map<String, Object> metadata) {
    return documentPlatformService.updateDocumentMetadataUsingCustomSpecification(documentId,
        metadata, documentPlatformService.getSpecificationWithEmptyUserId());
  }

  @Step("Send PATCH request to update document Metadata without User Id")
  public Response updateDocumentMetadataWithoutUserId(String documentId,
      Map<String, Object> metadata) {
    return documentPlatformService.updateDocumentMetadataUsingCustomSpecification(documentId,
        metadata, documentPlatformService.getSpecificationWithoutUserId());
  }
}
