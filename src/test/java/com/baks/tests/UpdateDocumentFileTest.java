package com.baks.tests;

import static com.baks.constants.AllureDataConstants.FILENAME_VALIDATOR_FILTER;
import static com.baks.constants.ApiRequestsConstants.LOCK_STATUS_READ_ONLY_VALUE;
import static com.baks.constants.TestDataConstants.INVALID_DOCUMENT_ID;
import static com.baks.constants.TestDataConstants.TEST;
import static com.baks.utils.TestDataUtil.getRandomString;

import com.baks.enums.MetadataType;
import com.baks.enums.ResponseErrorModel;
import com.baks.enums.TestDocumentType;
import com.baks.pojo.response.DocumentDetailsObject;
import com.baks.runners.EndpointTestRunner;
import com.baks.steps.CreateDocumentSteps;
import com.baks.steps.HardDeleteDocumentsByIdSteps;
import com.baks.steps.UpdateDocumentFileSteps;
import com.baks.utils.DateUtil;
import com.baks.utils.TestDataUtil;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;

@Epic("Document CRUD operations")
@Feature("Update document API")
@DisplayName("Update document file tests")
class UpdateDocumentFileTest extends EndpointTestRunner {

  @Autowired
  private UpdateDocumentFileSteps updateDocumentFileSteps;
  @Autowired
  private CreateDocumentSteps createDocumentSteps;
  @Autowired
  private HardDeleteDocumentsByIdSteps hardDeleteDocumentsByIdSteps;
  private static final TestDocumentType testDocumentType = TestDocumentType.EXCEL;
  private static String documentId;

  @BeforeAll
  static void initDocumentId(@Autowired UpdateDocumentFileSteps updateDocumentFileSteps) {
    updateDocumentFileSteps.prepareUserForTests();
    documentId = updateDocumentFileSteps.getRandomDocumentIdWithActiveLockStatus();
  }

  @Test
  @DisplayName("Update document file with default Document Details check")
  @Order(1)
  void updateDocumentFileTest() {
    Response updateResponse = updateDocumentFileSteps.updateDocumentFileWithoutSpecifiedData(
        documentId, testDocumentType);
    updateDocumentFileSteps.checkResponseStatusCodeIs204(updateResponse,
        "Update document file with default Document Details check");
    updateDocumentFileSteps.actionWithDelay(() -> {
      DocumentDetailsObject documentDetails =
          updateDocumentFileSteps.getDocumentDetailsObjectByDocumentId(documentId);
      createDocumentSteps.checkUploadedDocumentSize(documentDetails.getSize(), testDocumentType);
      createDocumentSteps.checkDefaultValuesInDocumentDetails(documentDetails);
      createDocumentSteps.checkDocumentDetailsUpdatedDate(documentDetails.getUpdatedAt());
    });
  }

  @Test
  @DisplayName("Update document file with specified mandatory Metadata")
  void updateDocumentFileWithSpecifiedMandatoryMetaDataTest() {
    updateDocumentFileSteps.makeSureMandatoryMetadataExist();
    Map<String, Object> metaData = updateDocumentFileSteps.generateMandatoryMetadataValues();
    Response updateResponse = updateDocumentFileSteps
        .updateDocumentFileWithSpecifiedMandatoryMetaData(documentId, metaData, testDocumentType);
    updateDocumentFileSteps.checkResponseStatusCodeIs204(updateResponse,
        "Update document file with specified mandatory Metadata");

    updateDocumentFileSteps.actionWithDelay(() -> updateDocumentFileSteps
        .checkMetadataInDocumentDetails(updateDocumentFileSteps
            .getDocumentDetailsObjectByDocumentId(documentId), metaData));
  }

  @Test
  @DisplayName("Update document with specified document title")
  void updateDocumentFileWithSpecifiedDocumentTitleTest() {
    String documentTitle = TestDataUtil.getRandomString(10);
    Response updateResponse = updateDocumentFileSteps
        .updateDocumentFileWithSpecifiedDocumentTitle(documentId, documentTitle, testDocumentType);
    updateDocumentFileSteps.checkResponseStatusCodeIs204(updateResponse,
        "Update document file with specified document title");

    updateDocumentFileSteps.actionWithDelay(() ->
        updateDocumentFileSteps.checkDocumentTitleInDocumentDetails(
            updateDocumentFileSteps.getDocumentDetailsObjectByDocumentId(documentId),
            documentTitle));
  }

  //------------------------Not mandatory parameters verification------------------------
  @Test
  @DisplayName("Update document file with specified file name")
  void updateDocumentFileWithSpecifiedFileNameTest() {
    String fileName = "UpdatedDocument_" + getRandomString(5);
    Response updateResponse = updateDocumentFileSteps
        .updateDocumentFileWithSpecifiedFileName(documentId, fileName, testDocumentType);
    updateDocumentFileSteps.checkResponseStatusCodeIs204(updateResponse,
        "Update document file with specified file name");

    updateDocumentFileSteps.actionWithDelay(() ->
        updateDocumentFileSteps.checkFileNameInDocumentDetails(
            updateDocumentFileSteps.getDocumentDetailsObjectByDocumentId(documentId), fileName));
  }

  @Test
  @DisplayName("Update document file with specified lock status")
  void updateDocumentFileWithSpecifiedLockStatusTest() {
    String documentToUpdate = updateDocumentFileSteps.getRandomDocumentIdWithActiveLockStatus();
    String lockStatus = LOCK_STATUS_READ_ONLY_VALUE;
    Response updateResponse = updateDocumentFileSteps
        .updateDocumentFileWithSpecifiedLockStatus(documentToUpdate, lockStatus, testDocumentType);
    updateDocumentFileSteps.checkResponseStatusCodeIs204(updateResponse,
        "Update document file with specified lock status");

    updateDocumentFileSteps.actionWithDelay(() -> updateDocumentFileSteps
        .checkLockStatusInDocumentDetails(updateDocumentFileSteps
            .getDocumentDetailsObjectByDocumentId(documentToUpdate), lockStatus));
  }

  @Test
  @DisplayName("Update document file with specified expiration date")
  void updateDocumentFileWithSpecifiedExpirationDateTest() {
    String expirationDate = DateUtil.getCurrentIsoDateTimePlusSpecifiedHours(1);
    Response updateResponse = updateDocumentFileSteps.updateDocumentFileWithSpecifiedExpirationDate(
        documentId, expirationDate, testDocumentType);
    updateDocumentFileSteps.checkResponseStatusCodeIs204(updateResponse,
        "Update document file with specified expiration date");

    updateDocumentFileSteps.actionWithDelay(() -> updateDocumentFileSteps
        .checkExpirationDateInDocumentDetails(updateDocumentFileSteps
            .getDocumentDetailsObjectByDocumentId(documentId), expirationDate));
  }

  @Test
  @DisplayName("Update document file with specified non mandatory Metadata")
  void updateDocumentFileWithSpecifiedNonMandatoryMetaDataTest() {
    updateDocumentFileSteps.makeSureNonMandatoryMetadataExist();
    updateDocumentFileWithSpecifiedNonMandatoryMetaData(
        updateDocumentFileSteps.generateNonMandatoryMetadataValues());
  }

  @ParameterizedTest
  @DisplayName("Update document file with specified Metadata by type")
  @EnumSource(
      value = MetadataType.class,
      names = {"STRING", "DATE", "DATETIME", "DECIMAL", "INTEGER", "BOOLEAN"})
  void updateDocumentFileWithSpecifiedMetaDataByTypeTest(MetadataType metadataType) {
    updateDocumentFileSteps.makeSureMetadataWithTypeExist(metadataType);
    updateDocumentFileWithSpecifiedNonMandatoryMetaData(
        updateDocumentFileSteps.generateMetadataValuesByType(metadataType));
  }

  private void updateDocumentFileWithSpecifiedNonMandatoryMetaData(Map<String, Object> metaData) {
    Response updateResponse = updateDocumentFileSteps
        .updateDocumentFileWithSpecifiedNonMandatoryMetaData(
            documentId, metaData, testDocumentType);
    updateDocumentFileSteps.checkResponseStatusCodeIs204(updateResponse,
        "Update document file with specified non mandatory Metadata");
    updateDocumentFileSteps.actionWithDelay(() -> updateDocumentFileSteps
        .checkMetadataInDocumentDetails(updateDocumentFileSteps
            .getDocumentDetailsObjectByDocumentId(documentId), metaData));
  }

  @Test
  @DisplayName("Update document file with specified Digest data")
  void updateDocumentFileWithSpecifiedDigestTest() {
    Map<String, String> digestData = TestDataUtil.createDigestData(testDocumentType.getType());
    Response updateResponse = updateDocumentFileSteps
        .updateDocumentFileWithSpecifiedDigest(documentId, digestData, testDocumentType);
    updateDocumentFileSteps.checkResponseStatusCodeIs204(updateResponse,
        "Update document file with specified Digest data");

    updateDocumentFileSteps.actionWithDelay(() -> updateDocumentFileSteps
        .checkDigestDataInDocumentDetails(updateDocumentFileSteps
            .getDocumentDetailsObjectByDocumentId(documentId), digestData));
  }

  //------------------------Negative tests ------------------------
  @Test
  @DisplayName("Update document file without document category")
  void updateDocumentFileWithoutDocumentCategoryTest() {
    Response updateResponse = updateDocumentFileSteps
        .updateDocumentWithoutDocumentCategory(documentId);
    updateDocumentFileSteps.checkResponseStatusCodeIs400(updateResponse,
        "Update document file without document category");
    updateDocumentFileSteps.checkResponseErrorMessage(updateResponse,
        ResponseErrorModel.INVALID_DOCUMENT_CATEGORY);
  }

  @Test
  @DisplayName("Update document file using non-existent document category")
  void updateDocumentFileUsingNonexistentDocumentCategoryTest() {
    Response updateResponse = updateDocumentFileSteps
        .updateDocumentUsingNonexistentDocumentCategory(documentId, testDocumentType);
    updateDocumentFileSteps.checkResponseStatusCodeIs400(updateResponse,
        "Update document file using non-existent document category");
    updateDocumentFileSteps.checkResponseErrorMessage(updateResponse,
        ResponseErrorModel.INVALID_DOCUMENT_CATEGORY);
  }

  @Test
  @DisplayName("Update document file with empty attach")
  void updateDocumentWithoutAttachTest() {
    Response updateResponse = updateDocumentFileSteps.updateDocumentWithoutAttach(documentId);
    updateDocumentFileSteps.checkResponseStatusCodeIs400(updateResponse,
        "Update document file with empty attach");
    updateDocumentFileSteps.checkResponseErrorMessage(updateResponse,
        ResponseErrorModel.FILE_LENGTH);
  }

  @Test
  @DisplayName("Update document file using empty document title")
  void updateDocumentFileWithEmptyDocumentTitleTest() {
    Response updateResponse = updateDocumentFileSteps
        .updateDocumentFileWithSpecifiedDocumentTitle(documentId, "", testDocumentType);
    updateDocumentFileSteps.checkResponseStatusCodeIs400(updateResponse,
        "Update document file using empty document title");
    updateDocumentFileSteps.checkResponseErrorMessage(updateResponse,
        ResponseErrorModel.INVALID_DOCUMENT_TITLE);
  }

  @Test
  @DisplayName("Update document file for read only document")
  void updateReadOnlyDocumentFileTest() {
    String lockedDocumentId = updateDocumentFileSteps.getRandomReadOnlyDocumentId();
    Response updateResponse = updateDocumentFileSteps.updateDocumentFileWithoutSpecifiedData(
        lockedDocumentId, testDocumentType);
    updateDocumentFileSteps.checkResponseStatusCodeIs400(updateResponse,
        "Update document file for read only document");
    updateDocumentFileSteps.checkResponseErrorMessage(updateResponse,
        ResponseErrorModel.READ_ONLY_DOCUMENT);
  }

  @Test
  @DisplayName("Update document file with specified file name containing illegal characters")
  @Story(FILENAME_VALIDATOR_FILTER)
  void updateDocumentFileWithIllegalFileNameTest() {
    Response updateResponse = updateDocumentFileSteps
        .updateDocumentFileWithSpecifiedFileName(documentId, String.format("UpdatedDocument%s",
            TestDataUtil.getIllegalFileNameCharacter()), testDocumentType);
    updateDocumentFileSteps.checkResponseStatusCodeIs400(updateResponse,
        "Update document file with specified file name containing illegal characters");
    updateDocumentFileSteps.checkResponseErrorMessageWithoutErrorModel(updateResponse,
        ResponseErrorModel.FILE_NAME);
  }

  @Test
  @DisplayName("Update document file with specified lock status unsupported value")
  void updatePdfDocumentWithSpecifiedUnsupportedLockStatusValueTest() {
    Response updateResponse = updateDocumentFileSteps.updateDocumentFileWithSpecifiedLockStatus(
        documentId, TEST, testDocumentType);
    updateDocumentFileSteps.checkResponseStatusCodeIs400(updateResponse,
        "Update document file with specified lock status unsupported value");
    updateDocumentFileSteps.checkResponseErrorMessage(updateResponse,
        ResponseErrorModel.INVALID_LOCK_TYPE);
  }

  @Test
  @DisplayName("Update document file with specified expiration date value "
      + "less than Current System Datetime")
  void updatePdfDocumentWithSpecifiedInvalidExpirationDateTest() {
    String dateInPast = DateUtil.getCurrentIsoDateTimeMinusSpecifiedHours(5);
    Response updateResponse = updateDocumentFileSteps.updateDocumentFileWithSpecifiedExpirationDate(
        documentId, dateInPast, testDocumentType);
    updateDocumentFileSteps.checkResponseStatusCodeIs400(updateResponse,
        "Update document file with specified expiration date value "
            + "less than Current System Datetime");
    updateDocumentFileSteps.checkResponseErrorMessage(updateResponse,
        ResponseErrorModel.EXPIRATION_DATE);
  }

  @Test
  @DisplayName("Update document file with specified expiration date with invalid value")
  void updatePdfDocumentWithSpecifiedExpirationDateUsingInvalidValueTest() {
    Response updateResponse = updateDocumentFileSteps.updateDocumentFileWithSpecifiedExpirationDate(
        documentId, TEST, testDocumentType);
    updateDocumentFileSteps.checkResponseStatusCodeIs400(updateResponse,
        "Update document file with specified expiration date with invalid value");
    updateDocumentFileSteps.checkResponseErrorMessage(updateResponse,
        ResponseErrorModel.EXPIRATION_DATE);
  }

  @Test
  @DisplayName("Update document file without mandatory Metadata")
  void updateDocumentFileWithoutMandatoryMetaDataTest() {
    updateDocumentFileSteps.makeSureMandatoryMetadataExist();
    Response updateResponse = updateDocumentFileSteps.updateDocumentWithoutMandatoryMetadata(
        documentId, testDocumentType);
    updateDocumentFileSteps.checkResponseStatusCodeIs400(updateResponse,
        "Update document file without mandatory Metadata");
    updateDocumentFileSteps.checkMissingMandatoryMetadataErrorMessage(
        updateResponse, updateDocumentFileSteps.getMandatoryMetadataKeys());
  }

  @Test
  @DisplayName("Update document file with nonexistent Metadata key")
  void updateDocumentFileWithNonexistentMetaDataKeyTest() {
    Response updateResponse =
        updateDocumentFileSteps.updateDocumentFileWithSpecifiedNonMandatoryMetaData(
            documentId, Map.of(TEST, "Test value"), testDocumentType);
    updateDocumentFileSteps.checkResponseStatusCodeIs400(updateResponse,
        "Update document file with nonexistent Metadata key");
    updateDocumentFileSteps.checkNotDefinedMetadataKeyErrorMessage(updateResponse, TEST);
  }

  @ParameterizedTest
  @DisplayName("Update document file with incompatible Metadata value by type")
  @EnumSource(
      value = MetadataType.class,
      names = {"DATE", "DATETIME", "DECIMAL", "INTEGER", "BOOLEAN"})
  void updateDocumentFileWithIncompatibleMetaDataValueByTypeTest(MetadataType metadataType) {
    updateDocumentFileSteps.makeSureMetadataWithTypeExist(metadataType);
    String metadataKey = updateDocumentFileSteps.getMetadataKeyByType(metadataType);
    Map<String, Object> metaData = Map.of(metadataKey, TEST);
    Response updateResponse =
        updateDocumentFileSteps.updateDocumentFileWithSpecifiedNonMandatoryMetaData(
            documentId, metaData, testDocumentType);
    updateDocumentFileSteps.checkResponseStatusCodeIs400(updateResponse,
        "Update document file with incompatible Metadata value by type");
    updateDocumentFileSteps.checkInvalidMetadataTypeErrorMessage(updateResponse, metadataKey);
  }

  @Test
  @DisplayName("Update document file using invalid document Id")
  void updateDocumentFileUsingInvalidDocumentIdTest() {
    Response updateResponse = updateDocumentFileSteps.updateDocumentFileWithoutSpecifiedData(
        INVALID_DOCUMENT_ID, testDocumentType);
    updateDocumentFileSteps.checkResponseStatusCodeIs400(updateResponse,
        "Update document file using invalid document Id");
    updateDocumentFileSteps.checkResponseErrorMessage(updateResponse,
        ResponseErrorModel.NON_EXISTENT_DOCUMENT);
  }

  @Test
  @DisplayName("Update document file using hard deleted document Id")
  void updateDocumentFileUsingHardDeletedDocumentIdTest() {
    Response updateResponse = updateDocumentFileSteps.updateDocumentFileWithoutSpecifiedData(
        hardDeleteDocumentsByIdSteps.generateHardDeletedDocumentForTest(), testDocumentType);
    updateDocumentFileSteps.checkResponseStatusCodeIs400(updateResponse,
        "Update document file using hard deleted document Id");
    updateDocumentFileSteps.checkResponseErrorMessage(updateResponse,
        ResponseErrorModel.NON_EXISTENT_DOCUMENT);
  }

  @Test
  @DisplayName("Update document file using expired authorization token")
  void updateDocumentWithExpiredAuthorizationTokenTest() {
    Response updateResponse =
        updateDocumentFileSteps.updateDocumentFileUsingExpiredAuthorizationToken(documentId);
    updateDocumentFileSteps.checkResponseStatusCodeIs401(updateResponse,
        "Update document file using expired authorization token");
  }

  @Test
  @DisplayName("Update document file using invalid authorization token")
  void updateDocumentWithInvalidAuthorizationTokenTest() {
    Response updateResponse =
        updateDocumentFileSteps.updateDocumentFileUsingInvalidAuthorizationToken(documentId);
    updateDocumentFileSteps.checkResponseStatusCodeIs401(updateResponse,
        "Update document file using invalid authorization token");
  }

  @Test
  @DisplayName("Update document file using empty authorization token value")
  void updateDocumentWithEmptyAuthorizationTokenTest() {
    Response updateResponse =
        updateDocumentFileSteps.updateDocumentFileUsingEmptyAuthorizationToken(documentId);
    updateDocumentFileSteps.checkResponseStatusCodeIs401(updateResponse,
        "Update document file using empty authorization token");
  }

  @Test
  @DisplayName("Update document file without authorization token value")
  void updateDocumentWithoutAuthorizationTokenTest() {
    Response updateResponse =
        updateDocumentFileSteps.updateDocumentFileWithoutAuthorizationToken(documentId);
    updateDocumentFileSteps.checkResponseStatusCodeIs401(updateResponse,
        "Update document file without authorization token");
  }

  @Test
  @DisplayName("Update document file using invalid User Id")
  void updateDocumentWithInvalidUserIdTest() {
    Response updateResponse =
        updateDocumentFileSteps.updateDocumentFileUsingInvalidUserId(documentId);
    updateDocumentFileSteps.checkResponseStatusCodeIs400(updateResponse,
        "Update document file using invalid User Id");
    updateDocumentFileSteps.checkUserIdResponseErrorMessage(updateResponse);
  }

  @Test
  @DisplayName("Update document file with empty User Id")
  void updateDocumentWithEmptyUserIdTest() {
    Response updateResponse =
        updateDocumentFileSteps.updateDocumentFileUsingEmptyUserId(documentId);
    updateDocumentFileSteps.checkResponseStatusCodeIs400(updateResponse,
        "Update document file with empty User Id");
    updateDocumentFileSteps.checkUserIdResponseErrorMessage(updateResponse);
  }

  @Test
  @DisplayName("Update document file without User Id")
  void updateDocumentWithoutUserIdTest() {
    Response updateResponse = updateDocumentFileSteps.updateDocumentFileWithoutUserId(documentId);
    updateDocumentFileSteps.checkResponseStatusCodeIs400(updateResponse,
        "Update document file without User Id");
    updateDocumentFileSteps.checkUserIdResponseErrorMessage(updateResponse);
  }
}
