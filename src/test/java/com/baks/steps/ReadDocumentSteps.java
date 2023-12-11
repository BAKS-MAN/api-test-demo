package com.baks.steps;

import com.baks.pojo.response.DocumentDetailsObject;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import java.util.Set;
import org.assertj.core.api.Assertions;
import org.springframework.stereotype.Component;

/**
 * Step layer for Read document actions.
 */
@Component
public class ReadDocumentSteps extends BaseDocumentSteps {

  @Step("Send GET request for document details by DocumentId using expired authorization token")
  public Response getDocumentUsingExpiredAuthorizationToken(String documentId) {
    return documentPlatformService.getDocumentByIdUsingCustomSpecification(documentId,
        documentPlatformService.getDpSpecificationWithExpiredToken());
  }

  @Step("Send GET request for document details by DocumentId using unsupported 'Accept' header")
  public Response getDocumentUsingUnsupportedAcceptHeader(String documentId) {
    return documentPlatformService.getDocumentByIdUsingCustomSpecification(documentId,
        documentPlatformService.getSpecificationWithUnsupportedAcceptHeader());
  }

  @Step("Send GET request for document details by DocumentId using invalid authorization token")
  public Response getDocumentUsingInvalidAuthorizationToken(String documentId) {
    return documentPlatformService.getDocumentByIdUsingCustomSpecification(documentId,
        documentPlatformService.getDpSpecificationWithInvalidAuthorizationToken());
  }

  @Step("Send GET request for document details by DocumentId with empty authorization token value")
  public Response getDocumentUsingEmptyAuthorizationToken(String documentId) {
    return documentPlatformService.getDocumentByIdUsingCustomSpecification(documentId,
        documentPlatformService.getSpecificationWithEmptyAuthorizationToken());
  }

  @Step("Send GET request for document details by DocumentId without authorization token")
  public Response getDocumentWithoutAuthorizationToken(String documentId) {
    return documentPlatformService.getDocumentByIdUsingCustomSpecification(documentId,
        documentPlatformService.getSpecificationWithoutAuthorizationToken());
  }

  @Step("Send GET request for document details by DocumentId using invalid User Id")
  public Response getDocumentUsingInvalidUserId(String documentId) {
    return documentPlatformService.getDocumentByIdUsingCustomSpecification(documentId,
        documentPlatformService.getSpecificationWithInvalidUserId());
  }

  @Step("Send GET request for document details by DocumentId using empty User Id value")
  public Response getDocumentUsingEmptyUserId(String documentId) {
    return documentPlatformService.getDocumentByIdUsingCustomSpecification(documentId,
        documentPlatformService.getSpecificationWithEmptyUserId());
  }

  @Step("Send GET request for document details by DocumentId without User Id")
  public Response getDocumentWithoutUserId(String documentId) {
    return documentPlatformService.getDocumentByIdUsingCustomSpecification(documentId,
        documentPlatformService.getSpecificationWithoutUserId());
  }

  //------------------------Verification steps ------------------------
  @Step("Check mandatory fields in document details response")
  public void checkDocumentDetailsResponseFields(Response documentDetailsResponse) {
    Set<String> requiredFields = DocumentDetailsObject.getDocumentDetailsMandatoryFields();
    Set<String> actualFields = getResponseFieldKeys(documentDetailsResponse);
    // documentId is added to message for investigation of flaky fails
    String documentId = documentDetailsResponse.as(DocumentDetailsObject.class).getDocumentId();
    Assertions.assertThat(actualFields)
        .as("Document '%s' details response contains all required fields", documentId)
        .containsAll(requiredFields);
  }
}
