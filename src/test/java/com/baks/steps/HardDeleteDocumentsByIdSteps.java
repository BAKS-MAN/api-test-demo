package com.baks.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Step layer for Hard delete documents by document Ids actions.
 */
@Component
public class HardDeleteDocumentsByIdSteps extends BaseDocumentSteps {

  @Step("Send DELETE request to hard delete documents by document Ids")
  public Response hardDeleteDocumentsByDocumentId(List<String> documentsId) {
    return documentPlatformService.hardDeleteDocumentsByIdUsingCustomSpecification(documentsId,
        documentPlatformService.getAuthorizedDpSpecification());
  }

  @Step("Send DELETE request to hard delete documents by document Ids")
  public Response hardDeleteDocumentsByDocumentIdSpecifiedBody(Object requestBody) {
    return documentPlatformService.hardDeleteDocumentsByIdUsingSpecifiedRequestBody(requestBody);
  }

  @Step("Send DELETE request to hard delete documents by document Ids "
      + "using expired authorization token")
  public Response hardDeleteDocumentsUsingExpiredAuthorizationToken(List<String> documentsId) {
    return documentPlatformService.hardDeleteDocumentsByIdUsingCustomSpecification(documentsId,
        documentPlatformService.getDpSpecificationWithExpiredToken());
  }

  @Step("Send DELETE request to hard delete documents by document Ids "
      + "using invalid authorization token")
  public Response hardDeleteDocumentsUsingInvalidAuthorizationToken(List<String> documentsId) {
    return documentPlatformService.hardDeleteDocumentsByIdUsingCustomSpecification(documentsId,
        documentPlatformService.getDpSpecificationWithInvalidAuthorizationToken());
  }

  @Step("Send DELETE request to hard delete documents by document Ids "
      + "with empty authorization token value")
  public Response hardDeleteDocumentsUsingEmptyAuthorizationToken(List<String> documentsId) {
    return documentPlatformService.hardDeleteDocumentsByIdUsingCustomSpecification(documentsId,
        documentPlatformService.getSpecificationWithEmptyAuthorizationToken());
  }

  @Step("Send DELETE request to hard delete documents by document Ids without authorization token")
  public Response hardDeleteDocumentsWithoutAuthorizationToken(List<String> documentsId) {
    return documentPlatformService.hardDeleteDocumentsByIdUsingCustomSpecification(documentsId,
        documentPlatformService.getSpecificationWithoutAuthorizationToken());
  }

  @Step("Send DELETE request to hard delete documents by document Ids using invalid User Id")
  public Response hardDeleteDocumentsUsingInvalidUserId(List<String> documentsId) {
    return documentPlatformService.hardDeleteDocumentsByIdUsingCustomSpecification(documentsId,
        documentPlatformService.getSpecificationWithInvalidUserId());
  }

  @Step("Send DELETE request to hard delete documents by document Ids using empty User Id value")
  public Response hardDeleteDocumentsUsingEmptyUserId(List<String> documentsId) {
    return documentPlatformService.hardDeleteDocumentsByIdUsingCustomSpecification(documentsId,
        documentPlatformService.getSpecificationWithEmptyUserId());
  }

  @Step("Send DELETE request to hard delete documents by document Ids without User Id")
  public Response hardDeleteDocumentsWithoutUserId(List<String> documentsId) {
    return documentPlatformService.hardDeleteDocumentsByIdUsingCustomSpecification(documentsId,
        documentPlatformService.getSpecificationWithoutUserId());
  }

  @Step("Generate hard deleted document for test")
  public String generateHardDeletedDocumentForTest() {
    String documentId = getRandomDocumentIdFromDocumentsList();
    Response deleteDocumentResponse = hardDeleteDocumentsByDocumentId(List.of(documentId));
    checkResponseStatusCodeIs204(deleteDocumentResponse, "Generate hard deleted document for test");
    // To be sure that document was deleted before other tests will start use it.
    actionWithDelay(() ->
        checkResponseStatusCodeIs404(documentPlatformService.getDocumentDetailsById(documentId),
            "Get hard deleted document details"));
    documentsUsageService.addDocumentIdToUsageList(documentId);
    return documentId;
  }
}
