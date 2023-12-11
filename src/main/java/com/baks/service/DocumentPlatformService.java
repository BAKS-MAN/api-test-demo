package com.baks.service;

import static com.baks.constants.ApiRequestsConstants.DOCUMENT_CATEGORIES_PATH;
import static com.baks.constants.ApiRequestsConstants.DOCUMENT_CATEGORY_HEADER;
import static com.baks.constants.ApiRequestsConstants.DOCUMENT_CATEGORY_ID;
import static com.baks.constants.ApiRequestsConstants.DOCUMENT_ID;
import static com.baks.constants.ApiRequestsConstants.DOCUMENT_TITLE_HEADER;
import static com.baks.constants.ApiRequestsConstants.EXPIRES_AT_HEADER;
import static com.baks.constants.ApiRequestsConstants.LOCK_STATUS_HEADER;
import static com.baks.constants.TestDataConstants.TEST;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;

import com.baks.constants.TestDataConstants;
import com.baks.enums.TestDocumentType;
import com.baks.pojo.response.DocumentCategoryObject;
import com.baks.pojo.response.MetadataObject;
import com.baks.utils.TestDataUtil;
import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * Document Platform Service class for related API requests.
 */
@Service
public class DocumentPlatformService extends DpApiService {

  private static final ThreadLocal<String> TEST_DOCUMENT_CATEGORY_ID = new ThreadLocal<>();
  private static final ThreadLocal<List<MetadataObject>> MANDATORY_METADATA =
      ThreadLocal.withInitial(ArrayList::new);
  private static final String DOCUMENTS_URI = "documents";
  private static final String SPECIFIED_DOCUMENT_URI = "documents/{documentId}";
  private static final String RECOVER_URI = "documents/{documentId}/recover";
  private static final String DOWNLOAD_URI = "documents/{documentId}/download";
  private static final String LOCK_STATUS_URI = "documents/{documentId}/lock-status";
  private static final String EXPIRATION_URI = "documents/{documentId}/expires-at";
  private static final String METADATA_URI = "documents/{documentId}/metadata";
  private static final String DOCUMENTS_FILTERED_LIST_URI = "documents/filter";
  private static final String DOCUMENTS_COUNT_URI = "documents/filter/count";
  private static final String DELETED_DOCUMENTS_FILTERED_LIST_URI = "documents/filter/deleted";
  private static final String MULTIPLE_URI = "documents/multiple";
  private static final String HARD_DELETE_BY_DOCUMENT_ID_URI = "documents/hard-by-document-ids";
  private static final String RECOVER_MULTIPLE_URI = "documents/recover/multiple";
  private static final String REGISTER_USER_URI = "register-user";
  private static final String DOCUMENT_CATEGORIES_URI = "document-categories";
  private static final String DOCUMENT_CATEGORY_URI = "document-categories/{categoryId}";

  private RequestSpecification getRequestSpecificationByDocumentType(
      TestDocumentType testDocumentType) {
    return RestAssured
        .given(getAuthorizedDpSpecification())
        .header(CONTENT_TYPE, testDocumentType.getRequestHeaderContentType())
        .header(DOCUMENT_TITLE_HEADER, TestDataConstants.DOCUMENT_TITLE_VALUE)
        .body(TestDataUtil.getSampleDocumentByType(testDocumentType.getType()));
  }

  private RequestSpecification getCustomRequestSpecificationForPdfDocument(
      RequestSpecification requestSpecification) {
    TestDocumentType testDocumentType = TestDocumentType.PDF;
    return RestAssured
        .given(requestSpecification)
        .header(CONTENT_TYPE, testDocumentType.getRequestHeaderContentType())
        .header(DOCUMENT_TITLE_HEADER, TestDataConstants.DOCUMENT_TITLE_VALUE)
        .body(TestDataUtil.getSampleDocumentByType(testDocumentType.getType()));
  }

  //------------------------Get document requests------------------------

  /**
   * Sends GET request for document details by DocumentId.
   *
   * @return document details response
   */
  public Response getDocumentDetailsById(String documentId) {
    return RestAssured
        .given(getAuthorizedDpSpecification())
        .pathParam(DOCUMENT_ID, documentId)
        .get(SPECIFIED_DOCUMENT_URI);
  }

  /**
   * Sends GET request for document details by DocumentId using specified RequestSpecification.
   *
   * @return document details response
   */
  public Response getDocumentByIdUsingCustomSpecification(String documentId,
      RequestSpecification requestSpecification) {
    return RestAssured
        .given(requestSpecification)
        .pathParam(DOCUMENT_ID, documentId)
        .get(SPECIFIED_DOCUMENT_URI);
  }

  /**
   * Sends POST request for list of NOT deleted Documents ("deleted" value is expected to be false)
   * using specified RequestSpecification.
   *
   * @return RestAssured response with list of not deleted Documents
   */
  public Response getListOfNotDeletedDocumentsUsingCustomSpecification(
      RequestSpecification requestSpecification) {
    return RestAssured
        .given(requestSpecification)
        .post(DOCUMENTS_FILTERED_LIST_URI);
  }

  /**
   * Sends POST request for list of NOT deleted Documents with specified filter parameters.
   *
   * @return RestAssured response with filtered list of not deleted Documents
   */
  public Response getListOfFilteredDocuments(Object filterParams) {
    return RestAssured
        .given(getAuthorizedDpSpecification())
        .body(filterParams)
        .post(DOCUMENTS_FILTERED_LIST_URI);
  }

  /**
   * Sends POST request for list of NOT deleted Documents with specified query parameters.
   *
   * @return RestAssured response with filtered list of not deleted Documents
   */
  public Response getListOfDocumentsWithQueryParams(Map<String, Object> queryParams) {
    return RestAssured
        .given(getAuthorizedDpSpecification())
        .queryParams(queryParams)
        .post(DOCUMENTS_FILTERED_LIST_URI);
  }

  /**
   * Sends POST request for list of NOT deleted Documents with specified query and filter
   * parameters.
   *
   * @return RestAssured response with filtered list of not deleted Documents
   */
  public Response getListOfFilteredDocumentsWithQueryParams(Map<String, Object> queryParams,
      Object filterParams) {
    return RestAssured
        .given(getAuthorizedDpSpecification())
        .queryParams(queryParams)
        .body(filterParams)
        .post(DOCUMENTS_FILTERED_LIST_URI);
  }

  /**
   * Sends POST request for list of NOT deleted Documents with specified headers and filter
   * parameters.
   *
   * @return RestAssured response with filtered list of not deleted Documents
   */
  public Response getListOfFilteredDocumentsWithSpecifiedHeaders(Map<String, Object> headers,
      Object filterParams) {
    return RestAssured
        .given(getAuthorizedDpSpecification())
        .body(filterParams)
        .headers(headers)
        .post(DOCUMENTS_FILTERED_LIST_URI);
  }

  /**
   * Sends POST request for list of NOT deleted Documents without Content-Type header.
   */
  public Response getListOfFilteredDocumentsWithoutContentTypeHeader() {
    return RestAssured
        .given(getAuthorizedDpSpecification())
        .noContentType()
        .post(DOCUMENTS_FILTERED_LIST_URI);
  }

  /**
   * Sends POST request to count NOT deleted Documents using specified RequestSpecification.
   *
   * @return RestAssured response with count of not deleted Documents
   */
  public Response countDocumentsUsingCustomSpecification(
      RequestSpecification requestSpecification) {
    return RestAssured
        .given(requestSpecification)
        .post(DOCUMENTS_COUNT_URI);
  }

  /**
   * Sends POST request for list of deleted Documents.
   *
   * @return RestAssured response with list of deleted Documents
   */
  public Response getListOfDeletedDocuments() {
    return getListOfDeletedDocumentsUsingCustomSpecification(getAuthorizedDpSpecification());
  }

  /**
   * Sends POST request for list of deleted Documents ("deleted" value is expected to be true) using
   * specified RequestSpecification.
   *
   * @return RestAssured response with list of deleted Documents
   */
  public Response getListOfDeletedDocumentsUsingCustomSpecification(
      RequestSpecification requestSpecification) {
    return RestAssured
        .given(requestSpecification)
        .post(DELETED_DOCUMENTS_FILTERED_LIST_URI);
  }

  //------------------------Download document requests------------------------

  /**
   * Sends GET request to download document by DocumentId using specified RequestSpecification.
   *
   * @return downloaded document
   */
  public Response downloadDocumentByIdUsingCustomSpecification(String documentId,
      RequestSpecification requestSpecification) {
    return RestAssured
        .given(requestSpecification)
        .pathParam(DOCUMENT_ID, documentId)
        .get(DOWNLOAD_URI);
  }

  //------------------------Upload document requests------------------------

  /**
   * Clear static test data.
   */
  public static void clearTestDocumentCategoryData() {
    TEST_DOCUMENT_CATEGORY_ID.remove();
    MANDATORY_METADATA.remove();
  }

  /**
   * Sends POST request to upload PDF document with specified document parameters.
   *
   * @return document creation response
   */
  public Response uploadPdfDocumentWithSpecifiedParameters(
      Map<String, Object> documentParameters) {
    return RestAssured
        .given(getRequestSpecificationByDocumentType(TestDocumentType.PDF))
        .header(getTestDocumentCategoryHeader())
        .headers(documentParameters)
        .post(DOCUMENTS_URI);
  }

  /**
   * Sends POST request to upload PDF document without mandatory field: Document-Category.
   *
   * @return document creation response
   */
  public Response uploadPdfDocumentWithoutDocumentCategory() {
    TestDocumentType testDocumentType = TestDocumentType.PDF;
    return RestAssured
        .given(getAuthorizedDpSpecification())
        .header(CONTENT_TYPE, testDocumentType.getRequestHeaderContentType())
        .header(DOCUMENT_TITLE_HEADER, TestDataConstants.DOCUMENT_TITLE_VALUE)
        .body(TestDataUtil.getSampleDocumentByType(testDocumentType.getType()))
        .post(DOCUMENTS_URI);
  }

  /**
   * Sends POST request to upload PDF document without mandatory field: Document title.
   *
   * @return document creation response
   */
  public Response uploadPdfDocumentWithoutDocumentTitle() {
    TestDocumentType testDocumentType = TestDocumentType.PDF;
    return RestAssured
        .given(getAuthorizedDpSpecification())
        .header(CONTENT_TYPE, testDocumentType.getRequestHeaderContentType())
        .header(getTestDocumentCategoryHeader())
        .body(TestDataUtil.getSampleDocumentByType(testDocumentType.getType()))
        .post(DOCUMENTS_URI);
  }

  /**
   * Sends POST request to upload PDF document without attachment.
   *
   * @return document creation response
   */
  public Response uploadDocumentWithoutAttach() {
    TestDocumentType testDocumentType = TestDocumentType.PDF;
    return RestAssured
        .given(getAuthorizedDpSpecification())
        .header(CONTENT_TYPE, testDocumentType.getRequestHeaderContentType())
        .header(getTestDocumentCategoryHeader())
        .post(DOCUMENTS_URI);
  }

  /**
   * Sends POST request to upload PDF document using specified RequestSpecification.
   *
   * @return document creation response
   */
  public Response uploadPdfDocumentUsingCustomSpecification(
      RequestSpecification requestSpecification) {
    return RestAssured
        .given(getCustomRequestSpecificationForPdfDocument(requestSpecification))
        .header(getTestDocumentCategoryHeader())
        .post(DOCUMENTS_URI);
  }

  /**
   * Sends POST request to upload PDF document using nonexistent endpoint.
   */
  public Response uploadPdfDocumentUsingNonexistentEndpoint() {
    return RestAssured
        .given(getRequestSpecificationByDocumentType(TestDocumentType.PDF))
        .header(getTestDocumentCategoryHeader())
        .post(TEST);
  }

  //------------------------Delete document requests------------------------

  /**
   * Sends DELETE request to soft delete specified document by DocumentId.
   */
  public Response deleteDocument(String documentId) {
    return RestAssured
        .given(getAuthorizedDpSpecification())
        .pathParam(DOCUMENT_ID, documentId)
        .delete(SPECIFIED_DOCUMENT_URI);
  }

  /**
   * Sends DELETE request to soft delete document by DocumentId using specified
   * RequestSpecification.
   */
  public Response deleteDocumentByIdUsingCustomSpecification(String documentId,
      RequestSpecification requestSpecification) {
    return RestAssured
        .given(requestSpecification)
        .pathParam(DOCUMENT_ID, documentId)
        .delete(SPECIFIED_DOCUMENT_URI);
  }

  /**
   * Sends DELETE request to soft delete multiple documents by Document Ids.
   */
  public Response deleteMultipleDocumentsByIdUsingCustomSpecification(
      List<String> documentsToDelete, RequestSpecification requestSpecification) {
    return RestAssured
        .given(requestSpecification)
        .body(documentsToDelete.toArray())
        .delete(MULTIPLE_URI);
  }

  /**
   * Sends DELETE request to hard delete multiple documents by Document Ids using specified
   * RequestSpecification.
   */
  public Response hardDeleteDocumentsByIdUsingCustomSpecification(
      List<String> documentsIds, RequestSpecification requestSpecification) {
    return RestAssured
        .given(requestSpecification)
        .body(documentsIds.toArray())
        .delete(HARD_DELETE_BY_DOCUMENT_ID_URI);
  }

  /**
   * Sends DELETE request to hard delete multiple documents by Document Ids using specified request
   * body.
   */
  public Response hardDeleteDocumentsByIdUsingSpecifiedRequestBody(Object requestBody) {
    return RestAssured
        .given(getAuthorizedDpSpecification())
        .body(requestBody)
        .delete(HARD_DELETE_BY_DOCUMENT_ID_URI);
  }

  //------------------------Recover document requests------------------------

  /**
   * Sends PUT request to recover document by DocumentId using specified RequestSpecification.
   *
   * @return recover document response
   */
  public Response recoverDocumentByIdUsingCustomSpecification(String documentId,
      RequestSpecification requestSpecification) {
    return RestAssured
        .given(requestSpecification)
        .pathParam(DOCUMENT_ID, documentId)
        .put(RECOVER_URI);
  }

  /**
   * Sends PUT request to recover multiple documents by DocumentId's.
   */
  public Response recoverMultipleDocumentsByIdUsingCustomSpecification(
      List<String> documentsToRecover, RequestSpecification requestSpecification) {
    return RestAssured
        .given(requestSpecification)
        .body(documentsToRecover.toArray())
        .put(RECOVER_MULTIPLE_URI);
  }

  /**
   * Sends POST request to recover multiple documents by DocumentId's with specified headers.
   */
  public Response recoverMultipleDocumentsWithSpecifiedHeaders(List<String> documentsToRecover,
      Map<String, Object> headers) {
    return RestAssured
        .given(getAuthorizedDpSpecification())
        .body(documentsToRecover.toArray())
        .headers(headers)
        .put(RECOVER_MULTIPLE_URI);
  }

  /**
   * Sends POST request to recover multiple documents without Content-Type header.
   */
  public Response recoverMultipleDocumentsWithoutContentTypeHeader() {
    return RestAssured
        .given(getAuthorizedDpSpecification())
        .noContentType()
        .put(RECOVER_MULTIPLE_URI);
  }

  //------------------------Update document requests------------------------

  /**
   * Sends PUT request to update document lock status using specified RequestSpecification.
   *
   * @param lockStatusValue lock status value to be applied for uploaded document.
   */
  public Response updateDocumentLockStatusUsingCustomSpecification(String documentId,
      String lockStatusValue, RequestSpecification requestSpecification) {
    return RestAssured
        .given(requestSpecification)
        .header(LOCK_STATUS_HEADER, lockStatusValue)
        .pathParam(DOCUMENT_ID, documentId)
        .put(LOCK_STATUS_URI);
  }

  /**
   * Sends PUT request to update document expiration date using specified RequestSpecification.
   *
   * @param expirationValue Date until a document is valid.
   */
  public Response updateDocumentExpirationDateUsingCustomSpecification(String documentId,
      String expirationValue, RequestSpecification requestSpecification) {
    return RestAssured
        .given(requestSpecification)
        .header(EXPIRES_AT_HEADER, expirationValue)
        .pathParam(DOCUMENT_ID, documentId)
        .put(EXPIRATION_URI);
  }

  /**
   * Sends PUT request to replace document Metadata using specified RequestSpecification.
   */
  public Response replaceDocumentMetadataUsingCustomSpecification(String documentId,
      Map<String, Object> metadata, RequestSpecification requestSpecification) {
    return RestAssured
        .given(requestSpecification)
        .headers(TestDataUtil.createMetaDataHeader(metadata))
        .pathParam(DOCUMENT_ID, documentId)
        .put(METADATA_URI);
  }

  /**
   * Sends PUT request to replace document Metadata without metadata header.
   */
  public Response replaceDocumentMetadataWithoutMetadataHeader(String documentId) {
    return RestAssured
        .given(getAuthorizedDpSpecification())
        .pathParam(DOCUMENT_ID, documentId)
        .put(METADATA_URI);
  }

  /**
   * Sends PUT request to update document Metadata using specified RequestSpecification.
   */
  public Response updateDocumentMetadataUsingCustomSpecification(String documentId,
      Map<String, Object> metadata, RequestSpecification requestSpecification) {
    return RestAssured
        .given(requestSpecification)
        .headers(TestDataUtil.createMetaDataHeader(metadata))
        .pathParam(DOCUMENT_ID, documentId)
        .patch(METADATA_URI);
  }

  /**
   * Sends PUT request to update document Metadata without metadata header.
   */
  public Response updateDocumentMetadataWithoutMetadataHeader(String documentId) {
    return RestAssured
        .given(getAuthorizedDpSpecification())
        .pathParam(DOCUMENT_ID, documentId)
        .patch(METADATA_URI);
  }

  /**
   * Sends PUT request to update document file with specified parameters.
   */
  public Response updateDocumentFileWithSpecifiedParameters(String documentId,
      Map<String, Object> documentParameters, TestDocumentType testDocumentType) {
    return RestAssured
        .given(getRequestSpecificationByDocumentType(testDocumentType))
        .header(getTestDocumentCategoryHeader())
        .headers(documentParameters)
        .pathParam(DOCUMENT_ID, documentId)
        .put(SPECIFIED_DOCUMENT_URI);
  }

  /**
   * Sends POST request to upload PDF document without specified optional document parameters, only
   * mandatory data is specified.
   *
   * @return document creation response
   */
  public Response uploadPdfDocumentWithPredefinedMandatoryData() {
    return RestAssured
        .given(getCustomRequestSpecificationForPdfDocument(getAuthorizedDpSpecification()))
        .header(getTestDocumentCategoryHeader())
        .headers(getTestDocumentCategoryMandatoryMetaDataHeaders())
        .post(DOCUMENTS_URI);
  }

  /**
   * Sends POST request to upload specified by type document without optional document parameters.
   *
   * @param testDocumentType type of uploaded document
   * @return document creation response
   * @see com.baks.enums.TestDocumentType enum with possible values of documents types
   */
  public Response uploadDocumentByType(TestDocumentType testDocumentType) {
    return RestAssured
        .given(getRequestSpecificationByDocumentType(testDocumentType))
        .header(getTestDocumentCategoryHeader())
        .headers(getTestDocumentCategoryMandatoryMetaDataHeaders())
        .post(DOCUMENTS_URI);
  }

  /**
   * Sends PUT request to update document file without specified optional document parameters, only
   * mandatory data is specified.
   */
  public Response updateDocumentFileWithPredefinedMandatoryData(String documentId,
      TestDocumentType testDocumentType) {
    return RestAssured
        .given(getRequestSpecificationByDocumentType(testDocumentType))
        .header(getTestDocumentCategoryHeader())
        .headers(getTestDocumentCategoryMandatoryMetaDataHeaders())
        .pathParam(DOCUMENT_ID, documentId)
        .put(SPECIFIED_DOCUMENT_URI);
  }

  /**
   * Sends PUT request to update document file and parameters using specified RequestSpecification
   * and predefined document parameters.
   */
  public Response updateDocumentFileUsingCustomSpecification(String documentId,
      RequestSpecification requestSpecification) {
    return RestAssured
        .given(getCustomRequestSpecificationForPdfDocument(requestSpecification))
        .header(getTestDocumentCategoryHeader())
        .headers(getTestDocumentCategoryMandatoryMetaDataHeaders())
        .pathParam(DOCUMENT_ID, documentId)
        .put(SPECIFIED_DOCUMENT_URI);
  }

  //----------------------Document categories ----------------------------------

  /**
   * Sends GET request to retrieve list of document categories using specified
   * RequestSpecification.
   *
   * @return list of document categories
   */
  public Response getDocumentCategoriesUsingCustomSpecification(
      RequestSpecification requestSpecification) {
    return RestAssured
        .given(requestSpecification)
        .get(DOCUMENT_CATEGORIES_URI);
  }

  /**
   * Sends GET request for document category details by document category id using specified
   * RequestSpecification.
   */
  public Response getDocumentCategoryDetailsUsingCustomSpecification(
      RequestSpecification requestSpecification, String documentCategoryId) {
    return RestAssured
        .given(requestSpecification)
        .pathParam(DOCUMENT_CATEGORY_ID, documentCategoryId)
        .get(DOCUMENT_CATEGORY_URI);
  }

  /**
   * Sends PUT request to update document file without mandatory field: Document-Category.
   */
  public Response updateDocumentFileWithoutDocumentCategory(String documentId) {
    TestDocumentType testDocumentType = TestDocumentType.EXCEL;
    return RestAssured
        .given(getAuthorizedDpSpecification())
        .header(CONTENT_TYPE, testDocumentType.getRequestHeaderContentType())
        .headers(getTestDocumentCategoryMandatoryMetaDataHeaders())
        .body(TestDataUtil.getSampleDocumentByType(testDocumentType.getType()))
        .pathParam(DOCUMENT_ID, documentId)
        .put(SPECIFIED_DOCUMENT_URI);
  }

  /**
   * Sends GET request to retrieve list of document categories.
   *
   * @return list of document categories
   */
  public Response getDocumentCategories() {
    return getDocumentCategoriesUsingCustomSpecification(getAuthorizedDpSpecification());
  }

  private Header getTestDocumentCategoryHeader() {
    return new Header(DOCUMENT_CATEGORY_HEADER, getTestDocumentCategoryId());
  }

  /**
   * Sends PUT request to update document file without attachment.
   */
  public Response updateDocumentFileWithoutAttach(String documentId) {
    TestDocumentType testDocumentType = TestDocumentType.EXCEL;
    return RestAssured
        .given(getAuthorizedDpSpecification())
        .header(CONTENT_TYPE, testDocumentType.getRequestHeaderContentType())
        .header(getTestDocumentCategoryHeader())
        .headers(getTestDocumentCategoryMandatoryMetaDataHeaders())
        .pathParam(DOCUMENT_ID, documentId)
        .put(SPECIFIED_DOCUMENT_URI);
  }

  /**
   * Returns list of Document Categories POJOs from document categories response".
   */
  public List<DocumentCategoryObject> getDocumentCategoryObjectListFromResponse(
      Response categoriesResponse) {
    return categoriesResponse.jsonPath()
        .getList(DOCUMENT_CATEGORIES_PATH, DocumentCategoryObject.class);
  }

  /**
   * Sends GET request to retrieve list of document categories and then extracts category id of the
   * test document category.
   *
   * @return category id of the test document category
   */
  public String getTestDocumentCategoryId() {
    if (StringUtils.isEmpty(TEST_DOCUMENT_CATEGORY_ID.get())) {
      TEST_DOCUMENT_CATEGORY_ID.set(TestDataUtil.getTestCategoryIdFromDocumentCategoriesList(
          getDocumentCategoryObjectListFromResponse(getDocumentCategories())));
    }
    return TEST_DOCUMENT_CATEGORY_ID.get();
  }

  /**
   * Generates Mandatory Metadata Headers for predefined test document category.
   */
  public Map<String, Object> getTestDocumentCategoryMandatoryMetaDataHeaders() {
    return TestDataUtil.createMetaDataHeader(
        TestDataUtil.generateMetaDataValues(getTestDocumentCategoryMandatoryMetaData()));
  }

  /**
   * Sends GET request for document category details using predefined test document category id and
   * extracts mandatory metadata.
   *
   * @return list of test document category mandatory metadata objects
   */
  public List<MetadataObject> getTestDocumentCategoryMandatoryMetaData() {
    if (MANDATORY_METADATA.get().isEmpty()) {
      DocumentCategoryObject documentCategory = getDocumentCategoryDetailsUsingCustomSpecification(
          getAuthorizedDpSpecification(), getTestDocumentCategoryId())
          .as(DocumentCategoryObject.class);
      List<MetadataObject> mandatoryMetadata = documentCategory.getMetadata().stream()
          .filter(MetadataObject::isRequired).collect(Collectors.toList());
      MANDATORY_METADATA.set(mandatoryMetadata);
    }
    return MANDATORY_METADATA.get();
  }

  //---------------------- Register user  ----------------------------------

  /**
   * Sends POST request to register user using specified RequestSpecification.
   */
  public Response registerUserUsingCustomSpecification(
      RequestSpecification requestSpecification) {
    return RestAssured
        .given(requestSpecification)
        .post(REGISTER_USER_URI);
  }
}
