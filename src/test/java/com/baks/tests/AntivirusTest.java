package com.baks.tests;

import static com.baks.constants.TestDataConstants.ANTIVIRUS_SCAN_STATUS_INFECTED;
import static com.baks.constants.TestDataConstants.ANTIVIRUS_SCAN_STATUS_OK;

import com.baks.pojo.response.DocumentDetailsObject;
import com.baks.runners.EndpointTestRunner;
import com.baks.steps.AntivirusSteps;
import com.baks.steps.DownloadDocumentSteps;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Epic("Antivirus feature")
@Feature("Antivirus feature")
@DisplayName("Antivirus tests")
class AntivirusTest extends EndpointTestRunner {

  @Autowired
  private AntivirusSteps antivirusSteps;
  @Autowired
  private DownloadDocumentSteps downloadDocumentSteps;
  private static String documentId;
  private static String infectedDocumentId;

  @BeforeAll
  static void initDocumentsForTest(@Autowired AntivirusSteps antivirusSteps) {
    documentId = antivirusSteps.generateNotInfectedDocumentForTest();
    infectedDocumentId = antivirusSteps.generateInfectedDocumentForTest();
  }

  @Test
  @DisplayName("Antivirus scan status check for uploaded not infected document")
  void scanStatusCheckForUploadedNotInfectedDocumentTest() {
    DocumentDetailsObject documentDetails = antivirusSteps.getDocumentDetailsObjectByDocumentId(
        documentId);
    antivirusSteps.checkScanStatusInDocumentDetails(documentDetails, ANTIVIRUS_SCAN_STATUS_OK);
    antivirusSteps.checkAvailabilityStatusInDocumentDetails(documentDetails, true);
  }

  @Test
  @DisplayName("Download possibility check for uploaded not infected document")
  void downloadNotInfectedDocumentTest() {
    downloadDocumentSteps.checkResponseStatusCodeIs200(
        downloadDocumentSteps.downloadDocumentByDocumentId(documentId),
        "Download possibility check for uploaded not infected document");
  }
  //------------------------ Tests with infected file ------------------------

  @Test
  @DisplayName("Antivirus scan status check for uploaded infected document")
  void scanStatusCheckForUploadedInfectedDocumentTest() {
    DocumentDetailsObject documentDetails =
        antivirusSteps.getDocumentDetailsObjectByDocumentId(infectedDocumentId);
    antivirusSteps.checkScanStatusInDocumentDetails(documentDetails,
        ANTIVIRUS_SCAN_STATUS_INFECTED);
    antivirusSteps.checkAvailabilityStatusInDocumentDetails(documentDetails, false);
    antivirusSteps.checkUnavailabilityReasonInDocumentDetails(documentDetails, "VIRUS_FOUND");

  }

  @Test
  @DisplayName("Download possibility check for uploaded infected document")
  void downloadInfectedDocumentTest() {
    downloadDocumentSteps.checkResponseStatusCodeIs405(
        downloadDocumentSteps.downloadDocumentByDocumentId(infectedDocumentId),
        "Download possibility check for uploaded infected document");
  }
}
