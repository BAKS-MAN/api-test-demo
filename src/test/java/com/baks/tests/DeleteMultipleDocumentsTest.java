package com.baks.tests;

import static com.baks.constants.TestDataConstants.INVALID_DOCUMENT_ID;

import com.baks.enums.ResponseErrorModel;
import com.baks.runners.EndpointTestRunner;
import com.baks.steps.DeleteDocumentSteps;
import com.baks.steps.DeleteMultipleDocumentsSteps;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.springframework.beans.factory.annotation.Autowired;

@Isolated
@Epic("Document CRUD operations")
@Feature("Soft Delete documents API")
@DisplayName("Soft Delete multiple documents API tests")
class DeleteMultipleDocumentsTest extends EndpointTestRunner {

  @Autowired
  private DeleteMultipleDocumentsSteps deleteMultipleDocumentsSteps;
  @Autowired
  private DeleteDocumentSteps deleteDocumentSteps;
  private static final int REQUIRED_DOCUMENTS_QTY = 4;
  private static List<String> notDeletedDocumentIdList = new ArrayList<>();
  private static String deletedDocumentId;

  @BeforeAll
  static void initDocumentIdList(
      @Autowired DeleteMultipleDocumentsSteps deleteMultipleDocumentsSteps) {
    notDeletedDocumentIdList = deleteMultipleDocumentsSteps.prepareNotDeletedDocumentsIdList(
        REQUIRED_DOCUMENTS_QTY);
  }

  @Test
  @DisplayName("Soft Delete multiple documents by Document id")
  @Order(1)
  void deleteMultipleDocumentsTest() {
    List<String> documentsToDelete =
        notDeletedDocumentIdList.stream().limit(2).collect(Collectors.toList());
    Response deleteResponse =
        deleteMultipleDocumentsSteps.deleteMultipleDocumentsByDocumentId(documentsToDelete);
    deleteMultipleDocumentsSteps.checkResponseStatusCodeIs204(deleteResponse,
        "Soft Delete multiple documents");
    // Check that all documents were deleted.
    for (String documentId : documentsToDelete) {
      deleteDocumentSteps.checkDocumentIsDeleted(documentId);
      notDeletedDocumentIdList.remove(documentId);
    }
    deletedDocumentId = documentsToDelete.get(0);
  }

  @Test
  @DisplayName("Soft Delete multiple documents where one document is already deleted.")
  void deleteMultipleDocumentsWithAlreadyDeletedDocumentTest() {
    String documentId = getNotDeletedDocumentIdFromList();
    String deletedDocumentId = getDeletedDocumentId();
    List<String> documentsToDelete = List.of(deletedDocumentId, documentId);
    Response deleteResponse =
        deleteMultipleDocumentsSteps.deleteMultipleDocumentsByDocumentId(documentsToDelete);
    deleteMultipleDocumentsSteps.checkResponseStatusCodeIs207(deleteResponse,
        "Soft Delete multiple documents where one document is already deleted");
    deleteDocumentSteps.checkDocumentIsDeleted(documentId);
    deleteMultipleDocumentsSteps.checkResponseErrorMessageByModelStateKey(deleteResponse,
        ResponseErrorModel.DELETED_DOCUMENT_DELETE, deletedDocumentId);
    notDeletedDocumentIdList.remove(documentId);
  }

  @Test
  @DisplayName("Soft Delete multiple documents where one document has invalid document Id")
  void deleteMultipleDocumentsWithInvalidDocumentIdTest() {
    String documentId = getNotDeletedDocumentIdFromList();
    List<String> documentsToDelete = List.of(documentId, INVALID_DOCUMENT_ID);
    Response deleteResponse =
        deleteMultipleDocumentsSteps.deleteMultipleDocumentsByDocumentId(documentsToDelete);
    deleteMultipleDocumentsSteps.checkResponseStatusCodeIs207(deleteResponse,
        "Soft Delete multiple documents where one document has invalid document Id");
    deleteDocumentSteps.checkDocumentIsDeleted(documentId);
    deleteMultipleDocumentsSteps.checkResponseErrorMessageByModelStateKey(deleteResponse,
        ResponseErrorModel.NON_EXISTENT_DOCUMENT, INVALID_DOCUMENT_ID);
    notDeletedDocumentIdList.remove(documentId);
  }

  @Test
  @DisplayName("Soft Delete multiple documents using expired authorization token")
  void deleteMultipleDocumentsWithExpiredAuthorizationTokenTest() {
    Response deleteResponse = deleteMultipleDocumentsSteps
        .deleteMultipleDocumentsUsingExpiredAuthorizationToken(notDeletedDocumentIdList);
    deleteMultipleDocumentsSteps.checkResponseStatusCodeIs401(deleteResponse,
        "Soft Delete multiple documents using expired authorization token");
  }

  @Test
  @DisplayName("Soft Delete multiple documents using invalid authorization token")
  void deleteMultipleDocumentsWithInvalidAuthorizationTokenTest() {
    Response deleteResponse = deleteMultipleDocumentsSteps
        .deleteMultipleDocumentsUsingInvalidAuthorizationToken(notDeletedDocumentIdList);
    deleteMultipleDocumentsSteps.checkResponseStatusCodeIs401(deleteResponse,
        "Soft Delete multiple documents using invalid authorization token");
  }

  @Test
  @DisplayName("Soft Delete multiple documents using empty authorization token value")
  void deleteMultipleDocumentsWithEmptyAuthorizationTokenTest() {
    Response deleteResponse = deleteMultipleDocumentsSteps
        .deleteMultipleDocumentsUsingEmptyAuthorizationToken(notDeletedDocumentIdList);
    deleteMultipleDocumentsSteps.checkResponseStatusCodeIs401(deleteResponse,
        "Soft Delete multiple documents using empty authorization token");
  }

  @Test
  @DisplayName("Soft Delete multiple documents without authorization token value")
  void deleteMultipleDocumentsWithoutAuthorizationTokenTest() {
    Response deleteResponse = deleteMultipleDocumentsSteps
        .deleteMultipleDocumentsWithoutAuthorizationToken(notDeletedDocumentIdList);
    deleteMultipleDocumentsSteps.checkResponseStatusCodeIs401(deleteResponse,
        "Soft Delete multiple documents without authorization token");
  }

  @Test
  @DisplayName("Soft Delete multiple documents using invalid User Id")
  void deleteMultipleDocumentsWithInvalidUserIdTest() {
    Response deleteResponse = deleteMultipleDocumentsSteps
        .deleteMultipleDocumentsUsingInvalidUserId(notDeletedDocumentIdList);
    deleteMultipleDocumentsSteps.checkResponseStatusCodeIs400(deleteResponse,
        "Soft Delete multiple documents using invalid User Id");
    deleteMultipleDocumentsSteps.checkUserIdResponseErrorMessage(deleteResponse);
  }

  @Test
  @DisplayName("Soft Delete multiple documents with empty User Id")
  void deleteMultipleDocumentsWithEmptyUserIdTest() {
    Response deleteResponse = deleteMultipleDocumentsSteps
        .deleteMultipleDocumentsUsingEmptyUserId(notDeletedDocumentIdList);
    deleteMultipleDocumentsSteps.checkResponseStatusCodeIs400(deleteResponse,
        "Soft Delete multiple documents with empty User Id");
    deleteMultipleDocumentsSteps.checkUserIdResponseErrorMessage(deleteResponse);
  }

  @Test
  @DisplayName("Soft Delete multiple documents without User Id")
  void deleteMultipleDocumentsWithoutUserIdTest() {
    Response deleteResponse = deleteMultipleDocumentsSteps.deleteMultipleDocumentsWithoutUserId(
        notDeletedDocumentIdList);
    deleteMultipleDocumentsSteps.checkResponseStatusCodeIs400(deleteResponse,
        "Soft Delete multiple documents without User Id");
    deleteMultipleDocumentsSteps.checkUserIdResponseErrorMessage(deleteResponse);
  }

  private String getNotDeletedDocumentIdFromList() {
    return notDeletedDocumentIdList.stream().findFirst().orElseThrow(
        () -> new NoSuchElementException("[Delete multiple documents] DocumentsId list is empty"));
  }

  private String getDeletedDocumentId() {
    if (StringUtils.isEmpty(deletedDocumentId)) {
      deletedDocumentId = deleteDocumentSteps.getRandomDocumentIdFromDeletedDocumentsList();
    }
    return deletedDocumentId;
  }
}
