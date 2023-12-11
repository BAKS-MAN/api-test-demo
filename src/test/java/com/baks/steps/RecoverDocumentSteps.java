package com.baks.steps;

import com.baks.pojo.response.DocumentDetailsObject;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.springframework.stereotype.Component;

/**
 * Step layer for Recover document actions.
 */
@Component
public class RecoverDocumentSteps extends BaseDocumentSteps {

  @Step("Send PUT request to recover specified document by DocumentId")
  public Response recoverDocumentByDocumentId(String documentId) {
    return documentPlatformService.recoverDocumentByIdUsingCustomSpecification(documentId,
        documentPlatformService.getAuthorizedDpSpecification());
  }

  @Step("Send PUT request to recover document by DocumentId using expired authorization token")
  public Response recoverDocumentUsingExpiredAuthorizationToken(String documentId) {
    return documentPlatformService.recoverDocumentByIdUsingCustomSpecification(documentId,
        documentPlatformService.getDpSpecificationWithExpiredToken());
  }

  @Step("Send PUT request to recover document by DocumentId using invalid authorization token")
  public Response recoverDocumentUsingInvalidAuthorizationToken(String documentId) {
    return documentPlatformService.recoverDocumentByIdUsingCustomSpecification(documentId,
        documentPlatformService.getDpSpecificationWithInvalidAuthorizationToken());
  }

  @Step("Send PUT request to recover document by DocumentId with empty authorization token value")
  public Response recoverDocumentUsingEmptyAuthorizationToken(String documentId) {
    return documentPlatformService.recoverDocumentByIdUsingCustomSpecification(documentId,
        documentPlatformService.getSpecificationWithEmptyAuthorizationToken());
  }

  @Step("Send PUT request to recover document by DocumentId without authorization token")
  public Response recoverDocumentWithoutAuthorizationToken(String documentId) {
    return documentPlatformService.recoverDocumentByIdUsingCustomSpecification(documentId,
        documentPlatformService.getSpecificationWithoutAuthorizationToken());
  }

  @Step("Send PUT request to recover document by DocumentId using invalid User Id")
  public Response recoverDocumentUsingInvalidUserId(String documentId) {
    return documentPlatformService.recoverDocumentByIdUsingCustomSpecification(documentId,
        documentPlatformService.getSpecificationWithInvalidUserId());
  }

  @Step("Send PUT request to recover document by DocumentId using empty User Id value")
  public Response recoverDocumentUsingEmptyUserId(String documentId) {
    return documentPlatformService.recoverDocumentByIdUsingCustomSpecification(documentId,
        documentPlatformService.getSpecificationWithEmptyUserId());
  }

  @Step("Send PUT request to recover document by DocumentId without User Id")
  public Response recoverDocumentWithoutUserId(String documentId) {
    return documentPlatformService.recoverDocumentByIdUsingCustomSpecification(documentId,
        documentPlatformService.getSpecificationWithoutUserId());
  }

  @Step("Check document 'deleted' field in Document Details after recover")
  public void checkDocumentIsRecovered(String documentId) {
    actionWithDelay(() -> {
      DocumentDetailsObject documentDetails = getDocumentDetailsObjectByDocumentId(documentId);
      Assertions.assertThat(documentDetails.isDeleted())
          .as("[Check Document Details] Document deleted statement after Recover")
          .isFalse();
    });
  }

  @Step("Check recover document response contains recovered Document ID")
  public void checkDocumentRecoverResponse(Response recoverResponse,
      String expectedDocumentId) {
    Assertions.assertThat(recoverResponse.jsonPath().getString("documentId"))
        .as("Check recover document response contains recovered Document ID")
        .isNotNull().contains(expectedDocumentId);
  }
}
