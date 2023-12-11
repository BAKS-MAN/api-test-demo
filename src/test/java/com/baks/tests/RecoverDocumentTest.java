package com.baks.tests;

import static com.baks.constants.TestDataConstants.INVALID_DOCUMENT_ID;

import com.baks.enums.ResponseErrorModel;
import com.baks.runners.EndpointTestRunner;
import com.baks.steps.HardDeleteDocumentsByIdSteps;
import com.baks.steps.RecoverDocumentSteps;
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
@Feature("Recover document API")
@DisplayName("Recover document API tests")
class RecoverDocumentTest extends EndpointTestRunner {

  @Autowired
  private RecoverDocumentSteps recoverDocumentSteps;
  @Autowired
  private HardDeleteDocumentsByIdSteps hardDeleteDocumentsByIdSteps;
  private static String notDeletedDocumentId;
  private static String deletedDocumentId;

  @BeforeAll
  static void initDocumentId(@Autowired RecoverDocumentSteps recoverDocumentSteps) {
    deletedDocumentId = recoverDocumentSteps.getRandomDocumentIdFromDeletedDocumentsList();
  }

  @Test
  @DisplayName("Recover PDF document")
  @Order(1)
  void recoverDocumentTest() {
    Response recoveryResponse = recoverDocumentSteps.recoverDocumentByDocumentId(deletedDocumentId);
    recoverDocumentSteps.checkResponseStatusCodeIs200(recoveryResponse,
        "Recover document");
    recoverDocumentSteps.checkDocumentRecoverResponse(recoveryResponse, deletedDocumentId);
    recoverDocumentSteps.checkDocumentIsRecovered(deletedDocumentId);
    notDeletedDocumentId = deletedDocumentId;
  }

  @Test
  @DisplayName("Recover not deleted document")
  void recoverNotDeletedDocumentTest() {
    Response recoveryResponse =
        recoverDocumentSteps.recoverDocumentByDocumentId(getNotDeletedDocumentId());
    recoverDocumentSteps.checkResponseStatusCodeIs400(recoveryResponse,
        "Recover not deleted document");
    recoverDocumentSteps.checkResponseErrorMessage(recoveryResponse,
        ResponseErrorModel.RECOVERED_DOCUMENT_RECOVER);
  }

  @Test
  @DisplayName("Recover hard deleted document")
  void recoverHardDeletedDocumentTest() {
    Response recoveryResponse = recoverDocumentSteps.recoverDocumentByDocumentId(
        hardDeleteDocumentsByIdSteps.generateHardDeletedDocumentForTest());
    recoverDocumentSteps.checkResponseStatusCodeIs400(recoveryResponse,
        "Recover hard deleted document");
    recoverDocumentSteps.checkResponseErrorMessage(recoveryResponse,
        ResponseErrorModel.NON_EXISTENT_DOCUMENT);
  }

  @Test
  @DisplayName("Recover document by Document id using invalid document Id")
  void recoverDocumentWithInvalidDocumentIdTest() {
    Response recoveryResponse = recoverDocumentSteps.recoverDocumentByDocumentId(
        INVALID_DOCUMENT_ID);
    recoverDocumentSteps.checkResponseStatusCodeIs400(recoveryResponse,
        "Recover document using invalid document Id");
    recoverDocumentSteps.checkResponseErrorMessage(recoveryResponse,
        ResponseErrorModel.NON_EXISTENT_DOCUMENT);
  }

  @Test
  @DisplayName("Recover document by Document id using expired authorization token")
  void recoverDocumentWithExpiredAuthorizationTokenTest() {
    Response recoveryResponse =
        recoverDocumentSteps.recoverDocumentUsingExpiredAuthorizationToken(deletedDocumentId);
    recoverDocumentSteps.checkResponseStatusCodeIs401(recoveryResponse,
        "Recover document by Document id using expired authorization token");
  }

  @Test
  @DisplayName("Recover document by Document id using invalid authorization token")
  void recoverDocumentWithInvalidAuthorizationTokenTest() {
    Response recoveryResponse =
        recoverDocumentSteps.recoverDocumentUsingInvalidAuthorizationToken(deletedDocumentId);
    recoverDocumentSteps.checkResponseStatusCodeIs401(recoveryResponse,
        "Recover document by Document id using invalid authorization token");
  }

  @Test
  @DisplayName("Recover document by Document id using empty authorization token value")
  void recoverDocumentWithEmptyAuthorizationTokenTest() {
    Response recoveryResponse =
        recoverDocumentSteps.recoverDocumentUsingEmptyAuthorizationToken(deletedDocumentId);
    recoverDocumentSteps.checkResponseStatusCodeIs401(recoveryResponse,
        "Recover document by Document id using empty authorization token");
  }

  @Test
  @DisplayName("Recover document by Document id without authorization token value")
  void recoverDocumentWithoutAuthorizationTokenTest() {
    Response recoveryResponse =
        recoverDocumentSteps.recoverDocumentWithoutAuthorizationToken(deletedDocumentId);
    recoverDocumentSteps.checkResponseStatusCodeIs401(recoveryResponse,
        "Recover document by Document id without authorization token");
  }

  @Test
  @DisplayName("Recover document by Document id using invalid User Id")
  void recoverDocumentWithInvalidUserIdTest() {
    Response recoveryResponse =
        recoverDocumentSteps.recoverDocumentUsingInvalidUserId(deletedDocumentId);
    recoverDocumentSteps.checkResponseStatusCodeIs400(recoveryResponse,
        "Recover document by Document id using invalid User Id");
    recoverDocumentSteps.checkUserIdResponseErrorMessage(recoveryResponse);
  }

  @Test
  @DisplayName("Recover document by Document id with empty User Id")
  void recoverDocumentWithEmptyUserIdTest() {
    Response recoveryResponse =
        recoverDocumentSteps.recoverDocumentUsingEmptyUserId(deletedDocumentId);
    recoverDocumentSteps.checkResponseStatusCodeIs400(recoveryResponse,
        "Recover document by Document id with empty User Id");
    recoverDocumentSteps.checkUserIdResponseErrorMessage(recoveryResponse);
  }

  @Test
  @DisplayName("Recover document by Document id without User Id")
  void recoverDocumentWithoutUserIdTest() {
    Response recoveryResponse =
        recoverDocumentSteps.recoverDocumentWithoutUserId(deletedDocumentId);
    recoverDocumentSteps.checkResponseStatusCodeIs400(recoveryResponse,
        "Recover document by Document id without User Id");
    recoverDocumentSteps.checkUserIdResponseErrorMessage(recoveryResponse);
  }

  private String getNotDeletedDocumentId() {
    if (StringUtils.isEmpty(notDeletedDocumentId)) {
      notDeletedDocumentId = recoverDocumentSteps.getRandomDocumentIdFromDocumentsList();
    }
    return notDeletedDocumentId;
  }
}
