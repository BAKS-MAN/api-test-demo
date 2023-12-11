package com.baks.tests;

import static com.baks.constants.TestDataConstants.INVALID_DOCUMENT_ID;

import com.baks.enums.ResponseErrorModel;
import com.baks.runners.EndpointTestRunner;
import com.baks.steps.DeleteDocumentSteps;
import com.baks.steps.RecoverDocumentSteps;
import com.baks.steps.RecoverMultipleDocumentsSteps;
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
import org.springframework.beans.factory.annotation.Autowired;

@Epic("Document CRUD operations")
@Feature("Recover documents API")
@DisplayName("Recover multiple documents API tests")
class RecoverMultipleDocumentsTest extends EndpointTestRunner {

  @Autowired
  private RecoverMultipleDocumentsSteps recoverMultipleDocumentsSteps;
  @Autowired
  private RecoverDocumentSteps recoverDocumentSteps;
  private static final int REQUIRED_DELETED_DOCUMENTS_QTY = 4;
  private static List<String> deletedDocumentIdList = new ArrayList<>();
  private static String notDeletedDocumentId;

  @BeforeAll
  static void initDocumentIdList(
      @Autowired DeleteDocumentSteps deleteDocumentSteps) {
    deletedDocumentIdList = deleteDocumentSteps.prepareDeletedDocumentIdList(
        REQUIRED_DELETED_DOCUMENTS_QTY);
  }

  @Test
  @DisplayName("Recover multiple documents by Document id")
  @Order(1)
  void recoverMultipleDocumentsTest() {
    List<String> documentsToRecover =
        deletedDocumentIdList.stream().limit(2).collect(Collectors.toList());
    Response recoveryResponse =
        recoverMultipleDocumentsSteps.recoverMultipleDocumentsByDocumentId(documentsToRecover);
    recoverDocumentSteps.checkResponseStatusCodeIs200(recoveryResponse,
        "Recover multiple documents");
    recoverMultipleDocumentsSteps.checkMultipleDocumentRecoverResponse(recoveryResponse,
        documentsToRecover);
    // Check that all documents were recovered.
    for (String documentId : documentsToRecover) {
      recoverDocumentSteps.checkDocumentIsRecovered(documentId);
      deletedDocumentIdList.remove(documentId);
    }
    notDeletedDocumentId = documentsToRecover.get(0);
  }

  @Test
  @DisplayName("Recover multiple documents where one document is already recovered.")
  void recoverMultipleDocumentsWithAlreadyRecoveredDocumentTest() {
    String deletedDocumentId = getDeletedDocumentIdFromList();
    String notDeletedDocumentId = getNotDeletedDocumentId();
    List<String> documentsToRecover = List.of(notDeletedDocumentId, deletedDocumentId);
    Response recoveryResponse =
        recoverMultipleDocumentsSteps.recoverMultipleDocumentsByDocumentId(documentsToRecover);
    recoverMultipleDocumentsSteps.checkResponseStatusCodeIs207(recoveryResponse,
        "Recover multiple documents where one document is already recovered");
    recoverDocumentSteps.checkDocumentIsRecovered(deletedDocumentId);
    recoverMultipleDocumentsSteps.checkResponseErrorMessageByModelStateKey(recoveryResponse,
        ResponseErrorModel.RECOVERED_DOCUMENT_RECOVER, notDeletedDocumentId);
    deletedDocumentIdList.remove(deletedDocumentId);
  }

  @Test
  @DisplayName("Recover multiple documents where one document has invalid document Id")
  void recoverMultipleDocumentsWithInvalidDocumentIdTest() {
    String deletedDocumentId = getDeletedDocumentIdFromList();
    List<String> documentsToRecover = List.of(deletedDocumentId, INVALID_DOCUMENT_ID);
    Response recoveryResponse =
        recoverMultipleDocumentsSteps.recoverMultipleDocumentsByDocumentId(documentsToRecover);
    recoverMultipleDocumentsSteps.checkResponseStatusCodeIs207(recoveryResponse,
        "Recover multiple documents where one document has invalid document Id");
    recoverDocumentSteps.checkDocumentIsRecovered(deletedDocumentId);
    recoverMultipleDocumentsSteps.checkResponseErrorMessageByModelStateKey(recoveryResponse,
        ResponseErrorModel.NON_EXISTENT_DOCUMENT, INVALID_DOCUMENT_ID);
    deletedDocumentIdList.remove(deletedDocumentId);
  }

  @Test
  @DisplayName("Recover multiple documents using expired authorization token")
  void recoverMultipleDocumentsWithExpiredAuthorizationTokenTest() {
    Response recoveryResponse = recoverMultipleDocumentsSteps
        .recoverMultipleDocumentsUsingExpiredAuthorizationToken(deletedDocumentIdList);
    recoverMultipleDocumentsSteps.checkResponseStatusCodeIs401(recoveryResponse,
        "Recover multiple documents using expired authorization token");
  }

  @Test
  @DisplayName("Recover multiple documents using invalid authorization token")
  void recoverMultipleDocumentsWithInvalidAuthorizationTokenTest() {
    Response recoveryResponse = recoverMultipleDocumentsSteps
        .recoverMultipleDocumentsUsingInvalidAuthorizationToken(deletedDocumentIdList);
    recoverMultipleDocumentsSteps.checkResponseStatusCodeIs401(recoveryResponse,
        "Recover multiple documents using invalid authorization token");
  }

  @Test
  @DisplayName("Recover multiple documents using empty authorization token value")
  void recoverMultipleDocumentsWithEmptyAuthorizationTokenTest() {
    Response recoveryResponse = recoverMultipleDocumentsSteps
        .recoverMultipleDocumentsUsingEmptyAuthorizationToken(deletedDocumentIdList);
    recoverMultipleDocumentsSteps.checkResponseStatusCodeIs401(recoveryResponse,
        "Recover multiple documents using empty authorization token");
  }

  @Test
  @DisplayName("Recover multiple documents without authorization token value")
  void recoverMultipleDocumentsWithoutAuthorizationTokenTest() {
    Response recoveryResponse = recoverMultipleDocumentsSteps
        .recoverMultipleDocumentsWithoutAuthorizationToken(deletedDocumentIdList);
    recoverMultipleDocumentsSteps.checkResponseStatusCodeIs401(recoveryResponse,
        "Recover multiple documents without authorization token");
  }

  @Test
  @DisplayName("Recover multiple documents using invalid User Id")
  void recoverMultipleDocumentsWithInvalidUserIdTest() {
    Response recoveryResponse = recoverMultipleDocumentsSteps
        .recoverMultipleDocumentsUsingInvalidUserId(deletedDocumentIdList);
    recoverMultipleDocumentsSteps.checkResponseStatusCodeIs400(recoveryResponse,
        "Recover multiple documents using invalid User Id");
    recoverMultipleDocumentsSteps.checkUserIdResponseErrorMessage(recoveryResponse);
  }

  @Test
  @DisplayName("Recover multiple documents with empty User Id")
  void recoverMultipleDocumentsWithEmptyUserIdTest() {
    Response recoveryResponse = recoverMultipleDocumentsSteps
        .recoverMultipleDocumentsUsingEmptyUserId(deletedDocumentIdList);
    recoverMultipleDocumentsSteps.checkResponseStatusCodeIs400(recoveryResponse,
        "Recover multiple documents with empty User Id");
    recoverMultipleDocumentsSteps.checkUserIdResponseErrorMessage(recoveryResponse);
  }

  @Test
  @DisplayName("Recover multiple documents without User Id")
  void recoverMultipleDocumentsWithoutUserIdTest() {
    Response recoveryResponse = recoverMultipleDocumentsSteps
        .recoverMultipleDocumentsWithoutUserId(deletedDocumentIdList);
    recoverMultipleDocumentsSteps.checkResponseStatusCodeIs400(recoveryResponse,
        "Recover multiple documents without User Id");
    recoverMultipleDocumentsSteps.checkUserIdResponseErrorMessage(recoveryResponse);
  }

  @Test
  @DisplayName("Recover multiple documents using unsupported 'Accept' header value")
  void recoverMultipleDocumentsWithInvalidAcceptHeaderTest() {
    Response recoveryResponse = recoverMultipleDocumentsSteps
        .recoverMultipleDocumentsUsingUnsupportedAcceptHeader(deletedDocumentIdList);
    recoverMultipleDocumentsSteps.checkResponseStatusCodeIs406(recoveryResponse,
        "Recover multiple documents using unsupported 'Accept' header value");
    recoverDocumentSteps.checkResponseErrorMessageWithoutErrorModel(recoveryResponse,
        ResponseErrorModel.NOT_ACCEPTABLE);
  }

  @Test
  @DisplayName("Recover multiple documents using unsupported 'Content-Type' header value")
  void recoverMultipleDocumentsWithInvalidContentTypeHeaderTest() {
    Response recoveryResponse = recoverMultipleDocumentsSteps
        .recoverMultipleDocumentsUsingUnsupportedContentTypeHeader(deletedDocumentIdList);
    recoverMultipleDocumentsSteps.checkResponseStatusCodeIs415(recoveryResponse,
        "Recover multiple documents using unsupported 'Content-Type' header value");
    recoverDocumentSteps.checkResponseErrorMessageWithoutErrorModel(recoveryResponse,
        ResponseErrorModel.UNSUPPORTED_CONTENT_TYPE);
  }

  @Test
  @DisplayName("Recover multiple documents without 'Content-Type' header")
  void recoverMultipleDocumentsWithNotProvidedContentTypeHeaderTest() {
    Response recoveryResponse = recoverMultipleDocumentsSteps
        .recoverMultipleDocumentsWithoutContentTypeHeader();
    recoverMultipleDocumentsSteps.checkResponseStatusCodeIs415(recoveryResponse,
        "Recover multiple documents without 'Content-Type' header");
    recoverDocumentSteps.checkResponseErrorMessageWithoutErrorModel(recoveryResponse,
        ResponseErrorModel.CONTENT_TYPE_HEADER_MISSED);
  }

  private String getDeletedDocumentIdFromList() {
    return deletedDocumentIdList.stream().findFirst().orElseThrow(
        () -> new NoSuchElementException("[Recover multiple documents] DocumentsId list is empty"));
  }

  private String getNotDeletedDocumentId() {
    if (StringUtils.isEmpty(notDeletedDocumentId)) {
      notDeletedDocumentId = recoverDocumentSteps.getRandomDocumentIdFromDocumentsList();
    }
    return notDeletedDocumentId;
  }
}
