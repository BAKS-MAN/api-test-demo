package com.baks.steps;

import com.baks.enums.TestDocumentType;
import com.baks.utils.TestDataUtil;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.springframework.stereotype.Component;

/**
 * Step layer for Download document actions.
 */
@Component
public class DownloadDocumentSteps extends BaseDocumentSteps {

  @Step("Send GET request to download document by DocumentId")
  public Response downloadDocumentByDocumentId(String documentId) {
    return documentPlatformService.downloadDocumentByIdUsingCustomSpecification(
        documentId, documentPlatformService.getAuthorizedDpSpecification());
  }

  @Step("Send GET request to download document by DocumentId using unsupported 'Accept' header")
  public Response downloadDocumentUsingUnsupportedAcceptHeader(String documentId) {
    return documentPlatformService.downloadDocumentByIdUsingCustomSpecification(
        documentId, documentPlatformService.getSpecificationWithUnsupportedAcceptHeader());
  }

  @Step("Send GET request to download document by DocumentId using expired authorization token")
  public Response downloadDocumentUsingExpiredAuthorizationToken(String documentId) {
    return documentPlatformService.downloadDocumentByIdUsingCustomSpecification(documentId,
        documentPlatformService.getDpSpecificationWithExpiredToken());
  }

  @Step("Send GET request to download document by DocumentId using invalid authorization token")
  public Response downloadDocumentUsingInvalidAuthorizationToken(String documentId) {
    return documentPlatformService.downloadDocumentByIdUsingCustomSpecification(documentId,
        documentPlatformService.getDpSpecificationWithInvalidAuthorizationToken());
  }

  @Step("Send GET request to download document by DocumentId with empty authorization token value")
  public Response downloadDocumentUsingEmptyAuthorizationToken(String documentId) {
    return documentPlatformService.downloadDocumentByIdUsingCustomSpecification(documentId,
        documentPlatformService.getSpecificationWithEmptyAuthorizationToken());
  }

  @Step("Send GET request to download document by DocumentId without authorization token")
  public Response downloadDocumentWithoutAuthorizationToken(String documentId) {
    return documentPlatformService.downloadDocumentByIdUsingCustomSpecification(documentId,
        documentPlatformService.getSpecificationWithoutAuthorizationToken());
  }

  @Step("Send GET request to download document by DocumentId using invalid User Id")
  public Response downloadDocumentUsingInvalidUserId(String documentId) {
    return documentPlatformService.downloadDocumentByIdUsingCustomSpecification(documentId,
        documentPlatformService.getSpecificationWithInvalidUserId());
  }

  @Step("Send GET request to download document by DocumentId using empty User Id value")
  public Response downloadDocumentUsingEmptyUserId(String documentId) {
    return documentPlatformService.downloadDocumentByIdUsingCustomSpecification(documentId,
        documentPlatformService.getSpecificationWithEmptyUserId());
  }

  @Step("Send GET request to download document by DocumentId without User Id")
  public Response downloadDocumentWithoutUserId(String documentId) {
    return documentPlatformService.downloadDocumentByIdUsingCustomSpecification(documentId,
        documentPlatformService.getSpecificationWithoutUserId());
  }

  //------------------------Verification steps ------------------------
  @Step("Check downloaded document in the response")
  public void checkDownloadDocumentResponse(Response downloadResponse,
      TestDocumentType testDocument) {
    byte[] downloadedFile = downloadResponse.then().extract().asByteArray();
    int expectedDocumentLength = TestDataUtil.getTestDocumentSize(testDocument.getType());

    Assertions.assertThat(downloadedFile)
        .as("Check downloaded document size equals to uploaded document.")
        .hasSize(expectedDocumentLength);
  }
}
