package com.baks.tests;

import static com.baks.constants.TestDataConstants.INVALID_DOCUMENT_ID;

import com.baks.enums.ResponseErrorModel;
import com.baks.runners.EndpointTestRunner;
import com.baks.steps.DeleteDocumentSteps;
import com.baks.steps.FilterDocumentsSteps;
import com.baks.steps.HardDeleteDocumentsByIdSteps;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.HashMap;
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
@Feature("Hard delete documents API")
@DisplayName("Hard delete documents API tests")
class HardDeleteDocumentsTest extends EndpointTestRunner {

  @Autowired
  private HardDeleteDocumentsByIdSteps hardDeleteDocumentsByIdSteps;
  @Autowired
  private DeleteDocumentSteps deleteDocumentSteps;
  @Autowired
  private FilterDocumentsSteps filterDocumentsSteps;
  private static final int REQUIRED_DOCUMENTS_QTY = 4;
  private static List<String> notDeletedDocumentIdList = new ArrayList<>();
  private static String hardDeletedDocumentId;

  @BeforeAll
  static void initDocumentIdList(
      @Autowired HardDeleteDocumentsByIdSteps hardDeleteDocumentsByIdSteps) {
    notDeletedDocumentIdList = hardDeleteDocumentsByIdSteps.prepareNotDeletedDocumentsIdList(
        REQUIRED_DOCUMENTS_QTY);
  }

  //---------------------------By document Ids -----------------------------------------------
  @Test
  @DisplayName("Hard delete not deleted and soft deleted documents by Document ids")
  @Order(1)
  void hardDeleteDocumentsByDocumentIdsTest() {
    List<String> documentsToDelete =
        notDeletedDocumentIdList.stream().limit(2).collect(Collectors.toList());
    documentsToDelete.addAll(deleteDocumentSteps.prepareDeletedDocumentIdList(2));
    Response deleteResponse =
        hardDeleteDocumentsByIdSteps.hardDeleteDocumentsByDocumentId(documentsToDelete);
    hardDeleteDocumentsByIdSteps.checkResponseStatusCodeIs204(deleteResponse,
        "Hard delete not deleted and soft deleted documents by Document ids");
    notDeletedDocumentIdList.removeAll(documentsToDelete);
    hardDeletedDocumentId = documentsToDelete.get(0);
  }

  @Test
  @DisplayName("Hard delete documents by Document ids where one document is already hard deleted.")
  void hardDeleteDocumentsByDocumentIdsWithAlreadyHardDeletedDocumentTest() {
    String documentId = getNotDeletedDocumentIdFromList();
    String deletedDocumentId = getHardDeletedDocumentId();
    List<String> documentsToDelete = List.of(deletedDocumentId, documentId);
    Response deleteResponse =
        hardDeleteDocumentsByIdSteps.hardDeleteDocumentsByDocumentId(documentsToDelete);
    hardDeleteDocumentsByIdSteps.checkResponseStatusCodeIs207(deleteResponse,
        "Hard delete documents where one document is already deleted");
    hardDeleteDocumentsByIdSteps.checkResponseErrorMessageByModelStateKey(deleteResponse,
        ResponseErrorModel.DOCUMENT_NOT_FOUND, deletedDocumentId);
    notDeletedDocumentIdList.remove(documentId);
  }

  @Test
  @DisplayName("Hard delete documents where one document has invalid document Id")
  void hardDeleteDocumentsByDocumentIdsWithInvalidDocumentIdTest() {
    String documentId = getNotDeletedDocumentIdFromList();
    List<String> documentsToDelete = List.of(documentId, INVALID_DOCUMENT_ID);
    Response deleteResponse =
        hardDeleteDocumentsByIdSteps.hardDeleteDocumentsByDocumentId(documentsToDelete);
    hardDeleteDocumentsByIdSteps.checkResponseStatusCodeIs207(deleteResponse,
        "Hard delete documents where one document has invalid document Id");
    hardDeleteDocumentsByIdSteps.checkResponseErrorMessageByModelStateKey(deleteResponse,
        ResponseErrorModel.DOCUMENT_NOT_FOUND, INVALID_DOCUMENT_ID);
    notDeletedDocumentIdList.remove(documentId);
  }

  //---------------------------Negative cases -----------------------------------------------
  @Test
  @DisplayName("Hard delete already hard deleted single document by Document id")
  void hardDeleteAlreadyHardDeletedDocumentTest() {
    String deletedDocumentId = getHardDeletedDocumentId();
    Response deleteResponse =
        hardDeleteDocumentsByIdSteps.hardDeleteDocumentsByDocumentId(List.of(deletedDocumentId));
    hardDeleteDocumentsByIdSteps.checkResponseStatusCodeIs400(deleteResponse,
        "Hard delete already hard deleted document by Document id");
    hardDeleteDocumentsByIdSteps.checkResponseErrorMessageByModelStateKey(deleteResponse,
        ResponseErrorModel.DOCUMENT_NOT_FOUND, deletedDocumentId);
  }

  @Test
  @DisplayName("Hard delete single document by Document id using invalid document Id")
  void hardDeleteDocumentByIdWithInvalidDocumentIdTest() {
    Response deleteResponse =
        hardDeleteDocumentsByIdSteps.hardDeleteDocumentsByDocumentId(List.of(INVALID_DOCUMENT_ID));
    hardDeleteDocumentsByIdSteps.checkResponseStatusCodeIs400(deleteResponse,
        "Hard delete single document by Document id using invalid document Id");
    hardDeleteDocumentsByIdSteps.checkResponseErrorMessageByModelStateKey(deleteResponse,
        ResponseErrorModel.DOCUMENT_NOT_FOUND, INVALID_DOCUMENT_ID);
  }

  @Test
  @DisplayName("Hard delete documents by Document ids using empty array")
  void hardDeleteDocumentsByDocumentIdsUsingEmptyArrayTest() {
    Response deleteResponse =
        hardDeleteDocumentsByIdSteps.hardDeleteDocumentsByDocumentId(new ArrayList<>());
    hardDeleteDocumentsByIdSteps.checkResponseStatusCodeIs400(deleteResponse,
        "Hard delete documents by Document ids using empty array");
    hardDeleteDocumentsByIdSteps.checkResponseErrorMessage(deleteResponse,
        ResponseErrorModel.DOCUMENT_IDS_EMPTY_ARRAY);
  }

  @Test
  @DisplayName("Hard delete documents by Document ids using invalid request body")
  void hardDeleteDocumentsByDocumentIdsUsingInvalidRequestBodyTest() {
    Response deleteResponse =
        hardDeleteDocumentsByIdSteps.hardDeleteDocumentsByDocumentIdSpecifiedBody(new HashMap<>());
    hardDeleteDocumentsByIdSteps.checkResponseStatusCodeIs400(deleteResponse,
        "Hard delete documents by Document ids using invalid request body");
    hardDeleteDocumentsByIdSteps.checkResponseErrorMessage(deleteResponse,
        ResponseErrorModel.DOCUMENT_IDS_INVALID_ARRAY);
  }

  //---------------------------Cloud Gateway filters ---------------------------------------
  @Test
  @DisplayName("Hard delete documents by document ids using expired authorization token")
  void hardDeleteDocumentsByDocumentIdsWithExpiredAuthorizationTokenTest() {
    Response deleteResponse = hardDeleteDocumentsByIdSteps
        .hardDeleteDocumentsUsingExpiredAuthorizationToken(notDeletedDocumentIdList);
    hardDeleteDocumentsByIdSteps.checkResponseStatusCodeIs401(deleteResponse,
        "Hard delete documents using expired authorization token");
  }

  @Test
  @DisplayName("Hard delete documents by document ids using invalid authorization token")
  void hardDeleteDocumentsByDocumentIdsWithInvalidAuthorizationTokenTest() {
    Response deleteResponse = hardDeleteDocumentsByIdSteps
        .hardDeleteDocumentsUsingInvalidAuthorizationToken(notDeletedDocumentIdList);
    hardDeleteDocumentsByIdSteps.checkResponseStatusCodeIs401(deleteResponse,
        "Hard delete documents using invalid authorization token");
  }

  @Test
  @DisplayName("Hard delete documents by document ids using empty authorization token value")
  void hardDeleteDocumentsByDocumentIdsWithEmptyAuthorizationTokenTest() {
    Response deleteResponse = hardDeleteDocumentsByIdSteps
        .hardDeleteDocumentsUsingEmptyAuthorizationToken(notDeletedDocumentIdList);
    hardDeleteDocumentsByIdSteps.checkResponseStatusCodeIs401(deleteResponse,
        "Hard delete documents using empty authorization token");
  }

  @Test
  @DisplayName("Hard delete documents by document ids without authorization token value")
  void hardDeleteDocumentsByDocumentIdsWithoutAuthorizationTokenTest() {
    Response deleteResponse = hardDeleteDocumentsByIdSteps
        .hardDeleteDocumentsWithoutAuthorizationToken(notDeletedDocumentIdList);
    hardDeleteDocumentsByIdSteps.checkResponseStatusCodeIs401(deleteResponse,
        "Hard delete documents without authorization token");
  }

  @Test
  @DisplayName("Hard delete documents by document ids using invalid User Id")
  void hardDeleteDocumentsByDocumentIdsWithInvalidUserIdTest() {
    Response deleteResponse = hardDeleteDocumentsByIdSteps
        .hardDeleteDocumentsUsingInvalidUserId(notDeletedDocumentIdList);
    hardDeleteDocumentsByIdSteps.checkResponseStatusCodeIs400(deleteResponse,
        "Hard delete documents using invalid User Id");
    hardDeleteDocumentsByIdSteps.checkUserIdResponseErrorMessage(deleteResponse);
  }

  @Test
  @DisplayName("Hard delete documents by document ids with empty User Id")
  void hardDeleteDocumentsByDocumentIdsWithEmptyUserIdTest() {
    Response deleteResponse = hardDeleteDocumentsByIdSteps
        .hardDeleteDocumentsUsingEmptyUserId(notDeletedDocumentIdList);
    hardDeleteDocumentsByIdSteps.checkResponseStatusCodeIs400(deleteResponse,
        "Hard delete documents with empty User Id");
    hardDeleteDocumentsByIdSteps.checkUserIdResponseErrorMessage(deleteResponse);
  }

  @Test
  @DisplayName("Hard delete documents by document ids without User Id")
  void hardDeleteDocumentsByDocumentIdsWithoutUserIdTest() {
    Response deleteResponse = hardDeleteDocumentsByIdSteps.hardDeleteDocumentsWithoutUserId(
        notDeletedDocumentIdList);
    hardDeleteDocumentsByIdSteps.checkResponseStatusCodeIs400(deleteResponse,
        "Hard delete documents without User Id");
    hardDeleteDocumentsByIdSteps.checkUserIdResponseErrorMessage(deleteResponse);
  }

  private String getNotDeletedDocumentIdFromList() {
    return notDeletedDocumentIdList.stream().findFirst().orElseThrow(
        () -> new NoSuchElementException(
            "[Hard delete documents] List of not deleted documents is empty"));
  }

  private String getHardDeletedDocumentId() {
    if (StringUtils.isEmpty(hardDeletedDocumentId)) {
      hardDeletedDocumentId = hardDeleteDocumentsByIdSteps.generateHardDeletedDocumentForTest();
    }
    return hardDeletedDocumentId;
  }
}
