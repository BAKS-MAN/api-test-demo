package com.baks.tests;

import static com.baks.constants.TestDataConstants.INVALID_DOCUMENT_ID;

import com.baks.enums.ResponseErrorModel;
import com.baks.runners.EndpointTestRunner;
import com.baks.steps.HardDeleteDocumentsByIdSteps;
import com.baks.steps.ReadDocumentSteps;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Epic("Document CRUD operations")
@Feature("Read document API")
@DisplayName("Read document API tests")
class ReadDocumentTest extends EndpointTestRunner {

  @Autowired
  private ReadDocumentSteps readDocumentSteps;
  @Autowired
  private HardDeleteDocumentsByIdSteps hardDeleteDocumentsByIdSteps;
  private static String documentId;

  @BeforeAll
  static void initDocumentId(@Autowired ReadDocumentSteps readDocumentSteps) {
    documentId = readDocumentSteps.getRandomDocumentIdFromDocumentsList();
  }

  @Test
  @DisplayName("Get document by id and check mandatory fields")
  @Order(1)
  void getDocumentDetailsTest() {
    Response documentResponse = readDocumentSteps.getDocumentDetailsByDocumentId(documentId);
    readDocumentSteps.checkResponseStatusCodeIs200(documentResponse,
        "Get document details by DocumentId");
    readDocumentSteps.checkDocumentDetailsResponseFields(documentResponse);
  }

  @Test
  @DisplayName("Get document using invalid document Id")
  void getDocumentWithInvalidDocumentIdTest() {
    Response documentResponse =
        readDocumentSteps.getDocumentDetailsByDocumentId(INVALID_DOCUMENT_ID);
    readDocumentSteps.checkResponseStatusCodeIs404(documentResponse,
        "Get document using invalid document Id");
    readDocumentSteps.checkResponseErrorMessage(documentResponse,
        ResponseErrorModel.NON_EXISTENT_DOCUMENT);
  }

  @Test
  @DisplayName("Get document using hard deleted document Id")
  void getHardDeletedDocumentTest() {
    Response documentResponse = readDocumentSteps.getDocumentDetailsByDocumentId(
        hardDeleteDocumentsByIdSteps.generateHardDeletedDocumentForTest());
    readDocumentSteps.checkResponseStatusCodeIs404(documentResponse,
        "Get document using hard deleted document Id");
    readDocumentSteps.checkResponseErrorMessage(documentResponse,
        ResponseErrorModel.NON_EXISTENT_DOCUMENT);
  }

  @Test
  @DisplayName("Get document by document id using unsupported 'Accept' header value")
  void getDocumentWithInvalidAcceptHeaderTest() {
    Response documentResponse =
        readDocumentSteps.getDocumentUsingUnsupportedAcceptHeader(documentId);
    readDocumentSteps.checkResponseStatusCodeIs406(documentResponse,
        "Get document by document id using unsupported 'Accept' header value");
    readDocumentSteps.checkResponseErrorMessageWithoutErrorModel(documentResponse,
        ResponseErrorModel.NOT_ACCEPTABLE);
  }

  @Test
  @DisplayName("Get document by document id using expired authorization token")
  void getDocumentWithExpiredAuthorizationTokenTest() {
    Response documentResponse =
        readDocumentSteps.getDocumentUsingExpiredAuthorizationToken(documentId);
    readDocumentSteps.checkResponseStatusCodeIs401(documentResponse,
        "Get document by document id using expired authorization token");
  }

  @Test
  @DisplayName("Get document by document id using invalid authorization token")
  void getDocumentWithInvalidAuthorizationTokenTest() {
    Response documentResponse =
        readDocumentSteps.getDocumentUsingInvalidAuthorizationToken(documentId);
    readDocumentSteps.checkResponseStatusCodeIs401(documentResponse,
        "Get document by document id using invalid authorization token");
  }

  @Test
  @DisplayName("Get document by document id using empty authorization token value")
  void getDocumentWithEmptyAuthorizationTokenTest() {
    Response documentResponse =
        readDocumentSteps.getDocumentUsingEmptyAuthorizationToken(documentId);
    readDocumentSteps.checkResponseStatusCodeIs401(documentResponse,
        "Get document by document id using empty authorization token");
  }

  @Test
  @DisplayName("Get document by document id without authorization token value")
  void getDocumentWithoutAuthorizationTokenTest() {
    Response documentResponse =
        readDocumentSteps.getDocumentWithoutAuthorizationToken(documentId);
    readDocumentSteps.checkResponseStatusCodeIs401(documentResponse,
        "Get document by document id without authorization token");
  }

  @Test
  @DisplayName("Get document by document id using invalid User Id")
  void getDocumentWithInvalidUserIdTest() {
    Response documentResponse =
        readDocumentSteps.getDocumentUsingInvalidUserId(documentId);
    readDocumentSteps.checkResponseStatusCodeIs400(documentResponse,
        "Get document by document id using invalid User Id");
    readDocumentSteps.checkUserIdResponseErrorMessage(documentResponse);
  }

  @Test
  @DisplayName("Get document by document id with empty User Id")
  void getDocumentWithEmptyUserIdTest() {
    Response documentResponse =
        readDocumentSteps.getDocumentUsingEmptyUserId(documentId);
    readDocumentSteps.checkResponseStatusCodeIs400(documentResponse,
        "Get document by document id with empty User Id");
    readDocumentSteps.checkUserIdResponseErrorMessage(documentResponse);
  }

  @Test
  @DisplayName("Get document by document id without User Id")
  void getDocumentWithoutUserIdTest() {
    Response documentResponse =
        readDocumentSteps.getDocumentWithoutUserId(documentId);
    readDocumentSteps.checkResponseStatusCodeIs400(documentResponse,
        "Get document by document id without User Id");
    readDocumentSteps.checkUserIdResponseErrorMessage(documentResponse);
  }
}
