package com.baks.steps;

import static com.baks.constants.ApiRequestsConstants.CORRELATION_ID_HEADER;
import static com.baks.constants.ApiRequestsConstants.DOCUMENT_CATEGORIES_KEY;
import static com.baks.constants.ApiRequestsConstants.EXPIRES_AT_KEY;
import static com.baks.constants.ApiRequestsConstants.LIMIT_PARAM;
import static com.baks.constants.ApiRequestsConstants.LOCK_STATUS_ACTIVE_VALUE;
import static com.baks.constants.ApiRequestsConstants.LOCK_STATUS_KEY;
import static com.baks.constants.ApiRequestsConstants.METADATA_KEY;
import static com.baks.constants.ApiRequestsConstants.OFFSET_PARAM;
import static com.baks.constants.ApiRequestsConstants.PAGE_PARAM;
import static com.baks.constants.ApiRequestsConstants.PER_PAGE_PARAM;
import static com.baks.constants.ApiRequestsConstants.UPLOADED_AT_KEY;
import static com.baks.constants.ConfigurationConstants.PAGE_SIZE_DEFAULT_VALUE;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;

import com.baks.enums.FilterComparator;
import com.baks.enums.MetadataType;
import com.baks.enums.ResponseErrorModel;
import com.baks.pojo.response.DocumentDetailsObject;
import com.baks.utils.DateUtil;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 * Step layer for filter documents actions.
 */
@Component
public class FilterDocumentsSteps extends BaseDocumentSteps {

  @Step("Send POST request to get list of documents")
  public Response getNotDeletedFilteredDocuments() {
    return documentPlatformService.getListOfNotDeletedDocumentsUsingCustomSpecification(
        documentPlatformService.getAuthorizedDpSpecification());
  }

  @Step("Send POST request to get list of documents filtered by expiration date")
  public Response getDocumentsFilteredByExpirationDate(String date, String comparisonValue) {
    return documentPlatformService.getListOfFilteredDocuments(
        getFilterRequestBodyAsString(EXPIRES_AT_KEY, date, comparisonValue));
  }

  @Step("Send POST request to get list of documents filtered by uploaded date")
  public Response getDocumentsFilteredByUploadedDate(String date, String comparisonValue) {
    return documentPlatformService.getListOfFilteredDocuments(
        getFilterRequestBodyAsString(UPLOADED_AT_KEY, date, comparisonValue));
  }

  @Step("Send POST request to get list of documents filtered by metadata")
  public Response getDocumentsFilteredByMeta(Map<String, String> meta, String metaType,
      String comparisonValue) {
    return documentPlatformService.getListOfFilteredDocuments(
        getFilteredByMetaRequestBody(meta, metaType, comparisonValue));
  }

  @Step("Send POST request to get list of documents filtered by custom filter")
  public Response getDocumentsFilteredByCustomFilter(String filterKey, String filterValue) {
    return documentPlatformService.getListOfFilteredDocuments(Map.of(filterKey, filterValue));
  }

  protected String getFilterRequestBodyAsString(String filterKey, Object filterValue,
      String filterComparison) {
    Map<String, Object> filter = new HashMap<>();
    Map<String, Object> filterParams = new HashMap<>();
    filterParams.put("value", filterValue);
    filterParams.put("comp", filterComparison);
    filter.put(filterKey, List.of(filterParams));
    return new JSONObject(filter).toString();
  }

  protected String getFilterRequestBodyWithoutParamsAsString(String filterKey) {
    return new JSONObject(Map.of(filterKey, new ArrayList<>())).toString();
  }

  @Step("Send POST request to get list of documents filtered by expiration date "
      + "without filter parameters")
  public Response getDocumentsFilteredByExpirationDateWithoutParams() {
    return documentPlatformService.getListOfFilteredDocuments(
        getFilterRequestBodyWithoutParamsAsString(EXPIRES_AT_KEY));
  }

  @Step("Send POST request to get list of documents filtered by uploaded date "
      + "without filter parameters")
  public Response getDocumentsFilteredByUploadedDateWithoutParams() {
    return documentPlatformService.getListOfFilteredDocuments(
        getFilterRequestBodyWithoutParamsAsString(UPLOADED_AT_KEY));
  }

  @Step("Send POST request to get list of documents filtered by document category")
  public Response getDocumentsFilteredByDocumentCategory(String documentCategory) {
    return documentPlatformService.getListOfFilteredDocuments(
        new JSONObject(Map.of(DOCUMENT_CATEGORIES_KEY, documentCategory)).toString());
  }

  @Step("Send POST request to get list of documents filtered by lock status")
  public Response getDocumentsFilteredByLockStatus(String lockStatus) {
    //Immutable map is not applicable here due test with null value.
    Map<String, String> filterParams = new HashMap<>();
    filterParams.put(LOCK_STATUS_KEY, lockStatus);
    return documentPlatformService.getListOfFilteredDocuments(filterParams);
  }

  @Step("Send POST request to get list of documents using unsupported 'Accept' header")
  public Response getFilteredDocumentsUsingUnsupportedAcceptHeader() {
    return documentPlatformService.getListOfNotDeletedDocumentsUsingCustomSpecification(
        documentPlatformService.getSpecificationWithUnsupportedAcceptHeader());
  }

  @Step("Send POST request to get list of documents using unsupported 'Content-Type' header")
  public Response getFilteredDocumentsUsingUnsupportedContentTypeHeader() {
    return documentPlatformService.getListOfFilteredDocumentsWithSpecifiedHeaders(
        Map.of(CONTENT_TYPE, ContentType.TEXT),
        Map.of(LOCK_STATUS_KEY, LOCK_STATUS_ACTIVE_VALUE));
  }

  @Step("Send POST request to get list of documents without 'Content-Type' header")
  public Response getFilteredDocumentsWithoutContentTypeHeader() {
    return documentPlatformService.getListOfFilteredDocumentsWithoutContentTypeHeader();
  }

  @Step("Send POST request to get list of documents using expired authorization token")
  public Response getFilteredDocumentsUsingExpiredAuthorizationToken() {
    return documentPlatformService.getListOfNotDeletedDocumentsUsingCustomSpecification(
        documentPlatformService.getDpSpecificationWithExpiredToken());
  }

  @Step("Send POST request to get list of documents using invalid authorization token")
  public Response filteredDocumentsUsingInvalidAuthorizationToken() {
    return documentPlatformService.getListOfNotDeletedDocumentsUsingCustomSpecification(
        documentPlatformService.getDpSpecificationWithInvalidAuthorizationToken());
  }

  @Step("Send POST request to get list of documents with empty authorization token value")
  public Response getFilteredDocumentsUsingEmptyAuthorizationToken() {
    return documentPlatformService.getListOfNotDeletedDocumentsUsingCustomSpecification(
        documentPlatformService.getSpecificationWithEmptyAuthorizationToken());
  }

  @Step("Send POST request to get list of documents without authorization token")
  public Response getFilteredDocumentsWithoutAuthorizationToken() {
    return documentPlatformService.getListOfNotDeletedDocumentsUsingCustomSpecification(
        documentPlatformService.getSpecificationWithoutAuthorizationToken());
  }

  @Step("Send POST request to get list of documents using invalid User Id")
  public Response getFilteredDocumentsUsingInvalidUserId() {
    return documentPlatformService.getListOfNotDeletedDocumentsUsingCustomSpecification(
        documentPlatformService.getSpecificationWithInvalidUserId());
  }

  @Step("Send POST request to get list of documents using empty User Id value")
  public Response getFilteredDocumentsUsingEmptyUserId() {
    return documentPlatformService.getListOfNotDeletedDocumentsUsingCustomSpecification(
        documentPlatformService.getSpecificationWithEmptyUserId());
  }

  @Step("Send POST request to get list of documents without User Id")
  public Response getFilteredDocumentsWithoutUserId() {
    return documentPlatformService.getListOfNotDeletedDocumentsUsingCustomSpecification(
        documentPlatformService.getSpecificationWithoutUserId());
  }

  @Step("Get list of Documents with specified page size")
  public Response getListOfDocumentsWithPageSize(Object pageSize) {
    Map<String, Object> queryParams = new HashMap<>();
    queryParams.put(PER_PAGE_PARAM, pageSize);
    return documentPlatformService.getListOfDocumentsWithQueryParams(queryParams);
  }

  @Step("Get list of Documents with specified page number")
  public Response getListOfDocumentsWithPagination(Object pageNumber) {
    Map<String, Object> queryParams = new HashMap<>();
    queryParams.put(PAGE_PARAM, pageNumber);
    return documentPlatformService.getListOfDocumentsWithQueryParams(queryParams);
  }

  @Step("Get list of Documents with specified page number and page size")
  public Response getListOfDocumentsWithPaginationAndPageSize(int pageNumber, int pageSize) {
    return documentPlatformService.getListOfDocumentsWithQueryParams(
        Map.of(PAGE_PARAM, pageNumber, PER_PAGE_PARAM, pageSize));
  }

  @Step("Get list of Documents with specified page limit")
  public Response getListOfDocumentsWithPageLimit(Object pageLimit) {
    Map<String, Object> queryParams = new HashMap<>();
    queryParams.put(LIMIT_PARAM, pageLimit);
    return documentPlatformService.getListOfDocumentsWithQueryParams(queryParams);
  }

  @Step("Get list of Documents with specified page offset")
  public Response getListOfDocumentsWithOffset(Object offset) {
    Map<String, Object> queryParams = new HashMap<>();
    queryParams.put(OFFSET_PARAM, offset);
    return documentPlatformService.getListOfDocumentsWithQueryParams(queryParams);
  }

  @Step("Get list of Documents with specified page offset and page limit")
  public Response getListOfDocumentsWithPageOffsetAndLimit(int offset, int limit) {
    return documentPlatformService.getListOfDocumentsWithQueryParams(
        Map.of(OFFSET_PARAM, offset, LIMIT_PARAM, limit));
  }

  public int getAvailablePageQuantity(int pageSize) {
    return BigDecimal.valueOf((double)
        getQuantityOfNotDeletedDocuments() / pageSize).setScale(0, RoundingMode.UP).intValue();
  }

  //---------------------------Additional cases -----------------------------------------------
  @Step("Send POST request to get filtered documents using specified correlation id header")
  public Response getDocumentsUsingSpecifiedCorrelationIdHeader(String correlationId) {
    return documentPlatformService.getListOfFilteredDocumentsWithSpecifiedHeaders(
        Map.of(CORRELATION_ID_HEADER, correlationId),
        Map.of(LOCK_STATUS_KEY, LOCK_STATUS_ACTIVE_VALUE));
  }

  //------------------------Verification steps ------------------------
  @Step("Check response status code and extract DocumentDetailsObject list")
  public List<DocumentDetailsObject> getCheckedDocumentDetailsObjectsFromResponse(
      Response filterResponse, String assertionMessage) {
    checkResponseStatusCodeIs200(filterResponse, assertionMessage);
    List<DocumentDetailsObject> filteredResults =
        getDocumentDetailsObjectsListFromResponse(filterResponse);
    checkListOfNotDeletedDocuments(filteredResults);
    return filteredResults;
  }

  protected void checkListOfFilteredDocumentsIsNotEmpty(List<DocumentDetailsObject> documentsList) {
    Assertions.assertThat(documentsList)
        .as("Check list of documents is not empty")
        .isNotEmpty();
  }

  @Step("Check list of filtered documents is empty")
  public void checkListOfFilteredDocumentsIsEmpty(List<DocumentDetailsObject> documentsList) {
    Assertions.assertThat(documentsList)
        .as("Check list of filtered documents is empty")
        .isEmpty();
  }

  @Step("Check list of not deleted documents")
  public void checkListOfNotDeletedDocuments(List<DocumentDetailsObject> documentsList) {
    checkListOfFilteredDocumentsIsNotEmpty(documentsList);
    Assertions.assertThat(documentsList.stream().noneMatch(DocumentDetailsObject::isDeleted))
        .as("Check list of not deleted documents doesn't contain deleted documents")
        .isTrue();
  }

  @Step("Check list of documents is filtered by expiration date")
  public void checkDocumentsFilteredByExpirationDate(List<DocumentDetailsObject> documentsList,
      String expirationDate, FilterComparator dateComparator) {
    Assertions.assertThat(documentsList.stream()
            .allMatch(document -> StringUtils.isNotEmpty(document.getExpiresAt())))
        .as("Check list of documents doesn't contain records with empty 'ExpiresAt' field")
        .isTrue();
    List<LocalDate> expirationDates = documentsList.stream()
        .map(document -> getLocaleDateFromString(document.getExpiresAt()))
        .collect(Collectors.toList());
    checkFilteredDocumentsDates(expirationDates, expirationDate, dateComparator);
  }

  @Step("Check list of documents is filtered by uploaded date")
  public void checkDocumentsFilteredByUploadedDate(List<DocumentDetailsObject> documentsList,
      String uploadedDate, FilterComparator dateComparator) {
    List<LocalDate> uploadDates = documentsList.stream()
        .map(document -> getLocaleDateFromString(document.getUploadedAt()))
        .collect(Collectors.toList());
    checkFilteredDocumentsDates(uploadDates, uploadedDate, dateComparator);
  }

  private LocalDate getLocaleDateFromString(String date) {
    return LocalDate.parse(DateUtil.getFormattedDateFromString(date));
  }

  @Step("Check list of documents contains only records with specified document categories")
  public void checkDocumentsFilteredByDocumentCategory(List<DocumentDetailsObject> documentsList,
      String documentCategory) {
    Assertions.assertThat(documentsList.stream()
            .allMatch(document -> document.getDocumentCategoryCode().equals(documentCategory)))
        .as("Check list of documents contains only records with specified document category: %s",
            documentCategory)
        .isTrue();
  }

  @Step("Check list of documents contains only records with specified lock status")
  public void checkDocumentsFilteredByLockStatus(List<DocumentDetailsObject> documentsList,
      String lockStatus) {
    Assertions.assertThat(documentsList.stream()
            .allMatch(document -> document.getLockStatus().equalsIgnoreCase(lockStatus)))
        .as("Check list of documents contains only records with with specified lock status: %s",
            lockStatus)
        .isTrue();
  }

  private void checkFilteredDocumentsDates(List<LocalDate> dateList, String comparisonDate,
      FilterComparator dateComparator) {
    boolean resultIsCorrect;
    LocalDate specifiedDate = LocalDate.parse(comparisonDate);

    switch (dateComparator) {
      case DATE_BEFORE:
        resultIsCorrect = dateList.stream().allMatch(date -> date.isBefore(specifiedDate));
        break;
      case DATE_ON_OR_BEFORE:
        resultIsCorrect = dateList.stream().noneMatch(date -> date.isAfter(specifiedDate));
        break;
      case DATE_AFTER:
        resultIsCorrect = dateList.stream().anyMatch(date -> date.isAfter(specifiedDate));
        break;
      case DATE_ON_OR_AFTER:
        resultIsCorrect = dateList.stream().noneMatch(date -> date.isBefore(specifiedDate));
        break;
      case DATE_ON:
        resultIsCorrect = dateList.stream().allMatch(date -> date.isEqual(specifiedDate));
        break;
      default:
        throw new IllegalArgumentException(String.format(
            "Date comparator '%s' is not supported", dateComparator.getValue()));
    }
    Assertions.assertThat(resultIsCorrect)
        .as("Check list of documents filtered by specified date: %s with comparator %s",
            comparisonDate, dateComparator.getValue())
        .isTrue();
  }

  @Step("Check list of documents is filtered by meta")
  public void checkDocumentsFilteredByMeta(List<DocumentDetailsObject> documentsList,
      Map<String, String> requestedMetadata, MetadataType metadataType,
      FilterComparator comparator) {
    List<Map<String, String>> documentsMetadata = documentsList.stream()
        .map(DocumentDetailsObject::getMetadata).collect(Collectors.toList());

    for (var metadataEntry : requestedMetadata.entrySet()) {
      String metadataKey = metadataEntry.getKey().toLowerCase();
      String metadataValue = metadataEntry.getValue();
      Assertions.assertThat(documentsMetadata.stream()
              .allMatch(documentMeta -> documentMeta.containsKey(metadataKey)))
          .as("All filtered documents contain requested metadata key: %s", metadataKey)
          .isTrue();
      List<String> documentsMetaValues = documentsMetadata.stream()
          .map(documentMeta -> documentMeta.get(metadataKey)).collect(Collectors.toList());
      switch (metadataType) {
        case STRING:
          checkFilteredDocumentsMetadataStringValues(documentsMetaValues, metadataValue,
              comparator);
          break;
        case DATE:
          List<LocalDate> dateList = documentsMetaValues.stream()
              .map(this::getLocaleDateFromString).collect(Collectors.toList());
          checkFilteredDocumentsDates(dateList, metadataValue, comparator);
          break;
        case DECIMAL:
        case INTEGER:
          checkFilteredDocumentsMetadataDecimalValues(
              documentsMetaValues.stream().map(Double::valueOf).collect(Collectors.toList()),
              Double.parseDouble(metadataValue), comparator);
          break;
        case BOOLEAN:
          checkFilteredDocumentsMetadataBooleanValues(
              documentsMetaValues.stream().map(Boolean::parseBoolean).collect(Collectors.toList()),
              metadataValue, comparator);
          break;
        default:
          throw new IllegalArgumentException(String.format(
              "MetadataType '%s' is not supported", metadataType.getValue()));
      }
    }
  }

  private void checkFilteredDocumentsMetadataStringValues(List<String> documentsMetaValues,
      String comparableValue, FilterComparator comparator) {
    boolean resultIsCorrect;
    switch (comparator) {
      case EQUALS:
        resultIsCorrect = documentsMetaValues.stream()
            .allMatch(metaValue -> metaValue.equals(comparableValue));
        break;
      case NOT_EQUALS:
        resultIsCorrect = documentsMetaValues.stream()
            .noneMatch(metaValue -> metaValue.equals(comparableValue));
        break;
      case CONTAINS:
        if (comparableValue.isEmpty()) {
          //check is not needed, cause value is not specified.
          resultIsCorrect = true;
        } else {
          resultIsCorrect = documentsMetaValues.stream()
              .allMatch(metaValue -> metaValue.contains(comparableValue));
        }
        break;
      case DOES_NOT_CONTAIN:
        resultIsCorrect = documentsMetaValues.stream()
            .noneMatch(metaValue -> metaValue.contains(comparableValue));
        break;
      case STARTS_WITH:
        resultIsCorrect = documentsMetaValues.stream()
            .allMatch(metaValue -> metaValue.startsWith(comparableValue));
        break;
      case ENDS_WITH:
        resultIsCorrect = documentsMetaValues.stream()
            .allMatch(metaValue -> metaValue.endsWith(comparableValue));
        break;
      default:
        throw new IllegalArgumentException(String.format(
            "Comparator '%s' is not supported", comparator.getValue()));
    }
    Assertions.assertThat(resultIsCorrect)
        .as("Check list of documents filtered by meta String value: %s with comparator %s",
            comparableValue, comparator.getValue())
        .isTrue();
  }

  private void checkFilteredDocumentsMetadataDecimalValues(List<Double> documentsMetaValues,
      double comparableValue, FilterComparator comparator) {
    boolean resultIsCorrect;
    switch (comparator) {
      case EQUALS:
        resultIsCorrect = documentsMetaValues.stream().allMatch(value -> value == comparableValue);
        break;
      case NOT_EQUALS:
        resultIsCorrect = documentsMetaValues.stream().allMatch(value -> value != comparableValue);
        break;
      case LESS_THAN:
        resultIsCorrect = documentsMetaValues.stream().allMatch(value -> value < comparableValue);
        break;
      case LESS_THAN_OR_EQUALS:
        resultIsCorrect = documentsMetaValues.stream().allMatch(value -> value <= comparableValue);
        break;
      case GREATER_THAN:
        resultIsCorrect = documentsMetaValues.stream().allMatch(value -> value > comparableValue);
        break;
      case GREATER_THAN_OR_EQUALS:
        resultIsCorrect = documentsMetaValues.stream().allMatch(value -> value >= comparableValue);
        break;
      default:
        throw new IllegalArgumentException(String.format(
            "Digit comparator '%s' is not supported", comparator.getValue()));
    }
    Assertions.assertThat(resultIsCorrect)
        .as("Check list of documents filtered by meta Decimal value: %s with comparator %s",
            comparableValue, comparator.getValue())
        .isTrue();
  }

  private void checkFilteredDocumentsMetadataBooleanValues(List<Boolean> documentsMetaValues,
      String comparableValue, FilterComparator comparator) {
    switch (comparator) {
      case IS:
        Assertions.assertThat(documentsMetaValues)
            .as("Check list of documents filtered by meta Boolean value: %s with comparator %s",
                comparableValue, comparator.getValue())
            .containsOnly(Boolean.parseBoolean(comparableValue));
        break;
      case NOT:
        Assertions.assertThat(documentsMetaValues)
            .as("Check list of documents filtered by meta Boolean value: %s with comparator %s",
                comparableValue, comparator.getValue())
            .doesNotContain(Boolean.parseBoolean(comparableValue));
        break;
      default:
        throw new IllegalArgumentException(String.format(
            "Boolean comparator '%s' is not supported", comparator.getValue()));
    }
  }

  @Step("Check documents default per page size")
  public void checkDocumentsDefaultPerPageSize(List<DocumentDetailsObject> documentsList) {
    checkDocumentsPerPageSize(documentsList, getQuantityOfNotDeletedDocuments(),
        PAGE_SIZE_DEFAULT_VALUE);
  }

  @Step("Check not deleted documents per page size")
  public void checkNotDeletedDocumentsPerPageSize(List<DocumentDetailsObject> documentsList,
      int pageSize) {
    checkDocumentsPerPageSize(documentsList, getQuantityOfNotDeletedDocuments(), pageSize);
  }

  protected void checkDocumentsPerPageSize(List<DocumentDetailsObject> documentsList,
      int documentsQty, int pageSize) {
    if (documentsQty < pageSize) {
      pageSize = documentsQty; // In case documents size less than requested page size.
    }
    Assertions.assertThat(documentsList)
        .as("Check documents per page size")
        .hasSize(pageSize);
  }

  @Step("Check not deleted documents per page size with specified page numbering")
  public void checkNotDeletedDocumentsPerPageNumberAndSize(
      List<DocumentDetailsObject> documentsList,
      int pageNumber, int pageSize) {
    checkDocumentsPerPageNumberAndSize(documentsList, getQuantityOfNotDeletedDocuments(),
        pageNumber, pageSize);
  }

  protected void checkDocumentsPerPageNumberAndSize(List<DocumentDetailsObject> documentsList,
      int documentsQty, int pageNumber, int pageSize) {
    // If page is the last, then page size could be less than requested page size.
    if ((pageNumber * pageSize) > documentsQty) {
      // Difference between requested page size and available.
      int difference = pageNumber * pageSize - documentsQty;
      pageSize -= difference;
    }
    Assertions.assertThat(documentsList)
        .as("Check documents per page size with specified page numbering")
        .hasSize(pageSize);
  }

  @Step("Check not deleted documents page size with specified page offset")
  public void checkNotDeletedDocumentsPageSizeWithOffset(List<DocumentDetailsObject> documentsList,
      int pageOffset, int pageLimit) {
    checkDocumentsPageSizeWithOffset(documentsList, getQuantityOfNotDeletedDocuments(), pageOffset,
        pageLimit);
  }

  protected void checkDocumentsPageSizeWithOffset(List<DocumentDetailsObject> documentsList,
      int documentsQty, int pageOffset, int pageLimit) {
    if ((documentsQty - pageOffset) < pageLimit) {
      pageLimit = documentsQty - pageOffset;
    }
    Assertions.assertThat(documentsList)
        .as("Check documents per page size with specified page numbering")
        .hasSize(pageLimit);
  }

  @Step("Check response error message for request with exceeded page size value")
  public void checkExceededPageSizeErrorMsg(Response filterResponse) {
    Assertions.assertThat(filterResponse.getBody().asPrettyString())
        .as("Check response error message for request with exceeded page size value")
        .contains("The value of limit/per_page exceeds the maximum value of 1000");
  }

  @Step("Check response error message for request with incorrect page size value")
  public void checkIncorrectPageSizeErrorMsg(Response filterResponse) {
    Assertions.assertThat(filterResponse.getBody().asPrettyString())
        .as("Check response error message for request with incorrect page size value")
        .contains("The value of limit/per_page must be greater than zero");
  }

  @Step("Check response error message for request with invalid page size value")
  public void checkInvalidPageSizeErrorMsg(Response filterResponse, String providedValue) {
    Assertions.assertThat(filterResponse.getBody().asPrettyString())
        .as("Check response error message for request with invalid page size value")
        .contains(String.format("Query parameter 'per_page' with value '%s' is not a valid Integer",
            providedValue));
  }

  @Step("Check response error message for request with incorrect page number value")
  public void checkIncorrectPageNumberErrorMsg(Response filterResponse) {
    Assertions.assertThat(filterResponse.getBody().asPrettyString())
        .as("Check response error message for request with incorrect page number value")
        .contains("page must be greater than zero");
  }

  @Step("Check response error message for request with invalid page number value")
  public void checkInvalidPageNumberErrorMsg(Response filterResponse, String providedValue) {
    Assertions.assertThat(filterResponse.getBody().asPrettyString())
        .as("Check response error message for request with invalid page number value")
        .contains(String.format("Query parameter 'page' with value '%s' is not a valid Long",
            providedValue));
  }

  @Step("Check response error message for request with invalid page limit value")
  public void checkInvalidPageLimitErrorMsg(Response filterResponse, String providedValue) {
    Assertions.assertThat(filterResponse.getBody().asPrettyString())
        .as("Check response error message for request with invalid page limit value")
        .contains(String.format("Query parameter 'limit' with value '%s' is not a valid Integer",
            providedValue));
  }

  @Step("Check response error message for request with incorrect page offset value")
  public void checkIncorrectPageOffsetErrorMsg(Response filterResponse) {
    Assertions.assertThat(filterResponse.getBody().asPrettyString())
        .as("Check response error message for request with incorrect page offset value")
        .contains("offset must be greater than or equal to zero");
  }

  @Step("Check response error message for request with invalid page offset value")
  public void checkInvalidPageOffsetErrorMsg(Response filterResponse, String providedValue) {
    Assertions.assertThat(filterResponse.getBody().asPrettyString())
        .as("Check response error message for request with invalid page offset value")
        .contains(String.format("Query parameter 'offset' with value '%s' is not a valid Long",
            providedValue));
  }

  @Step("Check specified correlation id header is present in the response")
  public void checkCorrelationIdValueInResponse(Response response, String uuid) {
    Assertions.assertThat(response.getHeader(CORRELATION_ID_HEADER))
        .as("Specified correlation id header is present in the response")
        .isEqualTo(uuid);
  }

  @Step("Check specified correlation id header is present in the response")
  public void checkCorrelationIdValueInIsNotPresentInResponse(Response response, String uuid) {
    Assertions.assertThat(response.getHeader(CORRELATION_ID_HEADER))
        .as("Specified correlation id header is present in the response")
        .isNotEqualTo(uuid);
  }

  //------------------------Combined steps ------------------------

  /**
   * Generates metadata request body for filter request.
   */
  protected String getFilteredByMetaRequestBody(Map<String, String> meta, String metaType,
      String comparisonValue) {
    List<Map<String, Object>> filters = new ArrayList<>();
    for (var metaDataEntry : meta.entrySet()) {
      Map<String, Object> filterParams = new HashMap<>();
      filterParams.put("key", metaDataEntry.getKey());
      filterParams.put("value", metaDataEntry.getValue());
      filterParams.put("type", metaType);
      filterParams.put("comp", comparisonValue);
      filters.add(filterParams);
    }
    return new JSONObject(Map.of(METADATA_KEY, filters)).toString();
  }

  /**
   * Checks that filtered documents response is empty.
   */
  public void checkFilteredDocumentsResponseIsEmpty(Response filterResponse) {
    checkResponseStatusCodeIs200(filterResponse, "Get filtered documents");
    List<DocumentDetailsObject> filteredResults =
        getDocumentDetailsObjectsListFromResponse(filterResponse);
    checkListOfFilteredDocumentsIsEmpty(filteredResults);
  }

  /**
   * Checks filter response has error message regarding invalid filter.
   */
  public void checkInvalidFilterResponse(Response filterResponse, String assertionMessage) {
    checkResponseStatusCodeIs400(filterResponse, assertionMessage);
    checkResponseErrorMessage(filterResponse,
        ResponseErrorModel.INVALID_FILTER);
  }

  /**
   * Checks filter response has error message regarding invalid field comparator.
   */
  public void checkInvalidFieldComparatorResponse(Response filterResponse,
      String assertionMessage) {
    checkResponseStatusCodeIs400(filterResponse, assertionMessage);
    checkResponseErrorMessage(filterResponse,
        ResponseErrorModel.INVALID_FIELD_COMPARATOR);
  }

  /**
   * Generates metadata Map Object with null value.
   */
  public Map<String, String> getMetaWithNullValue(String metaKey) {
    Map<String, String> metadata = new HashMap<>();
    metadata.put(metaKey, null);
    return metadata;
  }

  /**
   * Returns date for filter by date tests.
   */
  public String getDateForFilterByDateTest(FilterComparator dateComparator) {
    if (dateComparator.equals(FilterComparator.DATE_BEFORE)) {
      return DateUtil.getCurrentFormattedDatePlusDays(1);
    } else if (dateComparator.equals(FilterComparator.DATE_AFTER)) {
      return DateUtil.getCurrentFormattedDateMinusDays(10);
    } else {
      return DateUtil.getCurrentFormattedDate();
    }
  }
}
