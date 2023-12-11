package com.baks.tests;

import static com.baks.constants.TestDataConstants.INVALID_DOCUMENT_ID;

import com.baks.enums.ResponseErrorModel;
import com.baks.runners.EndpointTestRunner;
import com.baks.steps.DeleteDocumentSteps;
import com.baks.steps.HardDeleteDocumentsByIdSteps;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
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
@DisplayName("Soft Delete document API tests")
class DeleteDocumentTest extends EndpointTestRunner {

  @Autowired
  private DeleteDocumentSteps deleteDocumentSteps;
  @Autowired
  private HardDeleteDocumentsByIdSteps hardDeleteDocumentsByIdSteps;
  private static String documentId;
  private static String deletedDocumentId;

  @BeforeAll
  static void initDocumentId(@Autowired DeleteDocumentSteps deleteDocumentSteps) {
    documentId = deleteDocumentSteps.getRandomDocumentIdFromDocumentsList();
  }

  @Test
  @DisplayName("Soft Delete PDF document by Document id")
  @Order(1)
  void deleteDocumentTest() {
    Response deleteResponse = deleteDocumentSteps.deleteDocumentByDocumentId(documentId);
    deleteDocumentSteps.checkResponseStatusCodeIs204(deleteResponse, "Soft Delete document");

    deleteDocumentSteps.checkDocumentIsDeleted(documentId);
    deletedDocumentId = documentId;
  }

  @Test
  @DisplayName("Soft Delete already deleted document")
  void deleteAlreadyDeletedDocumentTest() {
    Response deleteResponse =
        deleteDocumentSteps.deleteDocumentByDocumentId(getDeletedDocumentId());
    deleteDocumentSteps.checkResponseStatusCodeIs400(deleteResponse,
        "Soft Delete already deleted document");
    deleteDocumentSteps.checkResponseErrorMessage(deleteResponse,
        ResponseErrorModel.DELETED_DOCUMENT_DELETE);
  }

  @Test
  @DisplayName("Soft Delete hard deleted document")
  void deleteHardDeletedDocumentTest() {
    Response deleteResponse = deleteDocumentSteps.deleteDocumentByDocumentId(
        hardDeleteDocumentsByIdSteps.generateHardDeletedDocumentForTest());
    deleteDocumentSteps.checkResponseStatusCodeIs400(deleteResponse,
        "Soft Delete hard deleted document");
    deleteDocumentSteps.checkResponseErrorMessage(deleteResponse,
        ResponseErrorModel.NON_EXISTENT_DOCUMENT);
  }

  @Test
  @DisplayName("Soft Delete document using invalid document Id")
  void deleteDocumentWithInvalidDocumentIdTest() {
    Response deleteResponse = deleteDocumentSteps.deleteDocumentByDocumentId(INVALID_DOCUMENT_ID);
    deleteDocumentSteps.checkResponseStatusCodeIs400(deleteResponse,
        "Soft Delete document using invalid document Id");
    deleteDocumentSteps.checkResponseErrorMessage(deleteResponse,
        ResponseErrorModel.NON_EXISTENT_DOCUMENT);
  }

  @Test
  @DisplayName("Soft Delete document by Document id using expired authorization token")
  void deleteDocumentWithExpiredAuthorizationTokenTest() {
    Response deleteResponse =
        deleteDocumentSteps.deleteDocumentUsingExpiredAuthorizationToken(documentId);
    deleteDocumentSteps.checkResponseStatusCodeIs401(deleteResponse,
        "Soft Delete document by Document id using expired authorization token");
  }

  @Test
  @DisplayName("Soft Delete document by Document id using invalid authorization token")
  void deleteDocumentWithInvalidAuthorizationTokenTest() {
    Response deleteResponse =
        deleteDocumentSteps.deleteDocumentUsingInvalidAuthorizationToken(documentId);
    deleteDocumentSteps.checkResponseStatusCodeIs401(deleteResponse,
        "Soft Delete document by Document id using invalid authorization token");
  }

  @Test
  @DisplayName("Soft Delete document by Document id using empty authorization token value")
  void deleteDocumentWithEmptyAuthorizationTokenTest() {
    Response deleteResponse =
        deleteDocumentSteps.deleteDocumentUsingEmptyAuthorizationToken(documentId);
    deleteDocumentSteps.checkResponseStatusCodeIs401(deleteResponse,
        "Soft Delete document by Document id using empty authorization token");
  }

  @Test
  @DisplayName("Soft Delete document by Document id without authorization token value")
  void deleteDocumentWithoutAuthorizationTokenTest() {
    Response deleteResponse =
        deleteDocumentSteps.deleteDocumentWithoutAuthorizationToken(documentId);
    deleteDocumentSteps.checkResponseStatusCodeIs401(deleteResponse,
        "Soft Delete document by Document id without authorization token");
  }

  @Test
  @DisplayName("Soft Delete document by Document id using invalid User Id")
  void deleteDocumentWithInvalidUserIdTest() {
    Response deleteResponse = deleteDocumentSteps.deleteDocumentUsingInvalidUserId(documentId);
    deleteDocumentSteps.checkResponseStatusCodeIs400(deleteResponse,
        "Soft Delete document by Document id using invalid User Id");
    deleteDocumentSteps.checkUserIdResponseErrorMessage(deleteResponse);
  }

  @Test
  @DisplayName("Soft Delete document by Document id with empty User Id")
  void deleteDocumentWithEmptyUserIdTest() {
    Response deleteResponse = deleteDocumentSteps.deleteDocumentUsingEmptyUserId(documentId);
    deleteDocumentSteps.checkResponseStatusCodeIs400(deleteResponse,
        "Soft Delete document by Document id with empty User Id");
    deleteDocumentSteps.checkUserIdResponseErrorMessage(deleteResponse);
  }

  @Test
  @DisplayName("Soft Delete document by Document id without User Id")
  void deleteDocumentWithoutUserIdTest() {
    Response deleteResponse = deleteDocumentSteps.deleteDocumentWithoutUserId(documentId);
    deleteDocumentSteps.checkResponseStatusCodeIs400(deleteResponse,
        "Soft Delete document by Document id without User Id");
    deleteDocumentSteps.checkUserIdResponseErrorMessage(deleteResponse);
  }

  private String getDeletedDocumentId() {
    if (StringUtils.isEmpty(deletedDocumentId)) {
      deletedDocumentId = deleteDocumentSteps.getRandomDocumentIdFromDeletedDocumentsList();
    }
    return deletedDocumentId;
  }
}
