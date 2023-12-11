package com.baks.steps;

import static com.baks.constants.ApiRequestsConstants.CONTENT_TYPE_TEST;
import static org.apache.http.HttpHeaders.ACCEPT;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.springframework.stereotype.Component;

/**
 * Step layer for Recover multiple documents actions.
 */
@Component
public class RecoverMultipleDocumentsSteps extends BaseDocumentSteps {

  @Step("Send PUT request to recover multiple documents")
  public Response recoverMultipleDocumentsByDocumentId(List<String> documentsId) {
    return documentPlatformService.recoverMultipleDocumentsByIdUsingCustomSpecification(documentsId,
        documentPlatformService.getAuthorizedDpSpecification());
  }

  @Step("Send PUT request to recover multiple documents using expired authorization token")
  public Response recoverMultipleDocumentsUsingExpiredAuthorizationToken(List<String> documentsId) {
    return documentPlatformService.recoverMultipleDocumentsByIdUsingCustomSpecification(documentsId,
        documentPlatformService.getDpSpecificationWithExpiredToken());
  }

  @Step("Send PUT request to recover multiple documents using invalid authorization token")
  public Response recoverMultipleDocumentsUsingInvalidAuthorizationToken(List<String> documentsId) {
    return documentPlatformService.recoverMultipleDocumentsByIdUsingCustomSpecification(documentsId,
        documentPlatformService.getDpSpecificationWithInvalidAuthorizationToken());
  }

  @Step("Send PUT request to recover multiple documents with empty authorization token value")
  public Response recoverMultipleDocumentsUsingEmptyAuthorizationToken(List<String> documentsId) {
    return documentPlatformService.recoverMultipleDocumentsByIdUsingCustomSpecification(documentsId,
        documentPlatformService.getSpecificationWithEmptyAuthorizationToken());
  }

  @Step("Send PUT request to recover multiple documents without authorization token")
  public Response recoverMultipleDocumentsWithoutAuthorizationToken(List<String> documentsId) {
    return documentPlatformService.recoverMultipleDocumentsByIdUsingCustomSpecification(documentsId,
        documentPlatformService.getSpecificationWithoutAuthorizationToken());
  }

  @Step("Send PUT request to recover multiple documents using invalid User Id")
  public Response recoverMultipleDocumentsUsingInvalidUserId(List<String> documentsId) {
    return documentPlatformService.recoverMultipleDocumentsByIdUsingCustomSpecification(documentsId,
        documentPlatformService.getSpecificationWithInvalidUserId());
  }

  @Step("Send PUT request to recover multiple documents using empty User Id value")
  public Response recoverMultipleDocumentsUsingEmptyUserId(List<String> documentsId) {
    return documentPlatformService.recoverMultipleDocumentsByIdUsingCustomSpecification(documentsId,
        documentPlatformService.getSpecificationWithEmptyUserId());
  }

  @Step("Send PUT request to recover multiple documents without User Id")
  public Response recoverMultipleDocumentsWithoutUserId(List<String> documentsId) {
    return documentPlatformService.recoverMultipleDocumentsByIdUsingCustomSpecification(documentsId,
        documentPlatformService.getSpecificationWithoutUserId());
  }

  @Step("Send POST request to recover multiple documents using unsupported 'Accept' header")
  public Response recoverMultipleDocumentsUsingUnsupportedAcceptHeader(List<String> documentsId) {
    return documentPlatformService.recoverMultipleDocumentsWithSpecifiedHeaders(documentsId,
        Map.of(ACCEPT, CONTENT_TYPE_TEST));
  }

  @Step("Send POST request to recover multiple documents using unsupported 'Content-Type' header")
  public Response recoverMultipleDocumentsUsingUnsupportedContentTypeHeader(
      List<String> documentsId) {
    return documentPlatformService.recoverMultipleDocumentsWithSpecifiedHeaders(documentsId,
        Map.of(CONTENT_TYPE, ContentType.TEXT));
  }

  @Step("Send POST request to recover multiple documents without 'Content-Type' header")
  public Response recoverMultipleDocumentsWithoutContentTypeHeader() {
    return documentPlatformService.recoverMultipleDocumentsWithoutContentTypeHeader();
  }

  @Step("Check recover multiple document response contains recovered Document ID's")
  public void checkMultipleDocumentRecoverResponse(Response recoverResponse,
      List<String> expectedDocumentIdList) {
    Assertions.assertThat(recoverResponse.jsonPath().getList("documentIds"))
        .as("Check multiple recover document response contains recovered Document ID's")
        .isNotNull().containsAll(expectedDocumentIdList);
  }
}
