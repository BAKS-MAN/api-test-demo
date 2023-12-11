package com.baks.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.springframework.stereotype.Component;

/**
 * Step layer for update document expiration date actions.
 */
@Component
public class UpdateDocumentExpirationDateSteps extends BaseDocumentSteps {

  @Step("Send PUT request to update document expiration date")
  public Response updateDocumentExpirationDate(String documentId, String expirationDate) {
    return documentPlatformService.updateDocumentExpirationDateUsingCustomSpecification(documentId,
        expirationDate,
        documentPlatformService.getAuthorizedDpSpecification());
  }

  @Step("Send PUT request to update document expiration date using expired authorization token")
  public Response updateDocumentExpirationDateUsingExpiredAuthorizationToken(String documentId,
      String expirationDate) {
    return documentPlatformService.updateDocumentExpirationDateUsingCustomSpecification(documentId,
        expirationDate, documentPlatformService.getDpSpecificationWithExpiredToken());
  }

  @Step("Send PUT request to update document expiration date using invalid authorization token")
  public Response updateDocumentExpirationDateUsingInvalidAuthorizationToken(String documentId,
      String expirationDate) {
    return documentPlatformService.updateDocumentExpirationDateUsingCustomSpecification(documentId,
        expirationDate, documentPlatformService.getDpSpecificationWithInvalidAuthorizationToken());
  }

  @Step("Send PUT request to update document expiration date with empty authorization token value")
  public Response updateDocumentExpirationDateUsingEmptyAuthorizationToken(String documentId,
      String expirationDate) {
    return documentPlatformService.updateDocumentExpirationDateUsingCustomSpecification(documentId,
        expirationDate, documentPlatformService.getSpecificationWithEmptyAuthorizationToken());
  }

  @Step("Send PUT request to update document expiration date without authorization token")
  public Response updateDocumentExpirationDateWithoutAuthorizationToken(String documentId,
      String expirationDate) {
    return documentPlatformService.updateDocumentExpirationDateUsingCustomSpecification(documentId,
        expirationDate, documentPlatformService.getSpecificationWithoutAuthorizationToken());
  }

  @Step("Send PUT request to update document expiration date using invalid User Id")
  public Response updateDocumentExpirationDateUsingInvalidUserId(String documentId,
      String expirationDate) {
    return documentPlatformService.updateDocumentExpirationDateUsingCustomSpecification(documentId,
        expirationDate, documentPlatformService.getSpecificationWithInvalidUserId());
  }

  @Step("Send PUT request to update document expiration date using empty User Id value")
  public Response updateDocumentExpirationDateUsingEmptyUserId(String documentId,
      String expirationDate) {
    return documentPlatformService.updateDocumentExpirationDateUsingCustomSpecification(documentId,
        expirationDate, documentPlatformService.getSpecificationWithEmptyUserId());
  }

  @Step("Send PUT request to update document expiration date without User Id")
  public Response updateDocumentExpirationDateWithoutUserId(String documentId,
      String expirationDate) {
    return documentPlatformService.updateDocumentExpirationDateUsingCustomSpecification(documentId,
        expirationDate, documentPlatformService.getSpecificationWithoutUserId());
  }

  @Step("Get document creation date from document details")
  public String getDocumentCreationDate(String documentId) {
    return getDocumentDetailsObjectByDocumentId(documentId).getCreatedAt();
  }
}
