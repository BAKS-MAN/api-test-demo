package com.baks.steps;

import static com.baks.constants.ApiRequestsConstants.DOCUMENT_CATEGORY_HEADER;
import static com.baks.constants.ApiRequestsConstants.DOCUMENT_TITLE_HEADER;
import static com.baks.constants.ApiRequestsConstants.EXPIRES_AT_HEADER;
import static com.baks.constants.ApiRequestsConstants.LOCK_STATUS_ACTIVE_VALUE;
import static com.baks.constants.ApiRequestsConstants.LOCK_STATUS_HEADER;
import static com.baks.constants.TestDataConstants.TEST;
import static com.baks.constants.TestDataConstants.TEST_DOCUMENT_CATEGORY_CODE;
import static com.baks.constants.TestDataConstants.USER_ID_VALUE;

import com.baks.enums.TestDocumentType;
import com.baks.pojo.response.DocumentDetailsObject;
import com.baks.utils.DateUtil;
import com.baks.utils.TestDataUtil;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.springframework.stereotype.Component;

/**
 * Step layer for Create document actions.
 */
@Component
public class CreateDocumentSteps extends BaseDocumentSteps {

  @Step("Send POST request to upload PDF document without optional document parameters")
  public Response uploadPdfDocumentWithoutOptionalParameters() {
    return documentPlatformService.uploadPdfDocumentWithPredefinedMandatoryData();
  }

  @Step("Send POST request to upload MS Word document without optional document parameters")
  public Response uploadWordDocumentWithoutOptionalParameters() {
    return documentPlatformService.uploadDocumentByType(TestDocumentType.WORD);
  }

  @Step("Send POST request to upload MS Excel document without optional document parameters")
  public Response uploadExcelDocumentWithoutOptionalParameters() {
    return documentPlatformService.uploadDocumentByType(TestDocumentType.EXCEL);
  }

  @Step("Send POST request to upload JPEG Image document without optional document parameters")
  public Response uploadJpgDocumentWithoutOptionalParameters() {
    return documentPlatformService.uploadDocumentByType(TestDocumentType.JPEG);
  }

  @Step("Send POST request to upload PNG Image document without optional document parameters")
  public Response uploadPngDocumentWithoutOptionalParameters() {
    return documentPlatformService.uploadDocumentByType(TestDocumentType.PNG);
  }

  @Step("Send POST request to upload PDF document with specified document title")
  public Response uploadPdfDocumentWithSpecifiedDocumentTitle(String documentTitle) {
    return documentPlatformService.uploadPdfDocumentWithSpecifiedParameters(
        getRequestHeadersWithMandatoryMeta(Map.of(DOCUMENT_TITLE_HEADER, documentTitle)));
  }

  @Step("Send POST request to upload PDF document with specified file name")
  public Response uploadPdfDocumentWithSpecifiedFileName(String fileName) {
    return documentPlatformService.uploadPdfDocumentWithSpecifiedParameters(
        getRequestHeadersWithMandatoryMeta(TestDataUtil.createContentDispositionHeader(fileName)));
  }

  @Step("Send POST request to upload PDF document with specified lock status")
  public Response uploadPdfDocumentWithSpecifiedLockStatus(String lockStatus) {
    return documentPlatformService.uploadPdfDocumentWithSpecifiedParameters(
        getRequestHeadersWithMandatoryMeta(Map.of(LOCK_STATUS_HEADER, lockStatus)));
  }

  @Step("Sends POST request to upload PDF document with specified expiration date")
  public Response uploadPdfDocumentWithSpecifiedExpirationDate(String expirationDate) {
    return documentPlatformService.uploadPdfDocumentWithSpecifiedParameters(
        getRequestHeadersWithMandatoryMeta(Map.of(EXPIRES_AT_HEADER, expirationDate)));
  }

  @Step("Send POST request to upload PDF document with specified mandatory metadata")
  public Response uploadPdfDocumentWithSpecifiedMandatoryMetaData(Map<String, Object> metaData) {
    return documentPlatformService.uploadPdfDocumentWithSpecifiedParameters(
        TestDataUtil.createMetaDataHeader(metaData));
  }

  @Step("Send POST request to upload PDF document with specified non mandatory metadata")
  public Response uploadPdfDocumentWithSpecifiedNonMandatoryMetaData(Map<String, Object> metaData) {
    return documentPlatformService.uploadPdfDocumentWithSpecifiedParameters(
        getRequestHeadersWithMandatoryMeta(TestDataUtil.createMetaDataHeader(metaData)));
  }

  @Step("Send POST request to upload specified document without optional document parameters")
  public Response uploadDocumentByType(TestDocumentType testDocument) {
    return documentPlatformService.uploadDocumentByType(testDocument);
  }

  //------------------------Negative steps ------------------------
  @Step("Send POST request to upload PDF document without mandatory field: Document-Category")
  public Response uploadDocumentWithoutDocumentCategory() {
    return documentPlatformService.uploadPdfDocumentWithoutDocumentCategory();
  }

  @Step("Send POST request to upload PDF document without mandatory field: Document Title")
  public Response uploadDocumentWithoutDocumentTitle() {
    return documentPlatformService.uploadPdfDocumentWithoutDocumentTitle();
  }

  @Step("Send POST request to upload PDF document using non-existent document category")
  public Response uploadDocumentUsingNonexistentDocumentCategory() {
    return documentPlatformService.uploadPdfDocumentWithSpecifiedParameters(
        getRequestHeadersWithMandatoryMeta(Map.of(DOCUMENT_CATEGORY_HEADER, TEST)));
  }

  @Step("Sends POST request to upload PDF document without attachment")
  public Response uploadDocumentWithoutAttach() {
    return documentPlatformService.uploadDocumentWithoutAttach();
  }

  @Step("Send POST request to upload PDF document without mandatory metadata")
  public Response uploadDocumentWithoutMandatoryMetadata() {
    return documentPlatformService.uploadPdfDocumentWithSpecifiedParameters(
        Map.of(LOCK_STATUS_HEADER, LOCK_STATUS_ACTIVE_VALUE));
  }

  @Step("Send POST request to upload PDF document using expired authorization token")
  public Response uploadDocumentWithExpiredAuthorizationToken() {
    return documentPlatformService.uploadPdfDocumentUsingCustomSpecification(
        documentPlatformService.getDpSpecificationWithExpiredToken());
  }

  @Step("Send POST request to upload PDF document using invalid authorization token")
  public Response uploadDocumentWithInvalidAuthorizationToken() {
    return documentPlatformService.uploadPdfDocumentUsingCustomSpecification(
        documentPlatformService.getDpSpecificationWithInvalidAuthorizationToken());
  }

  @Step("Send POST request to upload PDF document with empty authorization token value")
  public Response uploadDocumentWithEmptyAuthorizationToken() {
    return documentPlatformService.uploadPdfDocumentUsingCustomSpecification(
        documentPlatformService.getSpecificationWithEmptyAuthorizationToken());
  }

  @Step("Send POST request to upload PDF document without authorization token")
  public Response uploadDocumentWithoutAuthorizationToken() {
    return documentPlatformService.uploadPdfDocumentUsingCustomSpecification(
        documentPlatformService.getSpecificationWithoutAuthorizationToken());
  }

  @Step("Send POST request to upload PDF document using invalid User id header")
  public Response uploadDocumentWithInvalidUserId() {
    return documentPlatformService.uploadPdfDocumentUsingCustomSpecification(
        documentPlatformService.getSpecificationWithInvalidUserId());
  }

  @Step("Send POST request to upload PDF document using empty User id header value")
  public Response uploadDocumentWithEmptyUserId() {
    return documentPlatformService.uploadPdfDocumentUsingCustomSpecification(
        documentPlatformService.getSpecificationWithEmptyUserId());
  }

  @Step("Sends POST request to upload PDF document without User id header")
  public Response uploadDocumentWithoutUserId() {
    return documentPlatformService.uploadPdfDocumentUsingCustomSpecification(
        documentPlatformService.getSpecificationWithoutUserId());
  }

  @Step("Send POST request to upload PDF document using nonexistent User id header value")
  public Response uploadDocumentWithNonexistentUserId(String userId) {
    return documentPlatformService.uploadPdfDocumentUsingCustomSpecification(
        documentPlatformService.getAuthorizedDpSpecificationForUser(userId));
  }

  @Step("Send POST request to upload PDF document using nonexistent endpoint")
  public Response uploadDocumentUsingNonexistentEndpoint() {
    return documentPlatformService.uploadPdfDocumentUsingNonexistentEndpoint();
  }

  @Step("Send POST request to upload PDF using unsupported 'Accept' header")
  public Response uploadDocumentUsingUnsupportedAcceptHeader() {
    return documentPlatformService.uploadPdfDocumentUsingCustomSpecification(
        documentPlatformService.getSpecificationWithUnsupportedAcceptHeader());
  }

  public DocumentDetailsObject getCreatedDocumentDetailsObject(Response documentCreationResponse) {
    String documentId = getDocumentIdFromCreationResponse(documentCreationResponse);
    return getDocumentDetailsObjectByDocumentId(documentId);
  }

  //------------------------Verification steps ------------------------

  @Step("Check document creation response")
  public void checkCreatedDocumentResponse(Response documentCreationResponse) {
    checkResponseStatusCodeIs201(documentCreationResponse, "Upload document response code check");
    checkDocumentCreationResponseData(documentCreationResponse);
  }

  @Step("Check document creation response data")
  private void checkDocumentCreationResponseData(Response documentCreationResponse) {
    List<String> requiredValues = List.of("documentId", "detailsUri", "downloadUri");
    for (String requiredValue : requiredValues) {
      Assertions.assertThat(documentCreationResponse.getBody().jsonPath().getString(requiredValue))
          .as("Document creation response does not contains '%s'", requiredValue)
          .isNotEmpty();
    }
  }

  @Step("Check default values in Document Details")
  public void checkDefaultValuesInDocumentDetails(DocumentDetailsObject documentDetailsObject) {
    SoftAssertions softAssertions = new SoftAssertions();
    checkDocumentDetailsUserData(documentDetailsObject, softAssertions);
    checkDocumentCategory(documentDetailsObject.getDocumentCategoryCode(), softAssertions);
    checkDocumentLockStatusIsActive(documentDetailsObject.getLockStatus(), softAssertions);
    checkDocumentDefaultFileName(documentDetailsObject.getFileName(), softAssertions);
    checkDocumentIsNotDeleted(documentDetailsObject.isDeleted(), softAssertions);
    checkDocumentDownloadUrl(documentDetailsObject.getDownloadUrl(),
        documentDetailsObject.getDocumentId(),
        softAssertions);
    softAssertions.assertAll();
  }

  @Step("[Check Document Details] Created date")
  public void checkDocumentDetailsCreatedDate(String documentCreatedDate) {
    Assertions.assertThat(DateUtil.getFormattedDateFromString(documentCreatedDate))
        .as("[Check Default Document Details] createdAt")
        .isEqualTo(DateUtil.getCurrentFormattedDate());
  }

  @Step("[Check Document Details] Updated date")
  public void checkDocumentDetailsUpdatedDate(String documentUpdatedDate) {
    Assertions.assertThat(DateUtil.getFormattedDateFromString(documentUpdatedDate))
        .as("[Check Default Document Details] updatedAt")
        .isEqualTo(DateUtil.getCurrentFormattedDate());
  }

  @Step("[Check Default Document Details] Created And Updated user values")
  private void checkDocumentDetailsUserData(DocumentDetailsObject documentDetailsObject,
      SoftAssertions softAssertions) {
    Map<String, String> ownerData = Map.of(
        "createdBy", documentDetailsObject.getCreatedBy(),
        "updatedBy", documentDetailsObject.getUpdatedBy());
    for (var entry : ownerData.entrySet()) {
      softAssertions.assertThat(entry.getValue())
          .as("[Check Default Document Details] %s", entry.getKey())
          .isEqualTo(USER_ID_VALUE);
    }
  }

  @Step("[Check Default Document Details] Document Category")
  private void checkDocumentCategory(String documentCategory,
      SoftAssertions softAssertions) {
    softAssertions.assertThat(documentCategory)
        .as("[Check Default Document Details] Document Category")
        .isEqualTo(TEST_DOCUMENT_CATEGORY_CODE);
  }

  @Step("[Check Default Document Details] Lock Status")
  private void checkDocumentLockStatusIsActive(String lockStatus, SoftAssertions softAssertions) {
    softAssertions.assertThat(lockStatus)
        .as("[Check Default Document Details] Lock Status")
        .isEqualTo(LOCK_STATUS_ACTIVE_VALUE);
  }

  @Step("[Check Default Document Details] File name")
  private void checkDocumentDefaultFileName(String fileName, SoftAssertions softAssertions) {
    softAssertions.assertThat(fileName)
        .as("[Check Default Document Details] File name")
        .isEqualTo("Document"); // 'Document' is the default value
  }

  @Step("[Check Default Document Details] is Deleted statement")
  private void checkDocumentIsNotDeleted(boolean isDeleted, SoftAssertions softAssertions) {
    softAssertions.assertThat(isDeleted)
        .as("[Check Default Document Details] is Deleted")
        .isFalse();
  }

  @Step("[Check Default Document Details] Download Url")
  private void checkDocumentDownloadUrl(String downloadUrl, String documentId,
      SoftAssertions softAssertions) {
    String expectedUrl = String.format("/v1/documents/%s/download",
        documentId);
    softAssertions.assertThat(downloadUrl)
        .as("[Check Default Document Details] Download Url")
        .isEqualTo(expectedUrl);
  }

  @Step("Check uploaded document size in the document details equals to uploaded document")
  public void checkUploadedDocumentSize(int uploadedDocumentSize, TestDocumentType testDocument) {
    int expectedDocumentLength = TestDataUtil.getTestDocumentSize(testDocument.getType());
    Assertions.assertThat(uploadedDocumentSize)
        .as("Check uploaded document size in the document details equals to uploaded document.")
        .isEqualTo(expectedDocumentLength);
  }
}
