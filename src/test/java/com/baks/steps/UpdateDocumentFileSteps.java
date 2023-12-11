package com.baks.steps;

import static com.baks.constants.ApiRequestsConstants.DOCUMENT_CATEGORY_HEADER;
import static com.baks.constants.ApiRequestsConstants.DOCUMENT_TITLE_HEADER;
import static com.baks.constants.ApiRequestsConstants.EXPIRES_AT_HEADER;
import static com.baks.constants.ApiRequestsConstants.LOCK_STATUS_HEADER;
import static com.baks.constants.ApiRequestsConstants.LOCK_STATUS_READ_ONLY_VALUE;
import static com.baks.constants.TestDataConstants.TEST;

import com.baks.enums.TestDocumentType;
import com.baks.utils.TestDataUtil;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * Step layer for update document file and it's data actions.
 */
@Component
public class UpdateDocumentFileSteps extends BaseDocumentSteps {

  @Step("Send PUT request to update document file with specified document title")
  public Response updateDocumentFileWithSpecifiedDocumentTitle(String documentId,
      String documentTitle, TestDocumentType testDocumentType) {
    return documentPlatformService.updateDocumentFileWithSpecifiedParameters(documentId,
        getRequestHeadersWithMandatoryMeta(Map.of(DOCUMENT_TITLE_HEADER, documentTitle)),
        testDocumentType);
  }

  @Step("Send PUT request to update document file with specified file name")
  public Response updateDocumentFileWithSpecifiedFileName(String documentId, String fileName,
      TestDocumentType testDocumentType) {
    return documentPlatformService.updateDocumentFileWithSpecifiedParameters(documentId,
        getRequestHeadersWithMandatoryMeta(TestDataUtil.createContentDispositionHeader(fileName)),
        testDocumentType);
  }

  @Step("Send PUT request to update document file with specified lock status")
  public Response updateDocumentFileWithSpecifiedLockStatus(String documentId, String lockStatus,
      TestDocumentType testDocumentType) {
    return documentPlatformService.updateDocumentFileWithSpecifiedParameters(documentId,
        getRequestHeadersWithMandatoryMeta(Map.of(LOCK_STATUS_HEADER, lockStatus)),
        testDocumentType);
  }

  @Step("Send PUT request to update document file with specified expiration date")
  public Response updateDocumentFileWithSpecifiedExpirationDate(String documentId,
      String expirationDate, TestDocumentType testDocumentType) {
    return documentPlatformService.updateDocumentFileWithSpecifiedParameters(documentId,
        getRequestHeadersWithMandatoryMeta(Map.of(EXPIRES_AT_HEADER, expirationDate)),
        testDocumentType);
  }

  @Step("Send PUT request to update document file with specified Digest data")
  public Response updateDocumentFileWithSpecifiedDigest(String documentId,
      Map<String, String> digestData, TestDocumentType testDocumentType) {
    return documentPlatformService.updateDocumentFileWithSpecifiedParameters(documentId,
        getRequestHeadersWithMandatoryMeta(TestDataUtil.createDigestHeader(digestData)),
        testDocumentType);
  }

  @Step("Send PUT request to update document file with specified mandatory metadata")
  public Response updateDocumentFileWithSpecifiedMandatoryMetaData(String documentId,
      Map<String, Object> metaData, TestDocumentType testDocumentType) {
    return documentPlatformService.updateDocumentFileWithSpecifiedParameters(documentId,
        TestDataUtil.createMetaDataHeader(metaData), testDocumentType);
  }

  @Step("Send PUT request to update document file with specified non mandatory metadata")
  public Response updateDocumentFileWithSpecifiedNonMandatoryMetaData(String documentId,
      Map<String, Object> metaData, TestDocumentType testDocumentType) {
    return documentPlatformService.updateDocumentFileWithSpecifiedParameters(documentId,
        getRequestHeadersWithMandatoryMeta(TestDataUtil.createMetaDataHeader(metaData)),
        testDocumentType);
  }

  @Step("Send PUT request to update document file without optional document parameters")
  public Response updateDocumentFileWithoutSpecifiedData(String documentId,
      TestDocumentType testDocumentType) {
    return documentPlatformService.updateDocumentFileWithPredefinedMandatoryData(
        documentId, testDocumentType);
  }

  //------------------------Negative steps ------------------------

  @Step("Send PUT request to update document file without mandatory field: Document-Category")
  public Response updateDocumentWithoutDocumentCategory(String documentId) {
    return documentPlatformService.updateDocumentFileWithoutDocumentCategory(documentId);
  }

  @Step("Send PUT request to update document file using non-existent document category")
  public Response updateDocumentUsingNonexistentDocumentCategory(String documentId,
      TestDocumentType testDocumentType) {
    return documentPlatformService.updateDocumentFileWithSpecifiedParameters(
        documentId, getRequestHeadersWithMandatoryMeta(Map.of(DOCUMENT_CATEGORY_HEADER, TEST)),
        testDocumentType);
  }

  @Step("Send PUT request to update document file without attachment")
  public Response updateDocumentWithoutAttach(String documentId) {
    return documentPlatformService.updateDocumentFileWithoutAttach(documentId);
  }

  @Step("Send PUT request to update document file without mandatory metadata")
  public Response updateDocumentWithoutMandatoryMetadata(String documentId,
      TestDocumentType testDocumentType) {
    return documentPlatformService.updateDocumentFileWithSpecifiedParameters(documentId,
        Map.of(LOCK_STATUS_HEADER, LOCK_STATUS_READ_ONLY_VALUE), testDocumentType);
  }

  @Step("Send PUT request to update document file using expired authorization token")
  public Response updateDocumentFileUsingExpiredAuthorizationToken(String documentId) {
    return documentPlatformService.updateDocumentFileUsingCustomSpecification(documentId,
        documentPlatformService.getDpSpecificationWithExpiredToken());
  }

  @Step("Send PUT request to update document file using invalid authorization token")
  public Response updateDocumentFileUsingInvalidAuthorizationToken(String documentId) {
    return documentPlatformService.updateDocumentFileUsingCustomSpecification(documentId,
        documentPlatformService.getDpSpecificationWithInvalidAuthorizationToken());
  }

  @Step("Send PUT request to update document file with empty authorization token value")
  public Response updateDocumentFileUsingEmptyAuthorizationToken(String documentId) {
    return documentPlatformService.updateDocumentFileUsingCustomSpecification(documentId,
        documentPlatformService.getSpecificationWithEmptyAuthorizationToken());
  }

  @Step("Send PUT request to update document file without authorization token")
  public Response updateDocumentFileWithoutAuthorizationToken(String documentId) {
    return documentPlatformService.updateDocumentFileUsingCustomSpecification(documentId,
        documentPlatformService.getSpecificationWithoutAuthorizationToken());
  }

  @Step("Send PUT request to update document file using invalid User Id")
  public Response updateDocumentFileUsingInvalidUserId(String documentId) {
    return documentPlatformService.updateDocumentFileUsingCustomSpecification(documentId,
        documentPlatformService.getSpecificationWithInvalidUserId());
  }

  @Step("Send PUT request to update document file using empty User Id value")
  public Response updateDocumentFileUsingEmptyUserId(String documentId) {
    return documentPlatformService.updateDocumentFileUsingCustomSpecification(documentId,
        documentPlatformService.getSpecificationWithEmptyUserId());
  }

  @Step("Send PUT request to update document file without User Id")
  public Response updateDocumentFileWithoutUserId(String documentId) {
    return documentPlatformService.updateDocumentFileUsingCustomSpecification(documentId,
        documentPlatformService.getSpecificationWithoutUserId());
  }
}
