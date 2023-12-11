package com.baks.tests;

import static com.baks.constants.ApiRequestsConstants.LOCK_STATUS_ACTIVE_VALUE;
import static com.baks.constants.ApiRequestsConstants.LOCK_STATUS_READ_ONLY_VALUE;
import static com.baks.constants.TestDataConstants.METADATA_STRING_VALUE;
import static com.baks.constants.TestDataConstants.TEST;
import static com.baks.constants.TestDataConstants.TEST_DOCUMENT_CATEGORY_CODE;

import com.baks.enums.FilterComparator;
import com.baks.enums.MetadataType;
import com.baks.enums.ResponseErrorModel;
import com.baks.runners.EndpointTestRunner;
import com.baks.steps.CreateDocumentSteps;
import com.baks.steps.FilterDocumentsSteps;
import com.baks.utils.DateUtil;
import com.baks.utils.TestDataUtil;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Issue;
import io.restassured.response.Response;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

@Execution(ExecutionMode.CONCURRENT)
@Epic("Document filter operations")
@Feature("Documents filter API")
@DisplayName("Filter not deleted documents tests")
class FilterDocumentsTest extends EndpointTestRunner {

  @Autowired
  private FilterDocumentsSteps filterDocumentsSteps;
  private static final String DECIMAL_DEFAULT_VALUE = "50";
  private static final String INTEGER_DEFAULT_VALUE = "14";
  private static String stringTypeMetadataKey;
  private static String integerTypeMetadataKey;
  private static String decimalTypeMetadataKey;
  private static String dateTypeMetadataKey;
  private static String booleanTypeMetadataKey;

  @BeforeAll
  static void prepareTestData(
      @Autowired CreateDocumentSteps createDocumentSteps) {
    stringTypeMetadataKey = createDocumentSteps.getStringTypeMetadataKey();
    integerTypeMetadataKey = createDocumentSteps.getIntegerTypeMetadataKey();
    decimalTypeMetadataKey = createDocumentSteps.getDecimalTypeMetadataKey();
    dateTypeMetadataKey = createDocumentSteps.getDateTypeMetadataKey();
    booleanTypeMetadataKey = createDocumentSteps.getBooleanTypeMetadataKey();

    // For metadata equals cases.
    createDocumentSteps.uploadPdfDocumentWithSpecifiedNonMandatoryMetaData(
        Map.of(dateTypeMetadataKey, DateUtil.getCurrentFormattedDate(),
            integerTypeMetadataKey, INTEGER_DEFAULT_VALUE,
            decimalTypeMetadataKey, DECIMAL_DEFAULT_VALUE));

    // For expiration period DATE_ON comparator.
    createDocumentSteps.uploadPdfDocumentWithSpecifiedExpirationDate(
        DateUtil.getCurrentIsoDateTimePlusSpecifiedHours(1));
  }

  @Test
  @DisplayName("Get list of not deleted documents without filter Criteria")
  @Order(1)
  void getNotDeletedDocumentsWithoutFilterCriteriaTest() {
    Response filterResponse = filterDocumentsSteps.getNotDeletedFilteredDocuments();
    filterDocumentsSteps.checkDocumentsDefaultPerPageSize(
        filterDocumentsSteps.getCheckedDocumentDetailsObjectsFromResponse(filterResponse,
            "Get list of not deleted documents"));
  }

  //------------------Get documents filtered by expiration date------------------------
  @ParameterizedTest
  @DisplayName("Get documents filtered by expiration date")
  @Issue("Comparator 'DATE_ON_OR_BEFORE' check is disabled "
      + "due an issue: https://jira/browse/PRO-171737")
  @EnumSource(
      value = FilterComparator.class,
      names = {"DATE_ON", "DATE_BEFORE", "DATE_AFTER"})
  void getDocumentsFilteredByExpirationDateTest(FilterComparator dateComparator) {
    String expirationDate = filterDocumentsSteps.getDateForFilterByDateTest(dateComparator);
    Response filterResponse = filterDocumentsSteps.getDocumentsFilteredByExpirationDate(
        expirationDate, dateComparator.getValue());
    filterDocumentsSteps.checkDocumentsFilteredByExpirationDate(
        filterDocumentsSteps.getCheckedDocumentDetailsObjectsFromResponse(filterResponse,
            "Get documents filtered by expiration date"),
        expirationDate, dateComparator);
  }

  @DisplayName("Get documents filtered by expiration date in incorrect format")
  @Test
  void getDocumentsFilteredByExpirationDateUsingIncorrectDateFormatTest() {
    Response filterResponse = filterDocumentsSteps.getDocumentsFilteredByExpirationDate(
        "12/02/2024", FilterComparator.DATE_BEFORE.getValue());
    filterDocumentsSteps.checkInvalidFilterResponse(filterResponse,
        "Get documents filtered by expiration date in incorrect format");
  }

  @DisplayName("Get documents filtered by expiration date with no value provided")
  @Test
  void getDocumentsFilteredByExpirationDateUsingEmptyDateTest() {
    Response filterResponse = filterDocumentsSteps.getDocumentsFilteredByExpirationDate(null,
        FilterComparator.DATE_BEFORE.getValue());
    filterDocumentsSteps.checkInvalidFilterResponse(filterResponse,
        "Get documents filtered by expiration date with no value provided");
  }

  @DisplayName("Get documents filtered by expiration date with incorrect comparator")
  @Test
  void getDocumentsFilteredByExpirationDateUsingIncorrectComparatorTest() {
    String expirationDate = DateUtil.getCurrentFormattedDatePlusMonths(20);
    Response filterResponse =
        filterDocumentsSteps.getDocumentsFilteredByExpirationDate(expirationDate, "TST");
    filterDocumentsSteps.checkInvalidFieldComparatorResponse(filterResponse,
        "Get documents filtered by expiration date with incorrect comparator");
  }

  @DisplayName("Get documents filtered by expiration date without comparison data")
  @Test
  void getDocumentsFilteredByExpirationDateWithoutComparisonDataTest() {
    // A list of all not deleted documents is expected without any filter applied.
    filterDocumentsSteps.getCheckedDocumentDetailsObjectsFromResponse(
        filterDocumentsSteps.getDocumentsFilteredByExpirationDateWithoutParams(),
        "Get documents filtered by expiration date without comparison data");
  }

  //------------------Get documents filtered by document category----------------------
  @Test
  @Disabled("Filter by document category is not implemented yet: PRO-178792")
  @DisplayName("Get documents filtered by document category")
  void getDocumentsFilteredByDocumentTypesTest() {
    String documentCategory = TEST_DOCUMENT_CATEGORY_CODE;
    Response filterResponse =
        filterDocumentsSteps.getDocumentsFilteredByDocumentCategory(documentCategory);
    filterDocumentsSteps.checkDocumentsFilteredByDocumentCategory(
        filterDocumentsSteps.getCheckedDocumentDetailsObjectsFromResponse(filterResponse,
            "Get documents filtered by document category"), documentCategory);
  }

  @Test
  @Disabled("Filter by document category is not implemented yet: PRO-178792")
  @DisplayName("Get documents filtered by document category with no value provided")
  void getDocumentsFilteredByNotProvidedDocumentCategoryTest() {
    // A list of all not deleted documents is expected without any filter applied.
    filterDocumentsSteps.getCheckedDocumentDetailsObjectsFromResponse(
        filterDocumentsSteps.getDocumentsFilteredByDocumentCategory(null),
        "Get documents filtered by document category with no value provided");
  }

  //------------------Get documents filtered by lock status----------------------------
  @ParameterizedTest
  @DisplayName("Get documents filtered by lock status")
  @ValueSource(strings = {LOCK_STATUS_ACTIVE_VALUE, LOCK_STATUS_READ_ONLY_VALUE})
  void getDocumentsFilteredByLockStatusTest(String lockStatus) {
    Response filterResponse = filterDocumentsSteps.getDocumentsFilteredByLockStatus(lockStatus);
    filterDocumentsSteps.checkDocumentsFilteredByLockStatus(
        filterDocumentsSteps.getCheckedDocumentDetailsObjectsFromResponse(filterResponse,
            "Get documents filtered by lock status"), lockStatus);
  }

  @Test
  @DisplayName("Get documents filtered by invalid lock status")
  void getDocumentsFilteredByInvalidLockStatusTest() {
    Response filterResponse = filterDocumentsSteps.getDocumentsFilteredByLockStatus("TEST_ONLY");
    filterDocumentsSteps.checkResponseStatusCodeIs400(filterResponse,
        "Get documents filtered by invalid lock status");
    filterDocumentsSteps.checkResponseErrorMessage(filterResponse,
        ResponseErrorModel.INVALID_FILTER_FIELD);
  }

  @Test
  @DisplayName("Get documents filtered by not provided lock status")
  void getDocumentsFilteredByNotProvidedLockStatusTest() {
    // A list of all not deleted documents is expected without any filter applied.
    filterDocumentsSteps.getCheckedDocumentDetailsObjectsFromResponse(
        filterDocumentsSteps.getDocumentsFilteredByLockStatus(null),
        "Get documents filtered by not provided lock status");
  }

  //------------------Get documents filtered by uploaded date--------------------------
  @ParameterizedTest
  @DisplayName("Get documents filtered by uploaded date")
  @Issue("Comparator 'DATE_ON_OR_BEFORE' check is disabled "
      + "due an issue: https://jira/browse/PRO-171737")
  @EnumSource(
      value = FilterComparator.class,
      names = {"DATE_ON", "DATE_BEFORE", "DATE_AFTER"})
  void getDocumentsFilteredByUploadedDateTest(FilterComparator dateComparator) {
    String uploadedDate = filterDocumentsSteps.getDateForFilterByDateTest(dateComparator);
    Response filterResponse = filterDocumentsSteps.getDocumentsFilteredByUploadedDate(
        uploadedDate, dateComparator.getValue());
    filterDocumentsSteps.checkDocumentsFilteredByUploadedDate(
        filterDocumentsSteps.getCheckedDocumentDetailsObjectsFromResponse(filterResponse,
            "Get documents filtered by uploaded date"), uploadedDate, dateComparator);
  }

  @DisplayName("Get documents filtered by uploaded date in incorrect format")
  @Test
  void getDocumentsFilteredByUploadedDateUsingIncorrectDateFormatTest() {
    Response filterResponse = filterDocumentsSteps.getDocumentsFilteredByUploadedDate(
        "12/02/2024", FilterComparator.DATE_BEFORE.getValue());
    filterDocumentsSteps.checkInvalidFilterResponse(filterResponse,
        "Get documents filtered by uploaded date in incorrect format");
  }

  @DisplayName("Get documents filtered by uploaded date with no value provided")
  @Test
  void getDocumentsFilteredByUploadedDateUsingEmptyDateTest() {
    Response filterResponse = filterDocumentsSteps.getDocumentsFilteredByUploadedDate(null,
        FilterComparator.DATE_BEFORE.getValue());
    filterDocumentsSteps.checkInvalidFilterResponse(filterResponse,
        "Get documents filtered by uploaded date with no value provided");
  }

  @DisplayName("Get documents filtered by uploaded date with incorrect comparator")
  @Test
  void getDocumentsFilteredByUploadedDateUsingIncorrectComparatorTest() {
    String uploadedDate = DateUtil.getCurrentFormattedDatePlusMonths(20);
    Response filterResponse =
        filterDocumentsSteps.getDocumentsFilteredByUploadedDate(uploadedDate, "TST");
    filterDocumentsSteps.checkInvalidFieldComparatorResponse(filterResponse,
        "Get documents filtered by uploaded date with incorrect comparator");
  }

  @DisplayName("Get documents filtered by uploaded date without comparison data")
  @Test
  void getDocumentsFilteredByUploadedDateWithoutComparisonDataTest() {
    // A list of all not deleted documents is expected without any filter applied.
    filterDocumentsSteps.getCheckedDocumentDetailsObjectsFromResponse(
        filterDocumentsSteps.getDocumentsFilteredByUploadedDateWithoutParams(),
        "Get documents filtered by uploaded date without comparison data");
  }

  //------------------Get documents filtered by metadata-------------------------------
  //-----------------------------String type metadata-------------------------------
  @ParameterizedTest
  @DisplayName("Get documents filtered by meta in String type with specified key and value")
  @EnumSource(
      value = FilterComparator.class,
      names = {"EQUALS", "NOT_EQUALS", "CONTAINS", "DOES_NOT_CONTAIN", "STARTS_WITH", "ENDS_WITH"})
  void getDocumentsFilteredByStringMetaTest(FilterComparator comparator) {
    MetadataType metadataType = MetadataType.STRING;
    Map<String, String> metadata = Map.of(stringTypeMetadataKey, METADATA_STRING_VALUE);
    Response filterResponse = filterDocumentsSteps.getDocumentsFilteredByMeta(
        metadata, metadataType.getValue(), comparator.getValue());
    filterDocumentsSteps.checkDocumentsFilteredByMeta(
        filterDocumentsSteps.getCheckedDocumentDetailsObjectsFromResponse(filterResponse,
            "Get documents filtered by meta in String type with specified key and value"),
        metadata, metadataType, comparator);
  }

  @Test
  @DisplayName("Get documents filtered by meta in String type with specified key only")
  void getDocumentsFilteredByStringWithoutValueTest() {
    MetadataType metadataType = MetadataType.STRING;
    Map<String, String> metadata = Map.of(stringTypeMetadataKey, "");
    FilterComparator comparator = FilterComparator.CONTAINS;
    Response filterResponse = filterDocumentsSteps.getDocumentsFilteredByMeta(
        metadata, metadataType.getValue(), comparator.getValue());
    filterDocumentsSteps.checkDocumentsFilteredByMeta(
        filterDocumentsSteps.getCheckedDocumentDetailsObjectsFromResponse(filterResponse,
            "Get documents filtered by meta in String type with specified key only"),
        metadata, metadataType, comparator);
  }

  @Test
  @DisplayName("Get documents filtered by meta in String type with incompatible comparator")
  void getDocumentsFilteredByStringMetaUsingIncompatibleComparatorTest() {
    Response filterResponse = filterDocumentsSteps.getDocumentsFilteredByMeta(
        Map.of(stringTypeMetadataKey, "100"),
        MetadataType.STRING.getValue(), FilterComparator.LESS_THAN.getValue());
    filterDocumentsSteps.checkInvalidFieldComparatorResponse(filterResponse,
        "Get documents filtered by meta in String type with incompatible comparator");
  }

  //------------------Get documents filtered by metadata-------------------------------
  //-----------------------------Date type metadata-------------------------------
  @ParameterizedTest
  @DisplayName("Get documents filtered by meta in Date type with specified key and value")
  @EnumSource(
      value = FilterComparator.class,
      names = {"DATE_ON", "DATE_ON_OR_BEFORE", "DATE_BEFORE", "DATE_AFTER", "DATE_ON_OR_AFTER"})
  void getDocumentsFilteredByDateMetaTest(FilterComparator comparator) {
    Map<String, String> metadata =
        Map.of(filterDocumentsSteps.getDateTypeMetadataKey(), DateUtil.getCurrentFormattedDate());
    MetadataType metadataType = MetadataType.DATE;

    Response filterResponse = filterDocumentsSteps.getDocumentsFilteredByMeta(
        metadata, metadataType.getValue(), comparator.getValue());
    filterDocumentsSteps.checkDocumentsFilteredByMeta(
        filterDocumentsSteps.getCheckedDocumentDetailsObjectsFromResponse(filterResponse,
            "Get documents filtered by meta in Date type with specified key and value"),
        metadata, metadataType, comparator);
  }

  @Test
  @DisplayName("Get documents filtered by meta in Date type with specified key only")
  void getDocumentsFilteredByDateMetaWithoutValueTest() {
    Response filterResponse = filterDocumentsSteps.getDocumentsFilteredByMeta(
        filterDocumentsSteps.getMetaWithNullValue(dateTypeMetadataKey),
        MetadataType.DATE.getValue(), FilterComparator.DATE_AFTER.getValue());
    filterDocumentsSteps.checkResponseStatusCodeIs400(filterResponse,
        "Get documents filtered by meta in Date type with specified key only");
  }

  @Test
  @DisplayName("Get documents filtered by meta in Date type with incompatible value")
  void getDocumentsFilteredByDateMetaUsingIncompatibleValueTest() {
    Response filterResponse = filterDocumentsSteps.getDocumentsFilteredByMeta(
        Map.of(dateTypeMetadataKey, TEST),
        MetadataType.DATE.getValue(), FilterComparator.DATE_ON_OR_AFTER.getValue());
    filterDocumentsSteps.checkInvalidFilterResponse(filterResponse,
        "Get documents filtered by meta in Date type with incompatible value");
  }

  @Test
  @DisplayName("Get documents filtered by meta in Date type with incompatible comparator")
  void getDocumentsFilteredByDateMetaUsingIncompatibleComparatorTest() {
    Response filterResponse = filterDocumentsSteps.getDocumentsFilteredByMeta(
        Map.of(dateTypeMetadataKey, DateUtil.getCurrentFormattedDate()),
        MetadataType.DATE.getValue(), FilterComparator.LESS_THAN.getValue());
    filterDocumentsSteps.checkInvalidFieldComparatorResponse(filterResponse,
        "Get documents filtered by meta in Date type with incompatible comparator");
  }

  //------------------Get documents filtered by metadata-------------------------------
  //-----------------------------Decimal type metadata----------------------------
  @ParameterizedTest
  @DisplayName("Get documents filtered by meta in Decimal type with specified key and value")
  @EnumSource(
      value = FilterComparator.class,
      names = {"EQUALS", "NOT_EQUALS", "GREATER_THAN", "GREATER_THAN_OR_EQUALS", "LESS_THAN",
          "LESS_THAN_OR_EQUALS"})
  void getDocumentsFilteredByDecimalMetaTest(FilterComparator comparator) {
    Map<String, String> metadata = Map.of(decimalTypeMetadataKey, DECIMAL_DEFAULT_VALUE);
    MetadataType metadataType = MetadataType.DECIMAL;
    Response filterResponse = filterDocumentsSteps.getDocumentsFilteredByMeta(
        metadata, metadataType.getValue(), comparator.getValue());
    filterDocumentsSteps.checkDocumentsFilteredByMeta(
        filterDocumentsSteps.getCheckedDocumentDetailsObjectsFromResponse(filterResponse,
            "Get documents filtered by meta in Decimal type with specified key and value"),
        metadata, metadataType, comparator);
  }

  @Test
  @DisplayName("Get documents filtered by meta in Decimal type with specified key only")
  void getDocumentsFilteredByDecimalMetaWithoutValueTest() {
    Response filterResponse = filterDocumentsSteps.getDocumentsFilteredByMeta(
        filterDocumentsSteps.getMetaWithNullValue(decimalTypeMetadataKey),
        MetadataType.DECIMAL.getValue(), FilterComparator.GREATER_THAN.getValue());
    filterDocumentsSteps.checkResponseStatusCodeIs400(filterResponse,
        "Get documents filtered by Decimal in Date type with specified key only");
  }

  @Test
  @DisplayName("Get documents filtered by meta in Decimal type with incompatible value")
  void getDocumentsFilteredByDecimalMetaUsingIncompatibleValueTest() {
    Response filterResponse = filterDocumentsSteps.getDocumentsFilteredByMeta(
        Map.of(decimalTypeMetadataKey, TEST),
        MetadataType.DECIMAL.getValue(), FilterComparator.GREATER_THAN.getValue());
    filterDocumentsSteps.checkInvalidFilterResponse(filterResponse,
        "Get documents filtered by meta in Decimal type with incompatible value");
  }

  @Test
  @DisplayName("Get documents filtered by meta in Decimal type with incompatible comparator")
  void getDocumentsFilteredByDecimalMetaUsingIncompatibleComparatorTest() {
    Response filterResponse = filterDocumentsSteps.getDocumentsFilteredByMeta(
        Map.of(decimalTypeMetadataKey, "22"),
        MetadataType.DECIMAL.getValue(), FilterComparator.DATE_ON_OR_AFTER.getValue());
    filterDocumentsSteps.checkInvalidFieldComparatorResponse(filterResponse,
        "Get documents filtered by meta in Decimal type with incompatible comparator");
  }

  //------------------Get documents filtered by metadata-------------------------------
  //-----------------------------Integer type metadata----------------------------
  @ParameterizedTest
  @DisplayName("Get documents filtered by meta in Integer type with specified key and value")
  @EnumSource(
      value = FilterComparator.class,
      names = {"EQUALS", "NOT_EQUALS", "GREATER_THAN", "GREATER_THAN_OR_EQUALS", "LESS_THAN",
          "LESS_THAN_OR_EQUALS"})
  void getDocumentsFilteredByIntegerMetaTest(FilterComparator comparator) {
    Map<String, String> metadata = Map.of(integerTypeMetadataKey, INTEGER_DEFAULT_VALUE);
    MetadataType metadataType = MetadataType.INTEGER;
    Response filterResponse = filterDocumentsSteps.getDocumentsFilteredByMeta(
        metadata, metadataType.getValue(), comparator.getValue());
    filterDocumentsSteps.checkDocumentsFilteredByMeta(
        filterDocumentsSteps.getCheckedDocumentDetailsObjectsFromResponse(filterResponse,
            "Get documents filtered by meta in Integer type with specified key and value"),
        metadata, metadataType, comparator);
  }

  @Test
  @DisplayName("Get documents filtered by meta in Integer type with specified key only")
  void getDocumentsFilteredByIntegerMetaWithoutValueTest() {
    Response filterResponse = filterDocumentsSteps.getDocumentsFilteredByMeta(
        filterDocumentsSteps.getMetaWithNullValue(integerTypeMetadataKey),
        MetadataType.INTEGER.getValue(), FilterComparator.GREATER_THAN_OR_EQUALS.getValue());
    filterDocumentsSteps.checkResponseStatusCodeIs400(filterResponse,
        "Get documents filtered by Integer in Date type with specified key only");
  }

  @Test
  @DisplayName("Get documents filtered by meta in Integer type with incompatible value")
  void getDocumentsFilteredByIntegerMetaUsingIncompatibleValueTest() {
    Response filterResponse = filterDocumentsSteps.getDocumentsFilteredByMeta(
        Map.of(integerTypeMetadataKey, TEST),
        MetadataType.INTEGER.getValue(), FilterComparator.LESS_THAN.getValue());
    filterDocumentsSteps.checkInvalidFilterResponse(filterResponse,
        "Get documents filtered by meta in Integer type with incompatible value");
  }

  @Test
  @DisplayName("Get documents filtered by meta in Integer type with incompatible comparator")
  void getDocumentsFilteredByIntegerMetaUsingIncompatibleComparatorTest() {
    Response filterResponse = filterDocumentsSteps.getDocumentsFilteredByMeta(
        Map.of(integerTypeMetadataKey, "22"),
        MetadataType.INTEGER.getValue(), FilterComparator.DATE_ON_OR_AFTER.getValue());
    filterDocumentsSteps.checkInvalidFieldComparatorResponse(filterResponse,
        "Get documents filtered by meta in Integer type with incompatible comparator");
  }

  //------------------Get documents filtered by metadata-------------------------------
  //-----------------------------Boolean type metadata----------------------------
  @ParameterizedTest
  @DisplayName("Get documents filtered by meta in Boolean type with specified key and value")
  @EnumSource(
      value = FilterComparator.class,
      names = {"IS", "NOT"})
  void getDocumentsFilteredByBooleanMetaTest(FilterComparator comparator) {
    Map<String, String> metadata = Map.of(booleanTypeMetadataKey, "true");
    MetadataType metadataType = MetadataType.BOOLEAN;
    Response filterResponse = filterDocumentsSteps.getDocumentsFilteredByMeta(
        metadata, metadataType.getValue(), comparator.getValue());
    filterDocumentsSteps.checkDocumentsFilteredByMeta(
        filterDocumentsSteps.getCheckedDocumentDetailsObjectsFromResponse(filterResponse,
            "Get documents filtered by meta in Boolean type with specified key and value"),
        metadata, metadataType, comparator);
  }

  @Test
  @DisplayName("Get documents filtered by meta in Boolean type with specified key only")
  void getDocumentsFilteredByBooleanMetaWithoutValueTest() {
    Response filterResponse = filterDocumentsSteps.getDocumentsFilteredByMeta(
        filterDocumentsSteps.getMetaWithNullValue(booleanTypeMetadataKey),
        MetadataType.BOOLEAN.getValue(), FilterComparator.IS.getValue());
    filterDocumentsSteps.checkResponseStatusCodeIs400(filterResponse,
        "Get documents filtered by Boolean in Date type with specified key only");
  }

  @Test
  @DisplayName("Get documents filtered by meta in Boolean type with incompatible value")
  void getDocumentsFilteredByBooleanMetaUsingIncompatibleValueTest() {
    Response filterResponse = filterDocumentsSteps.getDocumentsFilteredByMeta(
        Map.of(booleanTypeMetadataKey, TEST),
        MetadataType.BOOLEAN.getValue(), FilterComparator.IS.getValue());
    filterDocumentsSteps.checkInvalidFilterResponse(filterResponse,
        "Get documents filtered by meta in Boolean type with incompatible value");
  }

  @Test
  @DisplayName("Get documents filtered by meta in Boolean type with incompatible comparator")
  void getDocumentsFilteredByBooleanMetaUsingIncompatibleComparatorTest() {
    Response filterResponse = filterDocumentsSteps.getDocumentsFilteredByMeta(
        Map.of(booleanTypeMetadataKey, "false"),
        MetadataType.BOOLEAN.getValue(), FilterComparator.EQUALS.getValue());
    filterDocumentsSteps.checkInvalidFieldComparatorResponse(filterResponse,
        "Get documents filtered by meta in Boolean type with incompatible comparator");
  }

  //---------------------------Additional cases -----------------------------------------------
  @Test
  @DisplayName("Get documents filtered by unsupported filter")
  void getDocumentsFilteredByUnsupportedFilterTest() {
    Response filterResponse = filterDocumentsSteps.getDocumentsFilteredByCustomFilter(
        "fileQuality", "good");
    filterDocumentsSteps.checkResponseStatusCodeIs400(filterResponse,
        "Get documents filtered by unsupported filter");
    filterDocumentsSteps.checkResponseErrorMessageWithoutErrorModel(filterResponse,
        ResponseErrorModel.INVALID_REQUEST);
  }

  @Test
  @DisplayName("Get filtered documents using expired authorization token")
  void getFilteredDocumentsWithExpiredAuthorizationTokenTest() {
    Response filterResponse =
        filterDocumentsSteps.getFilteredDocumentsUsingExpiredAuthorizationToken();
    filterDocumentsSteps.checkResponseStatusCodeIs401(filterResponse,
        "Get filtered documents using expired authorization token");
  }

  @Test
  @DisplayName("Get filtered documents using invalid authorization token")
  void getFilteredDocumentsWithInvalidAuthorizationTokenTest() {
    Response filterResponse =
        filterDocumentsSteps.filteredDocumentsUsingInvalidAuthorizationToken();
    filterDocumentsSteps.checkResponseStatusCodeIs401(filterResponse,
        "Get filtered documents using invalid authorization token");
  }

  @Test
  @DisplayName("Get filtered documents using empty authorization token value")
  void getFilteredDocumentsWithEmptyAuthorizationTokenTest() {
    Response filterResponse =
        filterDocumentsSteps.getFilteredDocumentsUsingEmptyAuthorizationToken();
    filterDocumentsSteps.checkResponseStatusCodeIs401(filterResponse,
        "Get filtered documents using empty authorization token");
  }

  @Test
  @DisplayName("Get filtered documents without authorization token value")
  void getFilteredDocumentsWithoutAuthorizationTokenTest() {
    Response filterResponse =
        filterDocumentsSteps.getFilteredDocumentsWithoutAuthorizationToken();
    filterDocumentsSteps.checkResponseStatusCodeIs401(filterResponse,
        "Get filtered documents without authorization token");
  }

  @Test
  @DisplayName("Get filtered documents using invalid User Id")
  void getFilteredDocumentsWithInvalidUserIdTest() {
    Response filterResponse =
        filterDocumentsSteps.getFilteredDocumentsUsingInvalidUserId();
    filterDocumentsSteps.checkResponseStatusCodeIs400(filterResponse,
        "Get filtered documents using invalid User Id");
    filterDocumentsSteps.checkUserIdResponseErrorMessage(filterResponse);
  }

  @Test
  @DisplayName("Get filtered documents with empty User Id")
  void getFilteredDocumentsWithEmptyUserIdTest() {
    Response filterResponse =
        filterDocumentsSteps.getFilteredDocumentsUsingEmptyUserId();
    filterDocumentsSteps.checkResponseStatusCodeIs400(filterResponse,
        "Get filtered documents with empty User Id");
    filterDocumentsSteps.checkUserIdResponseErrorMessage(filterResponse);
  }

  @Test
  @DisplayName("Get filtered documents without User Id")
  void getFilteredDocumentsWithoutUserIdTest() {
    Response filterResponse =
        filterDocumentsSteps.getFilteredDocumentsWithoutUserId();
    filterDocumentsSteps.checkResponseStatusCodeIs400(filterResponse,
        "Get filtered documents without User Id");
    filterDocumentsSteps.checkUserIdResponseErrorMessage(filterResponse);
  }

  @Test
  @DisplayName("Get filtered documents using specified correlation id header")
  void getDocumentsUsingCorrectCorrelationIdHeaderTest() {
    String uuid = TestDataUtil.generateUuid();
    Response filteredDocuments =
        filterDocumentsSteps.getDocumentsUsingSpecifiedCorrelationIdHeader(uuid);
    filterDocumentsSteps.checkResponseStatusCodeIs200(filteredDocuments,
        "Get filtered documents using specified correlation id header");
    filterDocumentsSteps.checkCorrelationIdValueInResponse(filteredDocuments, uuid);
  }

  @Test
  @DisplayName("Get filtered documents using specified invalid correlation id header")
  void getDocumentsUsingInvalidCorrelationIdHeaderTest() {
    String uuid = TestDataUtil.getRandomString(10);
    Response filteredDocuments =
        filterDocumentsSteps.getDocumentsUsingSpecifiedCorrelationIdHeader(uuid);
    filterDocumentsSteps.checkResponseStatusCodeIs200(filteredDocuments,
        "Get filtered documents using specified invalid correlation id header");
    filterDocumentsSteps.checkCorrelationIdValueInIsNotPresentInResponse(filteredDocuments, uuid);
  }

  @Test
  @DisplayName("Get filtered documents using unsupported 'Accept' header value")
  void getFilteredDocumentsWithInvalidAcceptHeaderTest() {
    Response filterResponse =
        filterDocumentsSteps.getFilteredDocumentsUsingUnsupportedAcceptHeader();
    filterDocumentsSteps.checkResponseStatusCodeIs406(filterResponse,
        "Get filtered documents using unsupported 'Accept' header value");
    filterDocumentsSteps.checkResponseErrorMessageWithoutErrorModel(filterResponse,
        ResponseErrorModel.NOT_ACCEPTABLE);
  }

  @Test
  @DisplayName("Get filtered documents using unsupported 'Content-Type' header value")
  void getFilteredDocumentsWithInvalidContentTypeHeaderTest() {
    Response filterResponse =
        filterDocumentsSteps.getFilteredDocumentsUsingUnsupportedContentTypeHeader();
    filterDocumentsSteps.checkResponseStatusCodeIs415(filterResponse,
        "Get filtered documents using unsupported 'Content-Type' header value");
    filterDocumentsSteps.checkResponseErrorMessageWithoutErrorModel(filterResponse,
        ResponseErrorModel.UNSUPPORTED_CONTENT_TYPE);
  }

  @Test
  @DisplayName("Get filtered documents without 'Content-Type' header")
  void getFilteredDocumentsWithNotProvidedContentTypeHeaderTest() {
    Response filterResponse = filterDocumentsSteps.getFilteredDocumentsWithoutContentTypeHeader();
    filterDocumentsSteps.checkResponseStatusCodeIs415(filterResponse,
        "Get filtered documents without 'Content-Type' header");
    filterDocumentsSteps.checkResponseErrorMessageWithoutErrorModel(filterResponse,
        ResponseErrorModel.CONTENT_TYPE_HEADER_MISSED);
  }
}
