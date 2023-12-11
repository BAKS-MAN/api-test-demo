package com.baks.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.springframework.stereotype.Component;

/**
 * Step layer for update document lock status actions.
 */
@Component
public class UpdateDocumentLockStatusSteps extends BaseDocumentSteps {

  @Step("Send PUT request to update document lock status")
  public Response updateDocumentLockStatus(String documentId, String lockStatus) {
    return documentPlatformService.updateDocumentLockStatusUsingCustomSpecification(documentId,
        lockStatus,
        documentPlatformService.getAuthorizedDpSpecification());
  }

  @Step("Send PUT request to update document lock status using expired authorization token")
  public Response updateDocumentLockStatusUsingExpiredAuthorizationToken(String documentId,
      String lockStatus) {
    return documentPlatformService.updateDocumentLockStatusUsingCustomSpecification(documentId,
        lockStatus, documentPlatformService.getDpSpecificationWithExpiredToken());
  }

  @Step("Send PUT request to update document lock status using invalid authorization token")
  public Response updateDocumentLockStatusUsingInvalidAuthorizationToken(String documentId,
      String lockStatus) {
    return documentPlatformService.updateDocumentLockStatusUsingCustomSpecification(documentId,
        lockStatus, documentPlatformService.getDpSpecificationWithInvalidAuthorizationToken());
  }

  @Step("Send PUT request to update document lock status with empty authorization token value")
  public Response updateDocumentLockStatusUsingEmptyAuthorizationToken(String documentId,
      String lockStatus) {
    return documentPlatformService.updateDocumentLockStatusUsingCustomSpecification(documentId,
        lockStatus, documentPlatformService.getSpecificationWithEmptyAuthorizationToken());
  }

  @Step("Send PUT request to update document lock status without authorization token")
  public Response updateDocumentLockStatusWithoutAuthorizationToken(String documentId,
      String lockStatus) {
    return documentPlatformService.updateDocumentLockStatusUsingCustomSpecification(documentId,
        lockStatus, documentPlatformService.getSpecificationWithoutAuthorizationToken());
  }

  @Step("Send PUT request to update document lock status using invalid User Id")
  public Response updateDocumentLockStatusUsingInvalidUserId(String documentId,
      String lockStatus) {
    return documentPlatformService.updateDocumentLockStatusUsingCustomSpecification(documentId,
        lockStatus, documentPlatformService.getSpecificationWithInvalidUserId());
  }

  @Step("Send PUT request to update document lock status using empty User Id value")
  public Response updateDocumentLockStatusUsingEmptyUserId(String documentId,
      String lockStatus) {
    return documentPlatformService.updateDocumentLockStatusUsingCustomSpecification(documentId,
        lockStatus, documentPlatformService.getSpecificationWithEmptyUserId());
  }

  @Step("Send PUT request to update document lock status without User Id")
  public Response updateDocumentLockStatusWithoutUserId(String documentId,
      String lockStatus) {
    return documentPlatformService.updateDocumentLockStatusUsingCustomSpecification(documentId,
        lockStatus, documentPlatformService.getSpecificationWithoutUserId());
  }
}
