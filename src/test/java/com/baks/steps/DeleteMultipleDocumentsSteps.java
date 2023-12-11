package com.baks.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Step layer for Delete multiple documents actions.
 */
@Component
public class DeleteMultipleDocumentsSteps extends BaseDocumentSteps {

  @Step("Send DELETE request to delete multiple documents")
  public Response deleteMultipleDocumentsByDocumentId(List<String> documentsId) {
    return documentPlatformService.deleteMultipleDocumentsByIdUsingCustomSpecification(documentsId,
        documentPlatformService.getAuthorizedDpSpecification());
  }

  @Step("Send DELETE request to delete multiple documents using expired authorization token")
  public Response deleteMultipleDocumentsUsingExpiredAuthorizationToken(List<String> documentsId) {
    return documentPlatformService.deleteMultipleDocumentsByIdUsingCustomSpecification(documentsId,
        documentPlatformService.getDpSpecificationWithExpiredToken());
  }

  @Step("Send DELETE request to delete multiple documents using invalid authorization token")
  public Response deleteMultipleDocumentsUsingInvalidAuthorizationToken(List<String> documentsId) {
    return documentPlatformService.deleteMultipleDocumentsByIdUsingCustomSpecification(documentsId,
        documentPlatformService.getDpSpecificationWithInvalidAuthorizationToken());
  }

  @Step("Send DELETE request to delete multiple documents with empty authorization token value")
  public Response deleteMultipleDocumentsUsingEmptyAuthorizationToken(List<String> documentsId) {
    return documentPlatformService.deleteMultipleDocumentsByIdUsingCustomSpecification(documentsId,
        documentPlatformService.getSpecificationWithEmptyAuthorizationToken());
  }

  @Step("Send DELETE request to delete multiple documents without authorization token")
  public Response deleteMultipleDocumentsWithoutAuthorizationToken(List<String> documentsId) {
    return documentPlatformService.deleteMultipleDocumentsByIdUsingCustomSpecification(documentsId,
        documentPlatformService.getSpecificationWithoutAuthorizationToken());
  }

  @Step("Send DELETE request to delete multiple documents using invalid User Id")
  public Response deleteMultipleDocumentsUsingInvalidUserId(List<String> documentsId) {
    return documentPlatformService.deleteMultipleDocumentsByIdUsingCustomSpecification(documentsId,
        documentPlatformService.getSpecificationWithInvalidUserId());
  }

  @Step("Send DELETE request to delete multiple documents using empty User Id value")
  public Response deleteMultipleDocumentsUsingEmptyUserId(List<String> documentsId) {
    return documentPlatformService.deleteMultipleDocumentsByIdUsingCustomSpecification(documentsId,
        documentPlatformService.getSpecificationWithEmptyUserId());
  }

  @Step("Send DELETE request to delete multiple documents without User Id")
  public Response deleteMultipleDocumentsWithoutUserId(List<String> documentsId) {
    return documentPlatformService.deleteMultipleDocumentsByIdUsingCustomSpecification(documentsId,
        documentPlatformService.getSpecificationWithoutUserId());
  }
}
