package com.baks.steps;

import com.baks.pojo.response.DocumentDetailsObject;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.springframework.stereotype.Component;

/**
 * Step layer for Delete document actions.
 */
@Component
public class DeleteDocumentSteps extends BaseDocumentSteps {

  @Step("Send DELETE request to delete specified document by DocumentId")
  public Response deleteDocumentByDocumentId(String documentId) {
    return documentPlatformService.deleteDocument(documentId);
  }

  @Step("Send DELETE request to delete document by DocumentId using expired authorization token")
  public Response deleteDocumentUsingExpiredAuthorizationToken(String documentId) {
    return documentPlatformService.deleteDocumentByIdUsingCustomSpecification(documentId,
        documentPlatformService.getDpSpecificationWithExpiredToken());
  }

  @Step("Send DELETE request to delete document by DocumentId using invalid authorization token")
  public Response deleteDocumentUsingInvalidAuthorizationToken(String documentId) {
    return documentPlatformService.deleteDocumentByIdUsingCustomSpecification(documentId,
        documentPlatformService.getDpSpecificationWithInvalidAuthorizationToken());
  }

  @Step("Send DELETE request to delete document by DocumentId with empty authorization token value")
  public Response deleteDocumentUsingEmptyAuthorizationToken(String documentId) {
    return documentPlatformService.deleteDocumentByIdUsingCustomSpecification(documentId,
        documentPlatformService.getSpecificationWithEmptyAuthorizationToken());
  }

  @Step("Send DELETE request to delete document by DocumentId without authorization token")
  public Response deleteDocumentWithoutAuthorizationToken(String documentId) {
    return documentPlatformService.deleteDocumentByIdUsingCustomSpecification(documentId,
        documentPlatformService.getSpecificationWithoutAuthorizationToken());
  }

  @Step("Send DELETE request to delete document by DocumentId using invalid User Id")
  public Response deleteDocumentUsingInvalidUserId(String documentId) {
    return documentPlatformService.deleteDocumentByIdUsingCustomSpecification(documentId,
        documentPlatformService.getSpecificationWithInvalidUserId());
  }

  @Step("Send DELETE request to delete document by DocumentId using empty User Id value")
  public Response deleteDocumentUsingEmptyUserId(String documentId) {
    return documentPlatformService.deleteDocumentByIdUsingCustomSpecification(documentId,
        documentPlatformService.getSpecificationWithEmptyUserId());
  }

  @Step("Send DELETE request to delete document by DocumentId without User Id")
  public Response deleteDocumentWithoutUserId(String documentId) {
    return documentPlatformService.deleteDocumentByIdUsingCustomSpecification(documentId,
        documentPlatformService.getSpecificationWithoutUserId());
  }

  @Step("Prepare list of deleted documents for tests")
  public List<String> prepareDeletedDocumentIdList(int requiredDocumentsQty) {
    return prepareDocumentsForTest(getListOfDeletedDocumentsForTest(), requiredDocumentsQty,
        this::generateDeletedDocumentForTest);
  }

  @Step("Check document 'deleted' field equals 'true' in Document Details")
  public void checkDocumentIsDeleted(String documentId) {
    actionWithDelay(() -> {
      DocumentDetailsObject documentDetails = getDocumentDetailsObjectByDocumentId(documentId);
      Assertions.assertThat(documentDetails.isDeleted())
          .as("[Check Document Details] Document deleted statement")
          .isTrue();
    });
  }
}
