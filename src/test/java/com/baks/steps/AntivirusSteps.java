package com.baks.steps;

import com.baks.enums.TestDocumentType;
import com.baks.pojo.response.DocumentDetailsObject;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.springframework.stereotype.Component;

/**
 * Step layer for Read document actions.
 */
@Component
public class AntivirusSteps extends BaseDocumentSteps {

  @Step("Generate infected document for test")
  public String generateNotInfectedDocumentForTest() {
    String documentId = generateDocumentForTest();
    waitForAntivirusScanToBeCompleted(documentId);
    return documentId;
  }

  @Step("Generate infected document for test")
  public String generateInfectedDocumentForTest() {
    Response documentCreationResponse =
        documentPlatformService.uploadDocumentByType(TestDocumentType.INFECTED);
    checkResponseStatusCodeIs201(documentCreationResponse, "Upload infected document for test");
    String documentId = getDocumentIdFromCreationResponse(documentCreationResponse);
    waitForAntivirusScanToBeCompleted(documentId);
    return documentId;
  }

  //------------------------Verification steps ------------------------
  @Step("Check scan status field in document details")
  public void checkScanStatusInDocumentDetails(DocumentDetailsObject documentDetails,
      String expectedScanStatus) {
    Assertions.assertThat(documentDetails.getScanStatus())
        .as("Check scan status field in document details")
        .isEqualToIgnoringCase(expectedScanStatus);
  }

  @Step("Check 'Available' field value in document details")
  public void checkAvailabilityStatusInDocumentDetails(DocumentDetailsObject documentDetails,
      boolean expectedAvailabilityStatus) {
    Assertions.assertThat(documentDetails.isAvailable())
        .as("Check 'Available' field value in document details")
        .isEqualTo(expectedAvailabilityStatus);
  }

  @Step("Check unavailability reason in document details")
  public void checkUnavailabilityReasonInDocumentDetails(DocumentDetailsObject documentDetails,
      String expectedReason) {
    Assertions.assertThat(documentDetails.getUnavailabilityReason())
        .as("Check unavailability reason in document details")
        .isEqualToIgnoringCase(expectedReason);
  }
}
