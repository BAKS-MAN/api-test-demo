package com.baks.tests;

import static com.baks.constants.AllureDataConstants.FILENAME_VALIDATOR_FILTER;
import static com.baks.constants.ApiRequestsConstants.LOCK_STATUS_READ_ONLY_VALUE;
import static com.baks.constants.TestDataConstants.TEST;
import static com.baks.utils.TestDataUtil.getRandomString;

import com.baks.enums.MetadataType;
import com.baks.enums.ResponseErrorModel;
import com.baks.enums.TestDocumentType;
import com.baks.pojo.response.DocumentDetailsObject;
import com.baks.runners.EndpointTestRunner;
import com.baks.steps.CreateDocumentSteps;
import com.baks.utils.DateUtil;
import com.baks.utils.TestDataUtil;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;

@Execution(ExecutionMode.CONCURRENT)
@Epic("Document CRUD operations")
@Feature("Create/Upload document API")
@DisplayName("Create/Upload document API tests")
class CreateDocumentTest extends EndpointTestRunner {

  @Autowired
  private CreateDocumentSteps createDocumentSteps;

  @BeforeAll
  static void prepareDocumentCategory(@Autowired CreateDocumentSteps createDocumentSteps) {
    createDocumentSteps.prepareUserForTests();
  }

  @Test
  @DisplayName("Upload PDF document with default Document Details check")
  void uploadPdfDocumentTest() {
    Response uploadResponse = createDocumentSteps.uploadPdfDocumentWithoutOptionalParameters();
    createDocumentSteps.checkCreatedDocumentResponse(uploadResponse);
    String documentId = createDocumentSteps.getDocumentIdFromCreationResponse(uploadResponse);
    createDocumentSteps.waitForAntivirusScanToBeCompleted(documentId);
    DocumentDetailsObject documentDetails =
        createDocumentSteps.getDocumentDetailsObjectByDocumentId(documentId);
    createDocumentSteps.checkUploadedDocumentSize(documentDetails.getSize(), TestDocumentType.PDF);
    createDocumentSteps.checkDefaultValuesInDocumentDetails(documentDetails);
    createDocumentSteps.checkDocumentDetailsCreatedDate(documentDetails.getCreatedAt());
  }

  @Test
  @DisplayName("Upload MS Word document")
  void uploadWordDocumentTest() {
    Response uploadResponse = createDocumentSteps.uploadWordDocumentWithoutOptionalParameters();
    createDocumentSteps.checkCreatedDocumentResponse(uploadResponse);
  }

  @Test
  @DisplayName("Upload MS Excel document")
  void uploadExcelDocumentTest() {
    Response uploadResponse = createDocumentSteps.uploadExcelDocumentWithoutOptionalParameters();
    createDocumentSteps.checkCreatedDocumentResponse(uploadResponse);
  }

  @Test
  @DisplayName("Upload JPEG Image document")
  void uploadJpgDocumentTest() {
    Response uploadResponse = createDocumentSteps.uploadJpgDocumentWithoutOptionalParameters();
    createDocumentSteps.checkCreatedDocumentResponse(uploadResponse);
  }

  @Test
  @DisplayName("Upload PNG Image document")
  void uploadPngDocumentTest() {
    Response uploadResponse = createDocumentSteps.uploadPngDocumentWithoutOptionalParameters();
    createDocumentSteps.checkCreatedDocumentResponse(uploadResponse);
  }

  //------------------------Mandatory parameters verification----------------------------
  @Test
  @DisplayName("Upload PDF document with specified document title")
  void uploadPdfDocumentWithSpecifiedDocumentTitleTest() {
    String documentTitle = TestDataUtil.getRandomString(10);
    Response uploadResponse =
        createDocumentSteps.uploadPdfDocumentWithSpecifiedDocumentTitle(documentTitle);
    createDocumentSteps.checkCreatedDocumentResponse(uploadResponse);

    createDocumentSteps.actionWithDelay(() -> {
      DocumentDetailsObject documentDetails = createDocumentSteps.getCreatedDocumentDetailsObject(
          uploadResponse);
      createDocumentSteps.checkDocumentTitleInDocumentDetails(documentDetails, documentTitle);
    });
  }

  //------------------------Not mandatory parameters verification------------------------
  @Test
  @DisplayName("Upload PDF document with specified file name")
  void uploadPdfDocumentWithSpecifiedFileNameTest() {
    String fileName = "TestDocument_" + getRandomString(5);
    Response uploadResponse =
        createDocumentSteps.uploadPdfDocumentWithSpecifiedFileName(fileName);
    createDocumentSteps.checkCreatedDocumentResponse(uploadResponse);

    createDocumentSteps.actionWithDelay(() -> {
      DocumentDetailsObject documentDetails = createDocumentSteps.getCreatedDocumentDetailsObject(
          uploadResponse);
      createDocumentSteps.checkFileNameInDocumentDetails(documentDetails, fileName);
    });
  }

  @Test
  @DisplayName("Upload PDF document with specified lock status")
  void uploadPdfDocumentWithSpecifiedLockStatusTest() {
    String lockStatus = LOCK_STATUS_READ_ONLY_VALUE;
    Response uploadResponse =
        createDocumentSteps.uploadPdfDocumentWithSpecifiedLockStatus(lockStatus);
    createDocumentSteps.checkCreatedDocumentResponse(uploadResponse);

    createDocumentSteps.actionWithDelay(() -> {
      DocumentDetailsObject documentDetails = createDocumentSteps.getCreatedDocumentDetailsObject(
          uploadResponse);
      createDocumentSteps.checkLockStatusInDocumentDetails(documentDetails, lockStatus);
    });
  }

  @Test
  @DisplayName("Upload PDF document with specified expiration date in 'yyyy-MM-dd' format")
  void uploadPdfDocumentWithSpecifiedExpirationDateTest() {
    String expirationDate = DateUtil.getCurrentFormattedDatePlusDays(1);
    Response uploadResponse =
        createDocumentSteps.uploadPdfDocumentWithSpecifiedExpirationDate(expirationDate);
    createDocumentSteps.checkCreatedDocumentResponse(uploadResponse);

    createDocumentSteps.actionWithDelay(() -> {
      DocumentDetailsObject documentDetails =
          createDocumentSteps.getCreatedDocumentDetailsObject(uploadResponse);
      createDocumentSteps.checkExpirationDateInDocumentDetails(documentDetails, expirationDate);
    });
  }

  @Test
  @DisplayName("Upload PDF document with specified mandatory Metadata")
  void uploadPdfDocumentWithSpecifiedMandatoryMetaDataTest() {
    createDocumentSteps.makeSureMandatoryMetadataExist();
    Map<String, Object> metaData = createDocumentSteps.generateMandatoryMetadataValues();
    Response uploadResponse =
        createDocumentSteps.uploadPdfDocumentWithSpecifiedMandatoryMetaData(metaData);
    createDocumentSteps.checkCreatedDocumentResponse(uploadResponse);
    createDocumentSteps.actionWithDelay(() -> {
      DocumentDetailsObject documentDetails =
          createDocumentSteps.getCreatedDocumentDetailsObject(uploadResponse);
      createDocumentSteps.checkMetadataInDocumentDetails(documentDetails, metaData);
    });
  }

  @Test
  @DisplayName("Upload PDF document with specified non mandatory Metadata")
  void uploadPdfDocumentWithSpecifiedNonMandatoryMetaDataTest() {
    createDocumentSteps.makeSureNonMandatoryMetadataExist();
    uploadPdfDocumentWithSpecifiedNonMandatoryMetaData(
        createDocumentSteps.generateNonMandatoryMetadataValues());
  }

  @ParameterizedTest
  @DisplayName("Upload PDF document with specified Metadata by type")
  @EnumSource(
      value = MetadataType.class,
      names = {"STRING", "DATE", "DATETIME", "DECIMAL", "INTEGER", "BOOLEAN"})
  void uploadPdfDocumentWithSpecifiedMetaDataByTypeTest(MetadataType metadataType) {
    createDocumentSteps.makeSureMetadataWithTypeExist(metadataType);
    uploadPdfDocumentWithSpecifiedNonMandatoryMetaData(
        createDocumentSteps.generateMetadataValuesByType(metadataType));
  }

  private void uploadPdfDocumentWithSpecifiedNonMandatoryMetaData(Map<String, Object> metaData) {
    Response uploadResponse =
        createDocumentSteps.uploadPdfDocumentWithSpecifiedNonMandatoryMetaData(metaData);
    createDocumentSteps.checkCreatedDocumentResponse(uploadResponse);
    createDocumentSteps.actionWithDelay(() -> {
      DocumentDetailsObject documentDetails =
          createDocumentSteps.getCreatedDocumentDetailsObject(uploadResponse);
      createDocumentSteps.checkMetadataInDocumentDetails(documentDetails, metaData);
    });
  }

  //------------------------Negative tests ------------------------
  @Test
  @DisplayName("Upload PDF document without document category field")
  void uploadPdfDocumentWithoutDocumentCategoryTest() {
    Response uploadResponse = createDocumentSteps.uploadDocumentWithoutDocumentCategory();
    createDocumentSteps.checkResponseStatusCodeIs400(uploadResponse,
        "Upload PDF document without document category field");
    createDocumentSteps.checkResponseErrorMessage(uploadResponse,
        ResponseErrorModel.INVALID_DOCUMENT_CATEGORY);
  }

  @Test
  @DisplayName("Upload PDF document without document title field")
  void uploadPdfDocumentWithoutDocumentTitleTest() {
    Response uploadResponse = createDocumentSteps.uploadDocumentWithoutDocumentTitle();
    createDocumentSteps.checkResponseStatusCodeIs400(uploadResponse,
        "Upload PDF document without document title field");
    createDocumentSteps.checkResponseErrorMessage(uploadResponse,
        ResponseErrorModel.INVALID_DOCUMENT_TITLE);
  }

  @Test
  @DisplayName("Upload PDF document using non-existent document category")
  void uploadPdfDocumentUsingNonexistentDocumentCategoryTest() {
    Response uploadResponse = createDocumentSteps.uploadDocumentUsingNonexistentDocumentCategory();
    createDocumentSteps.checkResponseStatusCodeIs400(uploadResponse,
        "Upload PDF document using non-existent document category");
    createDocumentSteps.checkResponseErrorMessage(uploadResponse,
        ResponseErrorModel.INVALID_DOCUMENT_CATEGORY);
  }

  @Test
  @DisplayName("Upload document request with empty attach")
  void uploadDocumentWithoutAttachTest() {
    Response uploadResponse = createDocumentSteps.uploadDocumentWithoutAttach();
    createDocumentSteps.checkResponseStatusCodeIs400(uploadResponse,
        "Upload document request with empty attach");
    createDocumentSteps.checkResponseErrorMessage(uploadResponse, ResponseErrorModel.FILE_LENGTH);
  }

  @Test
  @DisplayName("Upload PDF document with specified lock status unsupported value")
  void uploadPdfDocumentWithSpecifiedUnsupportedLockStatusValueTest() {
    Response uploadResponse =
        createDocumentSteps.uploadPdfDocumentWithSpecifiedLockStatus(TEST);
    createDocumentSteps.checkResponseStatusCodeIs400(uploadResponse,
        "Upload PDF document with specified lock status unsupported value");
    createDocumentSteps.checkResponseErrorMessage(uploadResponse,
        ResponseErrorModel.INVALID_LOCK_TYPE);
  }

  @Test
  @DisplayName("Upload PDF document with specified expiration date value "
      + "less than Current System Datetime")
  void uploadPdfDocumentWithSpecifiedInvalidExpirationDateTest() {
    String dateInPast = DateUtil.getCurrentIsoDateTimeMinusSpecifiedHours(3);
    Response uploadResponse =
        createDocumentSteps.uploadPdfDocumentWithSpecifiedExpirationDate(dateInPast);
    createDocumentSteps.checkResponseStatusCodeIs400(uploadResponse,
        "Upload PDF document with specified expiration date value "
            + "less than Current System Datetime");
    createDocumentSteps.checkResponseErrorMessage(uploadResponse,
        ResponseErrorModel.EXPIRATION_DATE);
  }

  @Test
  @DisplayName("Upload PDF document with specified expiration date with invalid value")
  void updatePdfDocumentWithSpecifiedExpirationDateUsingInvalidValueTest() {
    Response uploadResponse =
        createDocumentSteps.uploadPdfDocumentWithSpecifiedExpirationDate(TEST);
    createDocumentSteps.checkResponseStatusCodeIs400(uploadResponse,
        "Upload PDF document with specified expiration date with invalid value");
    createDocumentSteps.checkResponseErrorMessage(uploadResponse,
        ResponseErrorModel.EXPIRATION_DATE);
  }

  @Test
  @DisplayName("Upload document file with specified file name containing illegal characters")
  @Story(FILENAME_VALIDATOR_FILTER)
  void updateDocumentFileWithIllegalFileNameTest() {
    Response uploadResponse = createDocumentSteps.uploadPdfDocumentWithSpecifiedFileName(
        String.format("UploadDocument%s", TestDataUtil.getIllegalFileNameCharacter()));
    createDocumentSteps.checkResponseStatusCodeIs400(uploadResponse,
        "Upload document file with specified file name containing illegal characters");
    createDocumentSteps.checkResponseErrorMessageWithoutErrorModel(uploadResponse,
        ResponseErrorModel.FILE_NAME);
  }

  @Test
  @DisplayName("Upload document without mandatory Metadata")
  void uploadDocumentWithoutMandatoryMetaDataTest() {
    List<String> mandatoryMetadataKeys = createDocumentSteps.getMandatoryMetadataKeys();
    Assumptions.assumeThat(mandatoryMetadataKeys)
        .as("Test is skipped: list of mandatory metadata keys is empty")
        .isNotEmpty();
    Response uploadResponse = createDocumentSteps.uploadDocumentWithoutMandatoryMetadata();
    createDocumentSteps.checkResponseStatusCodeIs400(uploadResponse,
        "Upload document without mandatory Metadata");
    createDocumentSteps.checkMissingMandatoryMetadataErrorMessage(
        uploadResponse, mandatoryMetadataKeys);
  }

  @Test
  @DisplayName("Upload document with nonexistent Metadata key")
  void uploadDocumentWithUnregisteredMetaDataKeyTest() {
    Response uploadResponse = createDocumentSteps
        .uploadPdfDocumentWithSpecifiedNonMandatoryMetaData(
            Map.of(TEST, "Test value"));
    createDocumentSteps.checkResponseStatusCodeIs400(uploadResponse,
        "Upload document with nonexistent Metadata key");
    createDocumentSteps.checkNotDefinedMetadataKeyErrorMessage(uploadResponse, TEST);
  }

  @ParameterizedTest
  @DisplayName("Upload PDF document with incompatible Metadata value by type")
  @EnumSource(
      value = MetadataType.class,
      names = {"DATE", "DATETIME", "DECIMAL", "INTEGER", "BOOLEAN"})
  void uploadPdfDocumentWithIncompatibleMetaDataValueByTypeTest(MetadataType metadataType) {
    createDocumentSteps.makeSureMetadataWithTypeExist(metadataType);
    String metadataKey = createDocumentSteps.getMetadataKeyByType(metadataType);
    Map<String, Object> metaData = Map.of(metadataKey, TEST);
    Response uploadResponse =
        createDocumentSteps.uploadPdfDocumentWithSpecifiedNonMandatoryMetaData(metaData);
    createDocumentSteps.checkResponseStatusCodeIs400(uploadResponse,
        "Upload PDF document with incompatible Metadata value by type");
    createDocumentSteps.checkInvalidMetadataTypeErrorMessage(uploadResponse, metadataKey);
  }

  @Test
  @DisplayName("Upload PDF document using nonexistent endpoint")
  void uploadPdfDocumentUsingNonexistentEndpointTest() {
    Response uploadResponse = createDocumentSteps.uploadDocumentUsingNonexistentEndpoint();
    createDocumentSteps.checkResponseStatusCodeIs404(uploadResponse,
        "Upload PDF document using nonexistent endpoint");
    createDocumentSteps.checkResponseErrorMessageWithoutErrorModel(uploadResponse,
        ResponseErrorModel.NOT_FOUND);
  }

  @Test
  @DisplayName("Upload document using expired authorization token")
  void uploadDocumentWithExpiredAuthorizationTokenTest() {
    Response uploadResponse = createDocumentSteps.uploadDocumentWithExpiredAuthorizationToken();
    createDocumentSteps.checkResponseStatusCodeIs401(uploadResponse,
        "Upload document using expired authorization token");
  }

  @Test
  @DisplayName("Upload document using invalid authorization token")
  void uploadDocumentWithInvalidAuthorizationTokenTest() {
    Response uploadResponse = createDocumentSteps.uploadDocumentWithInvalidAuthorizationToken();
    createDocumentSteps.checkResponseStatusCodeIs401(uploadResponse,
        "Upload document using invalid authorization token");
  }

  @Test
  @DisplayName("Upload document using empty authorization token value")
  void uploadDocumentWithEmptyAuthorizationTokenTest() {
    Response uploadResponse = createDocumentSteps.uploadDocumentWithEmptyAuthorizationToken();
    createDocumentSteps.checkResponseStatusCodeIs401(uploadResponse,
        "Upload document using empty authorization token");
  }

  @Test
  @DisplayName("Upload document without authorization token value")
  void uploadDocumentWithoutAuthorizationTokenTest() {
    Response uploadResponse = createDocumentSteps.uploadDocumentWithoutAuthorizationToken();
    createDocumentSteps.checkResponseStatusCodeIs401(uploadResponse,
        "Upload document without authorization token");
  }

  @Test
  @DisplayName("Upload document using invalid User Id")
  void uploadDocumentWithInvalidUserIdTest() {
    Response uploadResponse = createDocumentSteps.uploadDocumentWithInvalidUserId();
    createDocumentSteps.checkResponseStatusCodeIs400(uploadResponse,
        "Upload document using invalid User Id");
    createDocumentSteps.checkUserIdResponseErrorMessage(uploadResponse);
  }

  @Test
  @DisplayName("Upload document with empty User Id")
  void uploadDocumentWithEmptyUserIdTest() {
    Response uploadResponse = createDocumentSteps.uploadDocumentWithEmptyUserId();
    createDocumentSteps.checkResponseStatusCodeIs400(uploadResponse,
        "Upload document with empty User Id");
    createDocumentSteps.checkUserIdResponseErrorMessage(uploadResponse);
  }

  @Test
  @DisplayName("Upload document without User Id")
  void uploadDocumentWithoutUserIdTest() {
    Response uploadResponse = createDocumentSteps.uploadDocumentWithoutUserId();
    createDocumentSteps.checkResponseStatusCodeIs400(uploadResponse,
        "Upload document without User Id");
    createDocumentSteps.checkUserIdResponseErrorMessage(uploadResponse);
  }

  @Test
  @DisplayName("Upload document using nonexistent User Id")
  void uploadDocumentWithIncorrectUserIdTest() {
    String userId = TestDataUtil.generateUuid();
    Response uploadResponse = createDocumentSteps.uploadDocumentWithNonexistentUserId(userId);
    createDocumentSteps.checkResponseStatusCodeIs400(uploadResponse,
        "Upload document using Nonexistent User Id");
    createDocumentSteps.checkResponseErrorMessageByModelStateKey(uploadResponse,
        ResponseErrorModel.USER_NOT_REGISTERED, userId);
  }

  @Test
  @DisplayName("Upload document using unsupported 'Accept' header value")
  void uploadDocumentWithInvalidAcceptHeaderTest() {
    Response uploadResponse =
        createDocumentSteps.uploadDocumentUsingUnsupportedAcceptHeader();
    createDocumentSteps.checkResponseStatusCodeIs406(uploadResponse,
        "Upload document using unsupported 'Accept' header value");
    createDocumentSteps.checkResponseErrorMessageWithoutErrorModel(uploadResponse,
        ResponseErrorModel.NOT_ACCEPTABLE);
  }
}
