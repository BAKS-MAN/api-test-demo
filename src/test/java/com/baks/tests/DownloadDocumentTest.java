package com.baks.tests;

import static com.baks.constants.TestDataConstants.INVALID_DOCUMENT_ID;

import com.baks.enums.ResponseErrorModel;
import com.baks.enums.TestDocumentType;
import com.baks.runners.EndpointTestRunner;
import com.baks.steps.CreateDocumentSteps;
import com.baks.steps.DownloadDocumentSteps;
import com.baks.steps.HardDeleteDocumentsByIdSteps;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Epic("Document CRUD operations")
@Feature("Download document API")
@DisplayName("Download document API tests")
class DownloadDocumentTest extends EndpointTestRunner {

  @Autowired
  private DownloadDocumentSteps downloadDocumentSteps;
  @Autowired
  private CreateDocumentSteps createDocumentSteps;
  @Autowired
  private HardDeleteDocumentsByIdSteps hardDeleteDocumentsByIdSteps;
  private static String documentId;

  @BeforeAll
  static void initDocumentId(@Autowired DownloadDocumentSteps downloadDocumentSteps) {
    documentId = downloadDocumentSteps.getRandomDocumentIdFromDocumentsList();
  }

  @Test
  @DisplayName("Download document by document id")
  @Order(1)
  void downloadDocumentTest() {
    TestDocumentType testDocument = TestDocumentType.WORD;
    Response uploadResponse = createDocumentSteps.uploadDocumentByType(testDocument);
    createDocumentSteps.checkCreatedDocumentResponse(uploadResponse);
    documentId = createDocumentSteps.getDocumentIdFromCreationResponse(uploadResponse);
    createDocumentSteps.waitForAntivirusScanToBeCompleted(documentId);
    Response downloadResponse = downloadDocumentSteps.downloadDocumentByDocumentId(documentId);
    downloadDocumentSteps.checkResponseStatusCodeIs200(downloadResponse,
        "Download document by DocumentId");
    downloadDocumentSteps.checkDownloadDocumentResponse(downloadResponse, testDocument);
  }

  @Test
  @DisplayName("Download document using invalid document Id")
  void downloadDocumentWithInvalidDocumentIdTest() {
    Response downloadResponse =
        downloadDocumentSteps.downloadDocumentByDocumentId(INVALID_DOCUMENT_ID);
    downloadDocumentSteps.checkResponseStatusCodeIs404(downloadResponse,
        "Download document using invalid document Id");
    downloadDocumentSteps.checkResponseErrorMessage(downloadResponse,
        ResponseErrorModel.NON_EXISTENT_DOCUMENT);
  }

  @Test
  @DisplayName("Download soft deleted document")
  void downloadSoftDeletedDocumentTest() {
    Response downloadResponse = downloadDocumentSteps.downloadDocumentByDocumentId(
        downloadDocumentSteps.getRandomDocumentIdFromDeletedDocumentsList());
    downloadDocumentSteps.checkResponseStatusCodeIs405(downloadResponse,
        "Download soft deleted document");
    downloadDocumentSteps.checkResponseErrorMessage(downloadResponse,
        ResponseErrorModel.DELETED_DOCUMENT_DOWNLOAD);
  }

  @Test
  @DisplayName("Download hard deleted document")
  void downloadHardDeletedDocumentTest() {
    Response downloadResponse = downloadDocumentSteps.downloadDocumentByDocumentId(
        hardDeleteDocumentsByIdSteps.generateHardDeletedDocumentForTest());
    downloadDocumentSteps.checkResponseStatusCodeIs404(downloadResponse,
        "Download hard deleted document");
    downloadDocumentSteps.checkResponseErrorMessage(downloadResponse,
        ResponseErrorModel.NON_EXISTENT_DOCUMENT);
  }

  @Test
  @DisplayName("Download document using unsupported 'Accept' header value")
  void downloadDocumentWithInvalidAcceptHeaderTest() {
    Response downloadResponse =
        downloadDocumentSteps.downloadDocumentUsingUnsupportedAcceptHeader(documentId);
    downloadDocumentSteps.checkResponseStatusCodeIs406(downloadResponse,
        "Download document using unsupported 'Accept' header value");
    downloadDocumentSteps.checkResponseErrorMessageWithoutErrorModel(downloadResponse,
        ResponseErrorModel.NOT_ACCEPTABLE);
  }

  @Test
  @DisplayName("Download document by document id using expired authorization token")
  void downloadDocumentWithExpiredAuthorizationTokenTest() {
    Response downloadResponse =
        downloadDocumentSteps.downloadDocumentUsingExpiredAuthorizationToken(documentId);
    downloadDocumentSteps.checkResponseStatusCodeIs401(downloadResponse,
        "Download document by document id using expired authorization token");
  }

  @Test
  @DisplayName("Download document by document id using invalid authorization token")
  void downloadDocumentWithInvalidAuthorizationTokenTest() {
    Response downloadResponse =
        downloadDocumentSteps.downloadDocumentUsingInvalidAuthorizationToken(documentId);
    downloadDocumentSteps.checkResponseStatusCodeIs401(downloadResponse,
        "Download document by document id using invalid authorization token");
  }

  @Test
  @DisplayName("Download document by document id using empty authorization token value")
  void downloadDocumentWithEmptyAuthorizationTokenTest() {
    Response downloadResponse =
        downloadDocumentSteps.downloadDocumentUsingEmptyAuthorizationToken(documentId);
    downloadDocumentSteps.checkResponseStatusCodeIs401(downloadResponse,
        "Download document by document id using empty authorization token");
  }

  @Test
  @DisplayName("Download document by document id without authorization token value")
  void downloadDocumentWithoutAuthorizationTokenTest() {
    Response downloadResponse =
        downloadDocumentSteps.downloadDocumentWithoutAuthorizationToken(documentId);
    downloadDocumentSteps.checkResponseStatusCodeIs401(downloadResponse,
        "Download document by document id without authorization token");
  }

  @Test
  @DisplayName("Download document by document id using invalid User Id")
  void downloadDocumentWithInvalidUserIdTest() {
    Response downloadResponse =
        downloadDocumentSteps.downloadDocumentUsingInvalidUserId(documentId);
    downloadDocumentSteps.checkResponseStatusCodeIs400(downloadResponse,
        "Download document by document id using invalid User Id");
    downloadDocumentSteps.checkUserIdResponseErrorMessage(downloadResponse);
  }

  @Test
  @DisplayName("Download document by document id with empty User Id")
  void downloadDocumentWithEmptyUserIdTest() {
    Response downloadResponse =
        downloadDocumentSteps.downloadDocumentUsingEmptyUserId(documentId);
    downloadDocumentSteps.checkResponseStatusCodeIs400(downloadResponse,
        "Download document by document id with empty User Id");
    downloadDocumentSteps.checkUserIdResponseErrorMessage(downloadResponse);
  }

  @Test
  @DisplayName("Download document by document id without User Id")
  void downloadDocumentWithoutUserIdTest() {
    Response downloadResponse = downloadDocumentSteps.downloadDocumentWithoutUserId(documentId);
    downloadDocumentSteps.checkResponseStatusCodeIs400(downloadResponse,
        "Download document by document id without User Id");
    downloadDocumentSteps.checkUserIdResponseErrorMessage(downloadResponse);
  }
}
