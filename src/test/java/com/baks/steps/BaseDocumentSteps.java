package com.baks.steps;

import static com.baks.constants.ApiRequestsConstants.CORRELATION_ID_HEADER;
import static com.baks.constants.ApiRequestsConstants.DOCUMENT_ID;
import static com.baks.constants.ApiRequestsConstants.LOCK_STATUS_ACTIVE_VALUE;
import static com.baks.constants.ApiRequestsConstants.LOCK_STATUS_HEADER;
import static com.baks.constants.ApiRequestsConstants.LOCK_STATUS_KEY;
import static com.baks.constants.ApiRequestsConstants.LOCK_STATUS_READ_ONLY_VALUE;
import static com.baks.constants.ApiRequestsConstants.PER_PAGE_PARAM;
import static com.baks.constants.TestDataConstants.ANTIVIRUS_SCAN_STATUS_IN_PROGRESS;
import static com.baks.constants.TestDataConstants.ANTIVIRUS_SCAN_STATUS_OK;
import static com.baks.constants.TestDataConstants.SYSTEM_DOCUMENT_TYPE_NAME;
import static com.baks.constants.TestDataConstants.TEST_DOCUMENT_CATEGORY_CODE;

import com.baks.constants.TestDataConstants;
import com.baks.enums.MetadataType;
import com.baks.enums.ResponseErrorModel;
import com.baks.exceptions.UnexpectedResponseException;
import com.baks.pojo.response.DocumentCategoryObject;
import com.baks.pojo.response.DocumentDetailsObject;
import com.baks.pojo.response.MetadataObject;
import com.baks.pojo.response.SystemDocumentTypeObject;
import com.baks.service.DocumentPlatformService;
import com.baks.service.DocumentsUsageService;
import com.baks.utils.DateUtil;
import com.baks.utils.TestDataUtil;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import java.net.HttpURLConnection;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.Assumptions;
import org.awaitility.Awaitility;
import org.awaitility.core.ThrowingRunnable;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 * Parent class for classes with step actions.
 */
@ResourceLock(value = "documentsUsageService")
public class BaseDocumentSteps {

  @Autowired
  DocumentPlatformService documentPlatformService;
  @Autowired
  DocumentsUsageService documentsUsageService;

  @Step("Extract document id from document Creation Response")
  public String getDocumentIdFromCreationResponse(Response documentCreationResponse) {
    return getFieldValueFromResponse(DOCUMENT_ID, documentCreationResponse);
  }

  /**
   * Returns DocumentDetailsObject from document details response.
   */
  public DocumentDetailsObject getDocumentDetailsObjectByDocumentId(String documentId) {
    return getDocumentDetailsObjectFromResponse(getDocumentDetailsByDocumentId(documentId));
  }

  private DocumentDetailsObject getDocumentDetailsObjectFromResponse(Response documentDetails) {
    checkResponseStatusCodeIs200(documentDetails,
        "Get document details by DocumentId");
    return documentDetails.as(DocumentDetailsObject.class);
  }

  @Step("Send GET request for document details by DocumentId")
  public Response getDocumentDetailsByDocumentId(String documentId) {
    return documentPlatformService.getDocumentDetailsById(documentId);
  }

  @Step("Get list of Document Details POJOs from documents response")
  public List<DocumentDetailsObject> getDocumentDetailsObjectsListFromResponse(
      Response listOfDocumentsResponse) {
    return listOfDocumentsResponse.jsonPath().getList("documents", DocumentDetailsObject.class);
  }

  @Step("Get list of NOT deleted Documents")
  public List<DocumentDetailsObject> getListOfNotDeletedDocuments() {
    Response listOfDocuments =
        documentPlatformService.getListOfDocumentsWithQueryParams(Map.of(PER_PAGE_PARAM, 100));
    checkResponseStatusCodeIs200(listOfDocuments, "Get list of not deleted documents");
    return getDocumentDetailsObjectsListFromResponse(listOfDocuments);
  }

  @Step("Prepare list of not deleted documents for tests")
  public List<String> prepareNotDeletedDocumentsIdList(int requiredDocumentsQty) {
    return prepareDocumentsForTest(getListOfNotDeletedDocumentsForTest(), requiredDocumentsQty,
        this::generateDocumentForTest);
  }

  /**
   * Returns list of NOT deleted Documents which are not in use by another tests.
   */
  public List<String> getListOfNotDeletedDocumentsForTest() {
    return getDocumentIdListFromDocumentsList(
        removeInfectedFilesFromDocumentsList(getListOfNotDeletedDocuments()));
  }

  @Step("Get Randomly selected single document from list of NOT deleted Documents")
  public String getRandomDocumentIdFromDocumentsList() {
    return getRandomDocumentIdFromDocumentsList(getListOfNotDeletedDocumentsForTest());
  }

  /**
   * Returns randomly selected single document from specified document list. If list is empty then
   * new document will be created.
   */
  public String getRandomDocumentIdFromDocumentsList(List<String> listOfNotDeletedDocuments) {
    return getRandomDocumentIdFromListOrGenerateOne(listOfNotDeletedDocuments,
        this::generateDocumentForTest);
  }

  @Step("Generate document for test if list of not deleted Documents is empty")
  public String generateDocumentForTest() {
    prepareUserForTests();
    Response documentCreationResponse =
        documentPlatformService.uploadPdfDocumentWithPredefinedMandatoryData();
    checkResponseStatusCodeIs201(documentCreationResponse, "Upload document");
    String documentId = getDocumentIdFromCreationResponse(documentCreationResponse);
    documentsUsageService.addDocumentIdToUsageList(documentId);
    return documentId;
  }

  @Step("GET list of deleted Documents")
  public List<DocumentDetailsObject> getListOfDeletedDocuments() {
    Response listOfDocuments = documentPlatformService.getListOfDeletedDocuments();
    checkResponseStatusCodeIs200(listOfDocuments, "Get list of deleted documents");
    return getDocumentDetailsObjectsListFromResponse(listOfDocuments);
  }

  /**
   * Returns list of deleted Documents which are not in use by another tests.
   */
  public List<String> getListOfDeletedDocumentsForTest() {
    return getDocumentIdListFromDocumentsList(getListOfDeletedDocuments());
  }

  @Step("Get Randomly selected single document from list of deleted Documents")
  public String getRandomDocumentIdFromDeletedDocumentsList() {
    return getRandomDocumentIdFromListOrGenerateOne(getListOfDeletedDocumentsForTest(),
        this::generateDeletedDocumentForTest);
  }

  @Step("Generate deleted document for test if list of deleted Documents is empty")
  public String generateDeletedDocumentForTest() {
    String documentId = getRandomDocumentIdFromDocumentsList();
    Response deleteDocumentResponse = documentPlatformService.deleteDocument(documentId);
    checkResponseStatusCodeIs204(deleteDocumentResponse, "Generate deleted document for test");
    documentsUsageService.addDocumentIdToUsageList(documentId);
    return documentId;
  }

  @Step("Get list of Documents with 'active' lock status")
  public List<DocumentDetailsObject> getListOfActiveLockStatusDocuments() {
    Map<String, Object> filterParams = Map.of(LOCK_STATUS_KEY, LOCK_STATUS_ACTIVE_VALUE);
    Map<String, Object> queryParams = Map.of(PER_PAGE_PARAM, 100);
    Response listOfDocuments = documentPlatformService.getListOfFilteredDocumentsWithQueryParams(
        queryParams, filterParams);
    checkResponseStatusCodeIs200(listOfDocuments,
        "Get list of Documents with 'active' lock status");
    return getDocumentDetailsObjectsListFromResponse(listOfDocuments);
  }

  @Step("Get list of Documents with 'read only' lock status")
  public List<DocumentDetailsObject> getListOfReadOnlyLockStatusDocuments() {
    Map<String, Object> filterParams = Map.of(LOCK_STATUS_KEY, LOCK_STATUS_READ_ONLY_VALUE);
    Response listOfDocuments = documentPlatformService.getListOfFilteredDocuments(filterParams);
    checkResponseStatusCodeIs200(listOfDocuments,
        "Get list of Documents with 'read only' lock status");
    return getDocumentDetailsObjectsListFromResponse(listOfDocuments);
  }

  @Step("Get 'read only' random documentId from list of NOT deleted Documents")
  public String getRandomReadOnlyDocumentId() {
    List<String> readOnlyDocumentsList =
        getDocumentIdListFromDocumentsList(getListOfReadOnlyLockStatusDocuments());
    return getRandomDocumentIdFromListOrGenerateOne(readOnlyDocumentsList,
        this::generateReadOnlyDocumentForTest);
  }

  @Step("Generate 'read only' document for test if list of Documents doesn't contain it")
  private String generateReadOnlyDocumentForTest() {
    Response documentCreationResponse =
        documentPlatformService.uploadPdfDocumentWithSpecifiedParameters(
            getRequestHeadersWithMandatoryMeta(
                Map.of(LOCK_STATUS_HEADER, LOCK_STATUS_READ_ONLY_VALUE)));
    checkResponseStatusCodeIs201(documentCreationResponse,
        "Upload document with with specified 'read only' lock status");
    String documentId = getDocumentIdFromCreationResponse(documentCreationResponse);
    documentsUsageService.addDocumentIdToUsageList(documentId);
    return documentId;
  }

  @Step("Get random document from list of Documents with 'Active' Lock Status")
  public String getRandomDocumentIdWithActiveLockStatus() {
    return getRandomDocumentIdFromDocumentsList(
        getDocumentIdListFromDocumentsList(getListOfActiveLockStatusDocuments()));
  }

  /**
   * Extracts documentId from list of DocumentDetailsObject's and removes documentId's which are in
   * use by other tests, to avoid double document id usage.
   *
   * @return list of documentId's
   */
  public List<String> getDocumentIdListFromDocumentsList(
      List<DocumentDetailsObject> documentsList) {
    List<String> listOfDocumentId = documentsList.stream()
        .map(DocumentDetailsObject::getDocumentId).collect(Collectors.toList());
    documentsUsageService.removeUsedDocumentsFromList(listOfDocumentId);
    return listOfDocumentId;
  }

  private List<DocumentDetailsObject> removeInfectedFilesFromDocumentsList(
      List<DocumentDetailsObject> documentList) {
    return documentList.stream()
        .filter(document -> document.getScanStatus().equals(ANTIVIRUS_SCAN_STATUS_OK))
        .collect(Collectors.toList());
  }

  private String getRandomDocumentIdFromListOrGenerateOne(List<String> documentsList,
      Callable<String> generateDocumentAction) {
    String documentId;
    if (documentsList.isEmpty()) {
      try {
        documentId = generateDocumentAction.call();
      } catch (Exception e) {
        throw new RuntimeException("Failed to generate document for test: %s" + e.getMessage());
      }
    } else {
      int randomIndex = ThreadLocalRandom.current().nextInt(documentsList.size());
      documentId = documentsList.get(randomIndex);
    }
    documentsUsageService.addDocumentIdToUsageList(documentId);
    return documentId;
  }

  protected List<String> prepareDocumentsForTest(List<String> documentsList,
      int requiredDocumentsQty, Callable<String> generateDocumentAction) {
    if (documentsList.size() < requiredDocumentsQty) {
      int documentsToPrepare = requiredDocumentsQty - documentsList.size();
      for (int i = 0; i < documentsToPrepare; i++) {
        try {
          documentsList.add(generateDocumentAction.call());
        } catch (Exception e) {
          throw new RuntimeException("Failed to generate document for test: %s" + e.getMessage());
        }
      }
    }
    List<String> preparedDocuments =
        documentsList.stream().limit(requiredDocumentsQty).collect(Collectors.toList());
    documentsUsageService.addDocumentsIdToUsageList(preparedDocuments);
    return preparedDocuments;
  }

  @Step("Get quantity of not deleted documents")
  public int getQuantityOfNotDeletedDocuments() {
    Response countResponse = documentPlatformService.countDocumentsUsingCustomSpecification(
        documentPlatformService.getAuthorizedDpSpecification());
    return getCountValueFromCountResponse(countResponse);
  }

  private int getCountValueFromCountResponse(Response countResponse) {
    checkResponseStatusCodeIs200(countResponse, "Get quantity of not deleted documents");
    return countResponse.jsonPath().getInt("count");
  }

  @Step("Get document category details object by category id")
  public DocumentCategoryObject getDocumentCategoryObjectById(String documentCategoryId) {
    return getDocumentCategoryDetails(documentCategoryId).as(DocumentCategoryObject.class);
  }

  @Step("Send GET request for document category details by category Id")
  public Response getDocumentCategoryDetails(String categoryId) {
    return documentPlatformService.getDocumentCategoryDetailsUsingCustomSpecification(
        documentPlatformService.getAuthorizedDpSpecification(), categoryId);
  }

  @Step("Get list of mandatory metadata for test document category")
  public List<MetadataObject> getTestDocumentCategoryMandatoryMetadata() {
    return documentPlatformService.getTestDocumentCategoryMandatoryMetaData();
  }

  @Step("Get list of non mandatory metadata for test document category")
  private List<MetadataObject> getTestDocumentCategoryNonMandatoryMetadata() {
    return getDocumentCategoryObjectById(getTestDocumentCategoryId()).getMetadata().stream()
        .filter(metadata -> !metadata.isRequired()).collect(Collectors.toList());
  }

  @Step("Get test document category id")
  public String getTestDocumentCategoryId() {
    return documentPlatformService.getTestDocumentCategoryId();
  }

  @Step("Get list of mandatory system document type metadata keys")
  public List<String> getMandatoryMetadataKeys() {
    return getTestDocumentCategoryMandatoryMetadata().stream()
        .map(MetadataObject::getKey).collect(Collectors.toList());
  }

  @Step("Get list of system document type metadata by metadata type")
  private List<MetadataObject> getMetadataByType(MetadataType metadataType) {
    SystemDocumentTypeObject sdt = TestDataUtil.getSystemDocumentTypes().stream()
        .filter(documentType -> documentType.getSdtName().equals(SYSTEM_DOCUMENT_TYPE_NAME))
        .findFirst().orElseThrow(
            () -> new NoSuchElementException(String.format(
                "list of system document types does not contains document type with name: %s",
                SYSTEM_DOCUMENT_TYPE_NAME)));
    return sdt.getMetadata().stream()
        .filter(metadata -> metadata.getType().equalsIgnoreCase(metadataType.getSystemValue()))
        .collect(Collectors.toList());
  }

  @Step("Get list of system document type metadata keys by metadata type")
  public List<String> getMetadataKeysByType(MetadataType metadataType) {
    return getMetadataByType(metadataType).stream()
        .map(MetadataObject::getKey).collect(Collectors.toList());
  }

  @Step("Get metadata key by metadata type")
  public String getMetadataKeyByType(MetadataType metadataType) {
    return getMetadataKeysByType(metadataType).stream().findFirst().orElseThrow(
        () -> new NoSuchElementException(String.format(
            "list of metadata keys is empty for metadata type: %s",
            metadataType.getSystemValue())));
  }

  public String getStringTypeMetadataKey() {
    return getMetadataKeyByType(MetadataType.STRING);
  }

  public String getDateTypeMetadataKey() {
    return getMetadataKeyByType(MetadataType.DATE);
  }

  public String getIntegerTypeMetadataKey() {
    return getMetadataKeyByType(MetadataType.INTEGER);
  }

  public String getDecimalTypeMetadataKey() {
    return getMetadataKeyByType(MetadataType.DECIMAL);
  }

  public String getBooleanTypeMetadataKey() {
    return getMetadataKeyByType(MetadataType.BOOLEAN);
  }

  @Step("Generate mandatory metadata")
  public Map<String, Object> generateMandatoryMetadataValues() {
    return TestDataUtil.generateMetaDataValues(getTestDocumentCategoryMandatoryMetadata());
  }

  @Step("Generate non mandatory metadata")
  public Map<String, Object> generateNonMandatoryMetadataValues() {
    return TestDataUtil.generateMetaDataValues(getTestDocumentCategoryNonMandatoryMetadata());
  }

  @Step("Generate metadata by metadata type")
  public Map<String, Object> generateMetadataValuesByType(MetadataType metadataType) {
    return TestDataUtil.generateMetaDataValues(getMetadataByType(metadataType));
  }

  protected String getFieldValueFromResponse(String fieldPath, Response response) {
    String fieldValue = response.getBody().jsonPath().getString(fieldPath);
    if (StringUtils.isNotEmpty(fieldValue)) {
      return fieldValue;
    } else {
      throw new UnexpectedResponseException(String.format(
          "'%s'' was not found in the response: %n%s",
          fieldPath, response.getBody().asPrettyString()));
    }
  }

  @Step("Wait for the system to apply the changes then perform assertion steps")
  public void actionWithDelay(ThrowingRunnable assertionAction) {
    // To avoid cases when assertion performed earlier than system applied changes.
    Awaitility
        .await("Wait before perform assertion")
        /* Code execution timer, if code execution is not completed then will be fail by timeout,
       if assertion code should fail and timer is still left then retry action will be performed.*/
        .atMost(Duration.ofSeconds(10))
        .pollDelay(Duration.ofMillis(200)) // Delay before code execution.
        .untilAsserted(assertionAction);
  }

  @Step("Wait until antivirus scan to be completed")
  public void waitForAntivirusScanToBeCompleted(String documentId) {
    Awaitility
        .await()
        .atMost(Duration.ofSeconds(15))
        .pollInterval(Duration.ofSeconds(5))
        .untilAsserted(() -> Assertions.assertThat(
                getDocumentDetailsObjectByDocumentId(documentId).getScanStatus())
            .as("Check that document is scanned by Antivirus successfully")
            .isNotEqualToIgnoringCase(ANTIVIRUS_SCAN_STATUS_IN_PROGRESS));
  }

  @Step("Send GET request to retrieve document categories list")
  public Response getDocumentCategoriesList() {
    return documentPlatformService.getDocumentCategories();
  }

  protected List<DocumentCategoryObject> getDocumentCategoryObjectsList() {
    return documentPlatformService.getDocumentCategoryObjectListFromResponse(
        getDocumentCategoriesList());
  }

  @Step("Prepare user for automation tests")
  public void prepareUserForTests() {
    /* Category for automation testing was added into document configuration,
    so for every registered user this category will be created automatically.
    if such category doesn't exist for test user Id this means that user is not registered.*/
    if (getDocumentCategoryObjectsList().stream()
        .noneMatch(category -> category.getCategoryCode().equals(TEST_DOCUMENT_CATEGORY_CODE))) {
      checkResponseStatusCodeIs201(
          documentPlatformService.registerUserUsingCustomSpecification(
              documentPlatformService.getAuthorizedDpSpecificationForUser((
                  TestDataConstants.USER_ID_VALUE))),
          String.format("Create document category with code '%s' for automation tests",
              TEST_DOCUMENT_CATEGORY_CODE));
    }
  }

  protected Map<String, Object> getRequestHeadersWithMandatoryMeta(
      Map<String, Object> requestParameters) {
    return TestDataUtil.getRequestHeadersWithMandatoryMeta(requestParameters,
        getTestDocumentCategoryMandatoryMetadata());
  }

  protected Set<String> getResponseFieldKeys(Response response) {
    Map<String, Object> responseFields = response.jsonPath().get();
    return responseFields.keySet();
  }

  //------------------------Verification steps ------------------------
  @Step("Check Document title in Document Details")
  public void checkDocumentTitleInDocumentDetails(DocumentDetailsObject documentDetails,
      String documentTitle) {
    Assertions.assertThat(documentDetails.getDocumentTitle())
        .as("Check Document Details: Document title")
        .isEqualTo(documentTitle);
  }

  @Step("Check File Name in Document Details")
  public void checkFileNameInDocumentDetails(DocumentDetailsObject documentDetails,
      String fileName) {
    Assertions.assertThat(documentDetails.getFileName())
        .as("Check Document Details: File Name")
        .isEqualTo(fileName);
  }

  @Step("Check Lock Status in Document Details")
  public void checkLockStatusInDocumentDetails(DocumentDetailsObject documentDetails,
      String expectedLockStatus) {
    Assertions.assertThat(documentDetails.getLockStatus())
        .as("Check Document Details: Lock Status")
        .isEqualTo(expectedLockStatus);
  }

  @Step("Check Expiration Date in Document Details")
  public void checkExpirationDateInDocumentDetails(DocumentDetailsObject documentDetails,
      String expectedExpirationDate) {
    Assertions.assertThat(DateUtil.getFormattedDateFromString(documentDetails.getExpiresAt()))
        .as("Check Document Details: Expiration Date")
        .isEqualTo(DateUtil.getFormattedDateFromString(expectedExpirationDate));
  }

  @Step("Check Metadata in Document Details")
  public void checkMetadataInDocumentDetails(DocumentDetailsObject documentDetails,
      Map<String, Object> expectedMetaData) {
    Assertions.assertThat(documentDetails.getMetadata())
        .as("Check Document Details: Metadata")
        .containsAllEntriesOf(expectedMetaData.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> String.valueOf(e.getValue()))));
  }

  @Step("Check Digest data in Document Details")
  public void checkDigestDataInDocumentDetails(DocumentDetailsObject documentDetails,
      Map<String, String> digestData) {
    Assertions.assertThat(documentDetails.getMd5())
        .as("Check Document Details: MD5 Digest data")
        .isEqualTo(digestData.get("MD5"));
    Assertions.assertThat(documentDetails.getCrc32())
        .as("Check Document Details: CRC32 Digest data")
        .isEqualTo(digestData.get("CRC32"));
  }

  private String getResponseCheckAssertionMessage(Response response, String assertionDescription) {
    return String.format("%s: response code check; \n Request correlation id: %s",
        assertionDescription, response.getHeader(CORRELATION_ID_HEADER));
  }

  @Step("Check response status code is 200")
  public void checkResponseStatusCodeIs200(Response response, String assertionDescription) {
    Assertions.assertThat(response.getStatusCode())
        .as(getResponseCheckAssertionMessage(response, assertionDescription))
        .isEqualTo(HttpURLConnection.HTTP_OK);
  }

  @Step("Check response status code is 201: Created")
  public void checkResponseStatusCodeIs201(Response response, String assertionDescription) {
    Assertions.assertThat(response.getStatusCode())
        .as(getResponseCheckAssertionMessage(response, assertionDescription))
        .isEqualTo(HttpURLConnection.HTTP_CREATED);
  }

  @Step("Check response status code is 202: accepted")
  public void checkResponseStatusCodeIs202(Response response, String assertionDescription) {
    Assertions.assertThat(response.getStatusCode())
        .as(getResponseCheckAssertionMessage(response, assertionDescription))
        .isEqualTo(HttpURLConnection.HTTP_ACCEPTED);
  }

  @Step("Check response status code is 204: No content")
  public void checkResponseStatusCodeIs204(Response response, String assertionDescription) {
    Assertions.assertThat(response.getStatusCode())
        .as(getResponseCheckAssertionMessage(response, assertionDescription))
        .isEqualTo(HttpURLConnection.HTTP_NO_CONTENT);
  }

  @Step("Check response status code is 207: Multi-Status")
  public void checkResponseStatusCodeIs207(Response response, String assertionDescription) {
    Assertions.assertThat(response.getStatusCode())
        .as(getResponseCheckAssertionMessage(response, assertionDescription))
        .isEqualTo(HttpStatus.MULTI_STATUS.value());
  }

  @Step("Check response status code is 400: Bad Request")
  public void checkResponseStatusCodeIs400(Response response, String assertionDescription) {
    Assertions.assertThat(response.getStatusCode())
        .as(getResponseCheckAssertionMessage(response, assertionDescription))
        .isEqualTo(HttpURLConnection.HTTP_BAD_REQUEST);
  }

  @Step("Check response status code is 401: Unauthorized")
  public void checkResponseStatusCodeIs401(Response response, String assertionDescription) {
    Assertions.assertThat(response.getStatusCode())
        .as(getResponseCheckAssertionMessage(response, assertionDescription))
        .isEqualTo(HttpURLConnection.HTTP_UNAUTHORIZED);
  }

  @Step("Check response status code is 404: Not found")
  public void checkResponseStatusCodeIs404(Response response, String assertionDescription) {
    Assertions.assertThat(response.getStatusCode())
        .as(getResponseCheckAssertionMessage(response, assertionDescription))
        .isEqualTo(HttpURLConnection.HTTP_NOT_FOUND);
  }

  @Step("Check response status code is 405: Method Not Allowed")
  public void checkResponseStatusCodeIs405(Response response, String assertionDescription) {
    Assertions.assertThat(response.getStatusCode())
        .as(getResponseCheckAssertionMessage(response, assertionDescription))
        .isEqualTo(HttpURLConnection.HTTP_BAD_METHOD);
  }

  @Step("Check response status code is 406: Not Acceptable")
  public void checkResponseStatusCodeIs406(Response response, String assertionDescription) {
    Assertions.assertThat(response.getStatusCode())
        .as(getResponseCheckAssertionMessage(response, assertionDescription))
        .isEqualTo(HttpURLConnection.HTTP_NOT_ACCEPTABLE);
  }

  @Step("Check response status code is 409: Conflict")
  public void checkResponseStatusCodeIs409(Response response, String assertionDescription) {
    Assertions.assertThat(response.getStatusCode())
        .as(getResponseCheckAssertionMessage(response, assertionDescription))
        .isEqualTo(HttpURLConnection.HTTP_CONFLICT);
  }

  @Step("Check response status code is 424: Failed Dependency")
  public void checkResponseStatusCodeIs424(Response response, String assertionDescription) {
    Assertions.assertThat(response.getStatusCode())
        .as(getResponseCheckAssertionMessage(response, assertionDescription))
        .isEqualTo(HttpStatus.FAILED_DEPENDENCY.value());
  }

  @Step("Check response status code is 415: Unsupported Media Type")
  public void checkResponseStatusCodeIs415(Response response, String assertionDescription) {
    Assertions.assertThat(response.getStatusCode())
        .as(getResponseCheckAssertionMessage(response, assertionDescription))
        .isEqualTo(HttpURLConnection.HTTP_UNSUPPORTED_TYPE);
  }

  @Step("Check response error message")
  public void checkResponseErrorMessage(Response response, ResponseErrorModel responseErrorModel) {
    checkResponseErrorMessageByModelStateKey(response, responseErrorModel,
        responseErrorModel.getModelStateName());
  }

  @Step("Check response error message by model state key")
  public void checkResponseErrorMessageByModelStateKey(Response response,
      ResponseErrorModel responseErrorModel, String modelStateKey) {
    Map<String, List<Map<String, String>>> responseErrors = new LinkedCaseInsensitiveMap<>();
    responseErrors.putAll(response.jsonPath().getMap("errors.modelState[0]"));
    Assertions.assertThat(responseErrors)
        .as("Error Model state with key '%s' was not found in the response: %s",
            modelStateKey, response.prettyPrint())
        .isNotEmpty().containsKey(modelStateKey);
    Assertions.assertThat(responseErrors.get(modelStateKey)
            .stream().map(entry -> entry.get("message")).collect(Collectors.toList()))
        .as("List of error messages doesn't contain expected error message text")
        .contains(responseErrorModel.getMessage());
  }

  @Step("Check User Id error response message")
  /* Assertion for cases when UserId is invalid, empty or missing.*/
  public void checkUserIdResponseErrorMessage(Response userIdErrorResponse) {
    Assertions.assertThat(userIdErrorResponse.getBody().asPrettyString())
        .as("Check User Id error response message")
        .contains(ResponseErrorModel.USER_ID_ERROR.getMessage());
  }

  @Step("Check response error message")
  public void checkResponseErrorMessageWithoutErrorModel(Response errorResponse,
      ResponseErrorModel responseErrorModel) {
    Assertions.assertThat(errorResponse.getBody().asPrettyString())
        .as("Check response error message text")
        .containsIgnoringCase(responseErrorModel.getMessage());
  }

  @Step("Check missing mandatory metadata response error message")
  public void checkMissingMandatoryMetadataErrorMessage(Response metadataErrorResponse,
      List<String> mandatoryMetaKeys) {
    List<String> expectedErrorMessages = new ArrayList<>();
    for (String mandatoryMetaKey : mandatoryMetaKeys) {
      expectedErrorMessages.add(String.format(
          ResponseErrorModel.MANDATORY_METADATA_MISSING.getMessage(), mandatoryMetaKey));
    }
    Assertions.assertThat(metadataErrorResponse.getBody().asPrettyString())
        .as("Check missing mandatory metadata response error message")
        .contains(expectedErrorMessages);
  }

  @Step("Check invalid metadata type response error message")
  public void checkInvalidMetadataTypeErrorMessage(Response errorResponse, String metadataKey) {
    checkErrorResponseContainsMessage(errorResponse,
        String.format(ResponseErrorModel.METADATA_INVALID_TYPE.getMessage(), metadataKey));
  }

  @Step("Check not defined metadata key response error message")
  public void checkNotDefinedMetadataKeyErrorMessage(Response errorResponse, String metadataKey) {
    checkErrorResponseContainsMessage(errorResponse,
        String.format(ResponseErrorModel.NOT_DEFINED_METADATA_KEY.getMessage(),
            metadataKey.toLowerCase()));
  }

  @Step("Check mandatory metadata exist in the system")
  public void makeSureMandatoryMetadataExist() {
    Assumptions.assumeThat(getTestDocumentCategoryMandatoryMetadata())
        .as("Test is skipped: there are no mandatory metadata exist in the system")
        .isNotEmpty();
  }

  @Step("Check non mandatory metadata exist in the system")
  public void makeSureNonMandatoryMetadataExist() {
    Assumptions.assumeThat(getTestDocumentCategoryNonMandatoryMetadata())
        .as("Test is skipped: there are no non mandatory metadata exist in the system")
        .isNotEmpty();
  }

  @Step("Check metadata type exist in the system")
  public void makeSureMetadataWithTypeExist(MetadataType metadataType) {
    Assumptions.assumeThat(getMetadataByType(metadataType))
        .as("Test is skipped: there are no metadata with type '%s' exist in the system",
            metadataType.getSystemValue())
        .isNotEmpty();
  }

  private void checkErrorResponseContainsMessage(Response errorResponse,
      String expectedErrorMessage) {
    Assertions.assertThat(errorResponse.getBody().asPrettyString())
        .as("Check response contains error message")
        .contains(expectedErrorMessage);
  }
}
