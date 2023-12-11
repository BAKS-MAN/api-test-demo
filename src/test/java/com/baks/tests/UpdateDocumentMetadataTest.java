package com.baks.tests;

import static com.baks.constants.TestDataConstants.INVALID_DOCUMENT_ID;
import static com.baks.constants.TestDataConstants.TEST;

import com.baks.enums.MetadataType;
import com.baks.enums.ResponseErrorModel;
import com.baks.pojo.response.DocumentDetailsObject;
import com.baks.runners.EndpointTestRunner;
import com.baks.steps.CreateDocumentSteps;
import com.baks.steps.HardDeleteDocumentsByIdSteps;
import com.baks.steps.UpdateDocumentMetaDataSteps;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
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
@DisplayName("Update document metadata tests")
class UpdateDocumentMetadataTest extends EndpointTestRunner {

  @Autowired
  private UpdateDocumentMetaDataSteps updateDocumentMetaDataSteps;
  @Autowired
  private HardDeleteDocumentsByIdSteps hardDeleteDocumentsByIdSteps;
  @Autowired
  private CreateDocumentSteps createDocumentSteps;
  private static String documentId;
  private static Map<String, Object> metadata;

  @BeforeAll
  static void prepareTestData(@Autowired UpdateDocumentMetaDataSteps updateDocumentMetaDataSteps) {
    documentId = updateDocumentMetaDataSteps.getRandomDocumentIdWithActiveLockStatus();
    metadata = updateDocumentMetaDataSteps.generateMandatoryMetadataValues();
  }

  @Test
  @DisplayName("Replace document mandatory metadata")
  @Order(1)
  void replaceDocumentMandatoryMetadataTest() {
    updateDocumentMetaDataSteps.makeSureMandatoryMetadataExist();
    Response replaceResponse = updateDocumentMetaDataSteps.replaceDocumentMetadata(
        documentId, metadata);
    updateDocumentMetaDataSteps.checkResponseStatusCodeIs204(replaceResponse,
        "Replace document mandatory metadata");
    updateDocumentMetaDataSteps.actionWithDelay(() -> {
      DocumentDetailsObject documentDetails =
          updateDocumentMetaDataSteps.getDocumentDetailsObjectByDocumentId(documentId);
      updateDocumentMetaDataSteps.checkMetadataInDocumentDetails(documentDetails,
          metadata);
    });
  }

  @Test
  @DisplayName("Replace document non mandatory metadata")
  @Order(2)
  void replaceDocumentNonMandatoryMetadataTest() {
    updateDocumentMetaDataSteps.makeSureNonMandatoryMetadataExist();
    Response replaceResponse = updateDocumentMetaDataSteps.replaceDocumentMetadata(
        documentId, metadata);
    updateDocumentMetaDataSteps.checkResponseStatusCodeIs204(replaceResponse,
        "Replace document non mandatory metadata");
    updateDocumentMetaDataSteps.actionWithDelay(() -> {
      DocumentDetailsObject documentDetails =
          updateDocumentMetaDataSteps.getDocumentDetailsObjectByDocumentId(documentId);
      updateDocumentMetaDataSteps.checkMetadataInDocumentDetails(documentDetails,
          metadata);
    });
  }

  @Test
  @DisplayName("Update document mandatory metadata")
  @Order(3)
  void updateDocumentMandatoryMetaDataTest() {
    updateDocumentMetaDataSteps.makeSureMandatoryMetadataExist();
    // Create new document with predefined metadata.
    Map<String, Object> metaDataValues =
        updateDocumentMetaDataSteps.generateMandatoryMetadataValues();
    Response uploadResponse = createDocumentSteps.uploadPdfDocumentWithSpecifiedMandatoryMetaData(
        metaDataValues);
    createDocumentSteps.checkCreatedDocumentResponse(uploadResponse);
    String documentToUpdate = createDocumentSteps.getDocumentIdFromCreationResponse(uploadResponse);
    // Perform update request for created document with new metadata.
    Map<String, Object> newMetadata = updateDocumentMetaDataSteps.generateMandatoryMetadataValues();
    Response updateResponse = updateDocumentMetaDataSteps.updateDocumentMetadata(
        documentToUpdate, newMetadata);
    updateDocumentMetaDataSteps.checkResponseStatusCodeIs204(updateResponse,
        "Update document metadata with specified mandatory metadata");
    // Check the result.
    updateDocumentMetaDataSteps.actionWithDelay(() ->
        updateDocumentMetaDataSteps.checkMetadataInDocumentDetails(
            updateDocumentMetaDataSteps.getDocumentDetailsObjectByDocumentId(documentToUpdate),
            newMetadata));
  }

  @Test
  @DisplayName("Update document metadata with specified non mandatory metadata")
  @Order(4)
  void updateDocumentNonMandatoryMetaDataTest() {
    updateDocumentMetaDataSteps.makeSureNonMandatoryMetadataExist();
    updateDocumentNonMandatoryMetaData(
        updateDocumentMetaDataSteps.generateNonMandatoryMetadataValues());
  }

  @ParameterizedTest
  @DisplayName("Update document metadata with specified Metadata by type")
  @EnumSource(
      value = MetadataType.class,
      names = {"STRING", "DATE", "DATETIME", "DECIMAL", "INTEGER", "BOOLEAN"})
  void updateDocumentMetaDataWithSpecifiedMetaDataByTypeTest(MetadataType metadataType) {
    updateDocumentMetaDataSteps.makeSureMetadataWithTypeExist(metadataType);
    updateDocumentNonMandatoryMetaData(
        updateDocumentMetaDataSteps.generateMetadataValuesByType(metadataType));
  }

  private void updateDocumentNonMandatoryMetaData(Map<String, Object> newMetadata) {
    updateDocumentMetaDataSteps.makeSureNonMandatoryMetadataExist();
    // Create new document with predefined metadata.
    Response uploadResponse = createDocumentSteps.uploadPdfDocumentWithSpecifiedMandatoryMetaData(
        metadata);
    createDocumentSteps.checkCreatedDocumentResponse(uploadResponse);
    String documentToUpdate = createDocumentSteps.getDocumentIdFromCreationResponse(uploadResponse);
    // Perform update request for created document with new metadata.
    Response updateResponse = updateDocumentMetaDataSteps.updateDocumentMetadata(
        documentToUpdate, newMetadata);
    updateDocumentMetaDataSteps.checkResponseStatusCodeIs204(updateResponse,
        "Update document metadata with specified mandatory metadata");
    // Check the result.
    updateDocumentMetaDataSteps.actionWithDelay(() ->
        updateDocumentMetaDataSteps.checkMetadataInDocumentDetails(
            updateDocumentMetaDataSteps.getDocumentDetailsObjectByDocumentId(documentToUpdate),
            newMetadata));
  }

  //------------------------Negative tests ------------------------

  @Test
  @DisplayName("Replace document metadata without mandatory metadata")
  @Order(1)
  void replaceDocumentMetadataWithoutMandatoryMetadataTest() {
    updateDocumentMetaDataSteps.makeSureMandatoryMetadataExist();
    Response replaceResponse = updateDocumentMetaDataSteps.replaceDocumentMetadata(
        documentId, updateDocumentMetaDataSteps.generateNonMandatoryMetadataValues());
    updateDocumentMetaDataSteps.checkResponseStatusCodeIs400(replaceResponse,
        "Replace document metadata without mandatory metadata");
    updateDocumentMetaDataSteps.checkMissingMandatoryMetadataErrorMessage(
        replaceResponse, updateDocumentMetaDataSteps.getMandatoryMetadataKeys());
  }

  @Test
  @DisplayName("Replace document metadata without metadata header")
  void replaceDocumentMetadataWithoutMetadataHeaderTest() {
    Response replaceResponse =
        updateDocumentMetaDataSteps.replaceDocumentMetadataWithoutMetadataHeader(documentId);
    updateDocumentMetaDataSteps.checkResponseStatusCodeIs400(replaceResponse,
        "Replace document metadata without metadata header");
    updateDocumentMetaDataSteps.checkResponseErrorMessage(replaceResponse,
        ResponseErrorModel.EMPTY_METADATA);
  }

  @Test
  @DisplayName("Replace document metadata for read only document")
  void replaceReadOnlyDocumentMetadataTest() {
    String lockedDocumentId = updateDocumentMetaDataSteps.getRandomReadOnlyDocumentId();
    Response replaceResponse = updateDocumentMetaDataSteps.replaceDocumentMetadata(
        lockedDocumentId, metadata);
    updateDocumentMetaDataSteps.checkResponseStatusCodeIs400(replaceResponse,
        "Replace document metadata for read only document");
    updateDocumentMetaDataSteps.checkResponseErrorMessage(replaceResponse,
        ResponseErrorModel.READ_ONLY_DOCUMENT);
  }

  @Test
  @DisplayName("Replace document metadata using invalid document Id")
  void replaceDocumentMetadataUsingInvalidDocumentIdTest() {
    Response replaceResponse = updateDocumentMetaDataSteps.replaceDocumentMetadata(
        INVALID_DOCUMENT_ID, metadata);
    updateDocumentMetaDataSteps.checkResponseStatusCodeIs400(replaceResponse,
        "Replace document metadata using invalid document Id");
    updateDocumentMetaDataSteps.checkResponseErrorMessage(replaceResponse,
        ResponseErrorModel.NON_EXISTENT_DOCUMENT);
  }

  @ParameterizedTest
  @DisplayName("Update document metadata with incompatible Metadata value by type")
  @EnumSource(
      value = MetadataType.class,
      names = {"DATE", "DATETIME", "DECIMAL", "INTEGER", "BOOLEAN"})
  void updateDocumentMetadataWithIncompatibleMetaDataValueByTypeTest(MetadataType metadataType) {
    updateDocumentMetaDataSteps.makeSureMetadataWithTypeExist(metadataType);
    String metadataKey = updateDocumentMetaDataSteps.getMetadataKeyByType(metadataType);
    Map<String, Object> metaData = Map.of(metadataKey, TEST);
    Response updateResponse =
        updateDocumentMetaDataSteps.updateDocumentMetadata(documentId, metaData);
    updateDocumentMetaDataSteps.checkResponseStatusCodeIs400(updateResponse,
        "Update document metadata with incompatible Metadata value by type");
    updateDocumentMetaDataSteps.checkInvalidMetadataTypeErrorMessage(updateResponse, metadataKey);
  }

  @Test
  @DisplayName("Update document metadata with nonexistent Metadata key")
  void updateDocumentMetadataWithNonexistentMetaDataKeyTest() {
    Response updateResponse = updateDocumentMetaDataSteps.updateDocumentMetadata(
        documentId, Map.of(TEST, "Test value"));
    updateDocumentMetaDataSteps.checkResponseStatusCodeIs400(updateResponse,
        "Update document metadata with nonexistent Metadata key");
    updateDocumentMetaDataSteps.checkNotDefinedMetadataKeyErrorMessage(updateResponse, TEST);
  }

  @Test
  @DisplayName("Update document metadata without metadata header")
  void updateDocumentMetadataWithoutMetadataHeaderTest() {
    Response updateResponse =
        updateDocumentMetaDataSteps.updateDocumentMetadataWithoutMetadataHeader(documentId);
    updateDocumentMetaDataSteps.checkResponseStatusCodeIs400(updateResponse,
        "Update document metadata without metadata header");
    updateDocumentMetaDataSteps.checkResponseErrorMessage(updateResponse,
        ResponseErrorModel.EMPTY_METADATA);
  }

  @Test
  @DisplayName("Update document metadata for read only document")
  void updateReadOnlyDocumentMetadataTest() {
    String lockedDocumentId = updateDocumentMetaDataSteps.getRandomReadOnlyDocumentId();
    Response updateResponse = updateDocumentMetaDataSteps.updateDocumentMetadata(
        lockedDocumentId, metadata);
    updateDocumentMetaDataSteps.checkResponseStatusCodeIs400(updateResponse,
        "Update document metadata for read only document");
    updateDocumentMetaDataSteps.checkResponseErrorMessage(updateResponse,
        ResponseErrorModel.READ_ONLY_DOCUMENT);
  }

  @Test
  @DisplayName("Update document metadata using invalid document Id")
  void updateDocumentMetadataUsingInvalidDocumentIdTest() {
    Response updateResponse = updateDocumentMetaDataSteps.updateDocumentMetadata(
        INVALID_DOCUMENT_ID, metadata);
    updateDocumentMetaDataSteps.checkResponseStatusCodeIs400(updateResponse,
        "Update document metadata using invalid document Id");
    updateDocumentMetaDataSteps.checkResponseErrorMessage(updateResponse,
        ResponseErrorModel.NON_EXISTENT_DOCUMENT);
  }

  @Test
  @DisplayName("Update hard deleted document metadata")
  void updateHardDeletedDocumentMetadataTest() {
    Response updateResponse = updateDocumentMetaDataSteps.updateDocumentMetadata(
        hardDeleteDocumentsByIdSteps.generateHardDeletedDocumentForTest(), metadata);
    updateDocumentMetaDataSteps.checkResponseStatusCodeIs400(updateResponse,
        "Update hard deleted document metadata");
    updateDocumentMetaDataSteps.checkResponseErrorMessage(updateResponse,
        ResponseErrorModel.NON_EXISTENT_DOCUMENT);
  }

  @Test
  @DisplayName("Update document metadata using expired authorization token")
  void updateDocumentMetadataWithExpiredAuthorizationTokenTest() {
    Response updateResponse = updateDocumentMetaDataSteps
        .updateDocumentMetadataUsingExpiredAuthorizationToken(documentId, metadata);
    updateDocumentMetaDataSteps.checkResponseStatusCodeIs401(updateResponse,
        "Update document metadata using expired authorization token");
  }

  @Test
  @DisplayName("Update document metadata using invalid authorization token")
  void updateDocumentMetadataWithInvalidAuthorizationTokenTest() {
    Response updateResponse = updateDocumentMetaDataSteps
        .updateDocumentMetadataUsingInvalidAuthorizationToken(documentId, metadata);
    updateDocumentMetaDataSteps.checkResponseStatusCodeIs401(updateResponse,
        "Update document metadata using invalid authorization token");
  }

  @Test
  @DisplayName("Update document metadata using empty authorization token value")
  void updateDocumentMetadataWithEmptyAuthorizationTokenTest() {
    Response updateResponse = updateDocumentMetaDataSteps
        .updateDocumentMetadataUsingEmptyAuthorizationToken(documentId, metadata);
    updateDocumentMetaDataSteps.checkResponseStatusCodeIs401(updateResponse,
        "Update document metadata using empty authorization token");
  }

  @Test
  @DisplayName("Update document metadata without authorization token value")
  void updateDocumentMetadataWithoutAuthorizationTokenTest() {
    Response updateResponse = updateDocumentMetaDataSteps
        .updateDocumentMetadataWithoutAuthorizationToken(documentId, metadata);
    updateDocumentMetaDataSteps.checkResponseStatusCodeIs401(updateResponse,
        "Update document metadata without authorization token");
  }

  @Test
  @DisplayName("Update document metadata using invalid User Id")
  void updateDocumentMetadataWithInvalidUserIdTest() {
    Response updateResponse = updateDocumentMetaDataSteps
        .updateDocumentMetadataUsingInvalidUserId(documentId, metadata);
    updateDocumentMetaDataSteps.checkResponseStatusCodeIs400(updateResponse,
        "Update document metadata using invalid User Id");
    updateDocumentMetaDataSteps.checkUserIdResponseErrorMessage(updateResponse);
  }

  @Test
  @DisplayName("Update document metadata with empty User Id")
  void updateDocumentMetadataWithEmptyUserIdTest() {
    Response updateResponse = updateDocumentMetaDataSteps
        .updateDocumentMetadataUsingEmptyUserId(documentId, metadata);
    updateDocumentMetaDataSteps.checkResponseStatusCodeIs400(updateResponse,
        "Update document metadata with empty User Id");
    updateDocumentMetaDataSteps.checkUserIdResponseErrorMessage(updateResponse);
  }

  @Test
  @DisplayName("Update document metadata without User Id")
  void updateDocumentMetadataWithoutUserIdTest() {
    Response updateResponse = updateDocumentMetaDataSteps
        .updateDocumentMetadataWithoutUserId(documentId, metadata);
    updateDocumentMetaDataSteps.checkResponseStatusCodeIs400(updateResponse,
        "Update document metadata without User Id");
    updateDocumentMetaDataSteps.checkUserIdResponseErrorMessage(updateResponse);
  }
}
