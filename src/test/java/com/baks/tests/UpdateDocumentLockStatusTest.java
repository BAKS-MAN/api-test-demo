package com.baks.tests;

import static com.baks.constants.ApiRequestsConstants.LOCK_STATUS_ACTIVE_VALUE;
import static com.baks.constants.ApiRequestsConstants.LOCK_STATUS_READ_ONLY_VALUE;
import static com.baks.constants.TestDataConstants.INVALID_DOCUMENT_ID;
import static com.baks.constants.TestDataConstants.TEST;

import com.baks.enums.ResponseErrorModel;
import com.baks.pojo.response.DocumentDetailsObject;
import com.baks.runners.EndpointTestRunner;
import com.baks.steps.HardDeleteDocumentsByIdSteps;
import com.baks.steps.UpdateDocumentLockStatusSteps;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Epic("Document CRUD operations")
@Feature("Update document API")
@DisplayName("Update document lock status tests")
class UpdateDocumentLockStatusTest extends EndpointTestRunner {

  @Autowired
  private UpdateDocumentLockStatusSteps updateDocumentLockStatusSteps;
  @Autowired
  private HardDeleteDocumentsByIdSteps hardDeleteDocumentsByIdSteps;
  private static String documentId;
  private static final String LOCK_STATUS = LOCK_STATUS_READ_ONLY_VALUE;

  @BeforeAll
  static void initDocumentId(
      @Autowired UpdateDocumentLockStatusSteps updateDocumentLockStatusSteps) {
    documentId = updateDocumentLockStatusSteps.getRandomDocumentIdWithActiveLockStatus();
  }

  @Test
  @DisplayName("Update 'Active' document lock status to 'Read only'")
  @Order(1)
  void updateDocumentLockStatusToReadOnlyTest() {
    Response updateResponse = updateDocumentLockStatusSteps.updateDocumentLockStatus(
        documentId, LOCK_STATUS);
    updateDocumentLockStatusSteps.checkResponseStatusCodeIs200(updateResponse,
        "Update document lock status to 'Read only'");
    updateDocumentLockStatusSteps.actionWithDelay(() -> {
      DocumentDetailsObject documentDetails =
          updateDocumentLockStatusSteps.getDocumentDetailsObjectByDocumentId(documentId);
      updateDocumentLockStatusSteps.checkLockStatusInDocumentDetails(documentDetails, LOCK_STATUS);
    });
  }

  @Test
  @DisplayName("Update 'Read only' document lock status to 'Active'")
  @Order(2)
  void updateDocumentLockStatusToActiveTest() {
    String newLockStatus = LOCK_STATUS_ACTIVE_VALUE;
    String documentToUpdate = updateDocumentLockStatusSteps.getRandomReadOnlyDocumentId();
    Response updateResponse = updateDocumentLockStatusSteps.updateDocumentLockStatus(
        documentToUpdate, newLockStatus);
    updateDocumentLockStatusSteps.checkResponseStatusCodeIs200(updateResponse,
        "Update 'Read only' document lock status to 'Active'");
    updateDocumentLockStatusSteps.actionWithDelay(() -> {
      DocumentDetailsObject documentDetails =
          updateDocumentLockStatusSteps.getDocumentDetailsObjectByDocumentId(documentToUpdate);
      updateDocumentLockStatusSteps.checkLockStatusInDocumentDetails(documentDetails,
          newLockStatus);
    });
  }

  @Test
  @DisplayName("Update document lock status with unsupported value")
  void updateDocumentLockStatusUsingUnsupportedValueTest() {
    Response updateResponse = updateDocumentLockStatusSteps.updateDocumentLockStatus(
        documentId, TEST);
    updateDocumentLockStatusSteps.checkResponseStatusCodeIs400(updateResponse,
        "Update a document lock status with unsupported value");
    updateDocumentLockStatusSteps.checkResponseErrorMessage(updateResponse,
        ResponseErrorModel.INVALID_LOCK_TYPE);
  }


  @Test
  @DisplayName("Update document lock status using invalid document Id")
  void updateDocumentLockStatusUsingInvalidDocumentIdTest() {
    Response updateResponse = updateDocumentLockStatusSteps.updateDocumentLockStatus(
        INVALID_DOCUMENT_ID, LOCK_STATUS);
    updateDocumentLockStatusSteps.checkResponseStatusCodeIs400(updateResponse,
        "Update document lock status using invalid document Id");
    updateDocumentLockStatusSteps.checkResponseErrorMessage(updateResponse,
        ResponseErrorModel.NON_EXISTENT_DOCUMENT);
  }

  @Test
  @DisplayName("Update hard deleted document lock status")
  void updateHardDeletedDocumentLockStatusTest() {
    Response updateResponse = updateDocumentLockStatusSteps.updateDocumentLockStatus(
        hardDeleteDocumentsByIdSteps.generateHardDeletedDocumentForTest(), LOCK_STATUS);
    updateDocumentLockStatusSteps.checkResponseStatusCodeIs400(updateResponse,
        "Update hard deleted document lock status");
    updateDocumentLockStatusSteps.checkResponseErrorMessage(updateResponse,
        ResponseErrorModel.NON_EXISTENT_DOCUMENT);
  }

  @Test
  @DisplayName("Update document lock status using expired authorization token")
  void updateDocumentLockStatusWithExpiredAuthorizationTokenTest() {
    Response updateResponse = updateDocumentLockStatusSteps
        .updateDocumentLockStatusUsingExpiredAuthorizationToken(documentId, LOCK_STATUS);
    updateDocumentLockStatusSteps.checkResponseStatusCodeIs401(updateResponse,
        "Update document lock status using expired authorization token");
  }

  @Test
  @DisplayName("Update document lock status using invalid authorization token")
  void updateDocumentLockStatusWithInvalidAuthorizationTokenTest() {
    Response updateResponse = updateDocumentLockStatusSteps
        .updateDocumentLockStatusUsingInvalidAuthorizationToken(documentId, LOCK_STATUS);
    updateDocumentLockStatusSteps.checkResponseStatusCodeIs401(updateResponse,
        "Update document lock status using invalid authorization token");
  }

  @Test
  @DisplayName("Update document lock status using empty authorization token value")
  void updateDocumentLockStatusWithEmptyAuthorizationTokenTest() {
    Response updateResponse = updateDocumentLockStatusSteps
        .updateDocumentLockStatusUsingEmptyAuthorizationToken(documentId, LOCK_STATUS);
    updateDocumentLockStatusSteps.checkResponseStatusCodeIs401(updateResponse,
        "Update document lock status using empty authorization token");
  }

  @Test
  @DisplayName("Update document lock status without authorization token value")
  void updateDocumentLockStatusWithoutAuthorizationTokenTest() {
    Response updateResponse = updateDocumentLockStatusSteps
        .updateDocumentLockStatusWithoutAuthorizationToken(documentId, LOCK_STATUS);
    updateDocumentLockStatusSteps.checkResponseStatusCodeIs401(updateResponse,
        "Update document lock status without authorization token");
  }

  @Test
  @DisplayName("Update document lock status using invalid User Id")
  void updateDocumentLockStatusWithInvalidUserIdTest() {
    Response updateResponse = updateDocumentLockStatusSteps
        .updateDocumentLockStatusUsingInvalidUserId(documentId, LOCK_STATUS);
    updateDocumentLockStatusSteps.checkResponseStatusCodeIs400(updateResponse,
        "Update document lock status using invalid User Id");
    updateDocumentLockStatusSteps.checkUserIdResponseErrorMessage(updateResponse);
  }

  @Test
  @DisplayName("Update document lock status with empty User Id")
  void updateDocumentLockStatusWithEmptyUserIdTest() {
    Response updateResponse = updateDocumentLockStatusSteps
        .updateDocumentLockStatusUsingEmptyUserId(documentId, LOCK_STATUS);
    updateDocumentLockStatusSteps.checkResponseStatusCodeIs400(updateResponse,
        "Update document lock status with empty User Id");
    updateDocumentLockStatusSteps.checkUserIdResponseErrorMessage(updateResponse);
  }

  @Test
  @DisplayName("Update document lock status without User Id")
  void updateDocumentLockStatusWithoutUserIdTest() {
    Response updateResponse = updateDocumentLockStatusSteps
        .updateDocumentLockStatusWithoutUserId(documentId, LOCK_STATUS);
    updateDocumentLockStatusSteps.checkResponseStatusCodeIs400(updateResponse,
        "Update document lock status without User Id");
    updateDocumentLockStatusSteps.checkUserIdResponseErrorMessage(updateResponse);
  }
}
