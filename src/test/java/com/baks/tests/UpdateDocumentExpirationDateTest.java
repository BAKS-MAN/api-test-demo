package com.baks.tests;

import static com.baks.constants.TestDataConstants.INVALID_DOCUMENT_ID;
import static com.baks.constants.TestDataConstants.TEST;
import static com.baks.utils.DateUtil.DATE_WITHOUT_TIME_FORMAT;
import static com.baks.utils.DateUtil.ISO_DATE_TIME_FORMAT;
import static com.baks.utils.DateUtil.RFC_DATE_TIME_FORMAT;
import static com.baks.utils.DateUtil.ZONED_DATE_TIME_FORMAT;

import com.baks.enums.ResponseErrorModel;
import com.baks.pojo.response.DocumentDetailsObject;
import com.baks.runners.EndpointTestRunner;
import com.baks.steps.HardDeleteDocumentsByIdSteps;
import com.baks.steps.UpdateDocumentExpirationDateSteps;
import com.baks.utils.DateUtil;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

@Epic("Document CRUD operations")
@Feature("Update document API")
@DisplayName("Update document expiration date tests")
class UpdateDocumentExpirationDateTest extends EndpointTestRunner {

  @Autowired
  private UpdateDocumentExpirationDateSteps updateDocumentExpirationDateSteps;
  @Autowired
  private HardDeleteDocumentsByIdSteps hardDeleteDocumentsByIdSteps;
  private static String documentId;
  private static String expirationDate;

  @BeforeAll
  static void initDocumentId(
      @Autowired UpdateDocumentExpirationDateSteps updateDocumentExpirationDateSteps) {
    documentId = updateDocumentExpirationDateSteps.getRandomDocumentIdWithActiveLockStatus();
    expirationDate = DateUtil.getCurrentFormattedDatePlusDays(1);
  }

  @ParameterizedTest
  @DisplayName("Update document expiration date using all supported date formats")
  @ValueSource(strings = {ZONED_DATE_TIME_FORMAT, ISO_DATE_TIME_FORMAT, DATE_WITHOUT_TIME_FORMAT,
      RFC_DATE_TIME_FORMAT})
  @Order(1)
  void updateDocumentExpirationDateInSpecifiedFormatTest(String datePattern) {
    String formattedDate = DateUtil.getCurrentDatePlusMonthsInSpecifiedFormat(3, datePattern);
    Response updateResponse = updateDocumentExpirationDateSteps.updateDocumentExpirationDate(
        documentId, formattedDate);
    updateDocumentExpirationDateSteps.checkResponseStatusCodeIs200(updateResponse,
        String.format("Update document expiration date using date in '%s' format", datePattern));
    updateDocumentExpirationDateSteps.actionWithDelay(() -> {
      DocumentDetailsObject documentDetails =
          updateDocumentExpirationDateSteps.getDocumentDetailsObjectByDocumentId(documentId);
      updateDocumentExpirationDateSteps.checkExpirationDateInDocumentDetails(documentDetails,
          formattedDate);
    });
  }

  @Test
  @DisplayName("Update document expiration date for read only document")
  void updateReadOnlyDocumentExpirationDateTest() {
    String lockedDocumentId = updateDocumentExpirationDateSteps.getRandomReadOnlyDocumentId();
    Response updateResponse = updateDocumentExpirationDateSteps.updateDocumentExpirationDate(
        lockedDocumentId, expirationDate);
    updateDocumentExpirationDateSteps.checkResponseStatusCodeIs400(updateResponse,
        "Update document expiration date for read only document");
    updateDocumentExpirationDateSteps.checkResponseErrorMessage(updateResponse,
        ResponseErrorModel.READ_ONLY_DOCUMENT);
  }

  @Test
  @DisplayName("Update document expiration date to value less than Current System Datetime")
  void updateDocumentExpirationDateLessThenCurrentSystemDateTest() {
    String dateInPast = DateUtil.getCurrentIsoDateTimeMinusSpecifiedHours(1);
    Response updateResponse = updateDocumentExpirationDateSteps.updateDocumentExpirationDate(
        documentId, dateInPast);
    updateDocumentExpirationDateSteps.checkResponseStatusCodeIs400(updateResponse,
        "Update document expiration date to value less than Current System Datetime");
    updateDocumentExpirationDateSteps.checkResponseErrorMessage(updateResponse,
        ResponseErrorModel.EXPIRATION_DATE);
  }

  @Test
  @DisplayName("Update document expiration date to Document Creation Date value")
  void updateDocumentExpirationDateToDocumentCreationDateTest() {
    String documentCreationDate =
        updateDocumentExpirationDateSteps.getDocumentCreationDate(documentId);
    Response updateResponse = updateDocumentExpirationDateSteps.updateDocumentExpirationDate(
        documentId, documentCreationDate);
    updateDocumentExpirationDateSteps.checkResponseStatusCodeIs400(updateResponse,
        "Update document expiration date to Document Creation Date value");
    updateDocumentExpirationDateSteps.checkResponseErrorMessage(updateResponse,
        ResponseErrorModel.EXPIRATION_DATE);
  }

  @Test
  @DisplayName("Update document expiration date with invalid value")
  void updateDocumentExpirationDateUsingInvalidValueTest() {
    Response updateResponse = updateDocumentExpirationDateSteps.updateDocumentExpirationDate(
        documentId, TEST);
    updateDocumentExpirationDateSteps.checkResponseStatusCodeIs400(updateResponse,
        "Update document expiration date with invalid value");
    updateDocumentExpirationDateSteps.checkResponseErrorMessage(updateResponse,
        ResponseErrorModel.EXPIRATION_DATE);
  }

  @Test
  @DisplayName("Update document expiration date using invalid document Id")
  void updateDocumentExpirationDateUsingInvalidDocumentIdTest() {
    Response updateResponse = updateDocumentExpirationDateSteps.updateDocumentExpirationDate(
        INVALID_DOCUMENT_ID, expirationDate);
    updateDocumentExpirationDateSteps.checkResponseStatusCodeIs400(updateResponse,
        "Update document expiration date using invalid document Id");
    updateDocumentExpirationDateSteps.checkResponseErrorMessage(updateResponse,
        ResponseErrorModel.NON_EXISTENT_DOCUMENT);
  }

  @Test
  @DisplayName("Update hard deleted document expiration date")
  void updateHardDeletedDocumentExpirationDateTest() {
    Response updateResponse = updateDocumentExpirationDateSteps.updateDocumentExpirationDate(
        hardDeleteDocumentsByIdSteps.generateHardDeletedDocumentForTest(), expirationDate);
    updateDocumentExpirationDateSteps.checkResponseStatusCodeIs400(updateResponse,
        "Update hard deleted document expiration date");
    updateDocumentExpirationDateSteps.checkResponseErrorMessage(updateResponse,
        ResponseErrorModel.NON_EXISTENT_DOCUMENT);
  }

  @Test
  @DisplayName("Update document expiration date using expired authorization token")
  void updateDocumentExpirationDateWithExpiredAuthorizationTokenTest() {
    Response updateResponse = updateDocumentExpirationDateSteps
        .updateDocumentExpirationDateUsingExpiredAuthorizationToken(documentId, expirationDate);
    updateDocumentExpirationDateSteps.checkResponseStatusCodeIs401(updateResponse,
        "Update document expiration date using expired authorization token");
  }

  @Test
  @DisplayName("Update document expiration date using invalid authorization token")
  void updateDocumentExpirationDateWithInvalidAuthorizationTokenTest() {
    Response updateResponse = updateDocumentExpirationDateSteps
        .updateDocumentExpirationDateUsingInvalidAuthorizationToken(documentId, expirationDate);
    updateDocumentExpirationDateSteps.checkResponseStatusCodeIs401(updateResponse,
        "Update document expiration date using invalid authorization token");
  }

  @Test
  @DisplayName("Update document expiration date using empty authorization token value")
  void updateDocumentExpirationDateWithEmptyAuthorizationTokenTest() {
    Response updateResponse = updateDocumentExpirationDateSteps
        .updateDocumentExpirationDateUsingEmptyAuthorizationToken(documentId, expirationDate);
    updateDocumentExpirationDateSteps.checkResponseStatusCodeIs401(updateResponse,
        "Update document expiration date using empty authorization token");
  }

  @Test
  @DisplayName("Update document expiration date without authorization token value")
  void updateDocumentExpirationDateWithoutAuthorizationTokenTest() {
    Response updateResponse = updateDocumentExpirationDateSteps
        .updateDocumentExpirationDateWithoutAuthorizationToken(documentId, expirationDate);
    updateDocumentExpirationDateSteps.checkResponseStatusCodeIs401(updateResponse,
        "Update document expiration date without authorization token");
  }

  @Test
  @DisplayName("Update document expiration date using invalid User Id")
  void updateDocumentExpirationDateWithInvalidUserIdTest() {
    Response updateResponse = updateDocumentExpirationDateSteps
        .updateDocumentExpirationDateUsingInvalidUserId(documentId, expirationDate);
    updateDocumentExpirationDateSteps.checkResponseStatusCodeIs400(updateResponse,
        "Update document expiration date using invalid User Id");
    updateDocumentExpirationDateSteps.checkUserIdResponseErrorMessage(updateResponse);
  }

  @Test
  @DisplayName("Update document expiration date with empty User Id")
  void updateDocumentExpirationDateWithEmptyUserIdTest() {
    Response updateResponse = updateDocumentExpirationDateSteps
        .updateDocumentExpirationDateUsingEmptyUserId(documentId, expirationDate);
    updateDocumentExpirationDateSteps.checkResponseStatusCodeIs400(updateResponse,
        "Update document expiration date with empty User Id");
    updateDocumentExpirationDateSteps.checkUserIdResponseErrorMessage(updateResponse);
  }

  @Test
  @DisplayName("Update document expiration date without User Id")
  void updateDocumentExpirationDateWithoutUserIdTest() {
    Response updateResponse = updateDocumentExpirationDateSteps
        .updateDocumentExpirationDateWithoutUserId(documentId, expirationDate);
    updateDocumentExpirationDateSteps.checkResponseStatusCodeIs400(updateResponse,
        "Update document expiration date without User Id");
    updateDocumentExpirationDateSteps.checkUserIdResponseErrorMessage(updateResponse);
  }
}
