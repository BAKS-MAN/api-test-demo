package com.baks.tests;

import static com.baks.constants.ConfigurationConstants.PAGE_SIZE_DEFAULT_VALUE;

import com.baks.pojo.response.DocumentDetailsObject;
import com.baks.runners.EndpointTestRunner;
import com.baks.steps.FilterDocumentsSteps;
import com.baks.utils.TestDataUtil;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.api.parallel.Isolated;
import org.springframework.beans.factory.annotation.Autowired;

@Isolated
@Execution(ExecutionMode.CONCURRENT)
@Epic("Document filter operations")
@Feature("Documents filter API")
@DisplayName("Filter not deleted documents tests")
class FilterDocumentsPaginationTest extends EndpointTestRunner {

  @Autowired
  private FilterDocumentsSteps filterDocumentsSteps;

  @Test
  @DisplayName("Get filtered documents with specified page size")
  void getFilteredDocumentsWithSpecifiedPageSizeTest() {
    int pageSize = TestDataUtil.getRandomInteger(1, 1000);
    Response filterResponse = filterDocumentsSteps.getListOfDocumentsWithPageSize(pageSize);
    filterDocumentsSteps.checkResponseStatusCodeIs200(filterResponse,
        "Get filtered documents with specified page size");
    List<DocumentDetailsObject> listOfDocuments =
        filterDocumentsSteps.getDocumentDetailsObjectsListFromResponse(filterResponse);
    filterDocumentsSteps.checkNotDeletedDocumentsPerPageSize(listOfDocuments, pageSize);
  }

  @Test
  @DisplayName("Get filtered documents without specified page size value")
  void getFilteredDocumentsWithoutSpecifiedPageSizeTest() {
    Response filterResponse = filterDocumentsSteps.getListOfDocumentsWithPageSize(null);
    // Successful response with default page size is expected.
    checkDefaultPageSizeResponse(filterResponse,
        "Get filtered documents without specified page size value");
  }

  @Test
  @DisplayName("Get filtered documents with specified exceeded page size")
  void getFilteredDocumentsWithSpecifiedExceedsPageSizeTest() {
    Response filterResponse = filterDocumentsSteps.getListOfDocumentsWithPageSize(1001);
    filterDocumentsSteps.checkResponseStatusCodeIs400(filterResponse,
        "Get filtered documents with specified exceeded page size");
    filterDocumentsSteps.checkExceededPageSizeErrorMsg(filterResponse);
  }

  @Test
  @DisplayName("Get filtered documents with specified zero page size")
  void getFilteredDocumentsWithSpecifiedZeroPageSizeTest() {
    Response filterResponse = filterDocumentsSteps.getListOfDocumentsWithPageSize(0);
    filterDocumentsSteps.checkResponseStatusCodeIs400(filterResponse,
        "Get filtered documents with specified zero page size");
    filterDocumentsSteps.checkIncorrectPageSizeErrorMsg(filterResponse);
  }

  @Test
  @DisplayName("Get filtered documents with specified negative page size")
  void getFilteredDocumentsWithSpecifiedNegativePageSizeTest() {
    Response filterResponse = filterDocumentsSteps.getListOfDocumentsWithPageSize(
        TestDataUtil.getRandomNegativeInteger());
    filterDocumentsSteps.checkResponseStatusCodeIs400(filterResponse,
        "Get filtered documents with specified negative page size");
    filterDocumentsSteps.checkIncorrectPageSizeErrorMsg(filterResponse);
  }

  @Test
  @DisplayName("Get filtered documents with specified invalid page size value")
  void getFilteredDocumentsWithSpecifiedIncompatiblePageSizeValueTest() {
    String invalidValue = "fifty";
    Response filterResponse = filterDocumentsSteps.getListOfDocumentsWithPageSize(invalidValue);
    filterDocumentsSteps.checkResponseStatusCodeIs400(filterResponse,
        "Get filtered documents with specified invalid page size value");
    filterDocumentsSteps.checkInvalidPageSizeErrorMsg(filterResponse, invalidValue);
  }

  @Test
  @DisplayName("Get filtered documents without specified page number value")
  void getFilteredDocumentsWithoutSpecifiedPageNumberTest() {
    Response filterResponse = filterDocumentsSteps.getListOfDocumentsWithPagination(null);
    // Successful response with default page size is expected.
    checkDefaultPageSizeResponse(filterResponse,
        "Get filtered documents without specified page number value");
  }

  @Test
  @DisplayName("Get filtered documents with specified exceeded page number")
  void getFilteredDocumentsWithSpecifiedExceedsPageNumberTest() {
    int pageNumber = filterDocumentsSteps.getAvailablePageQuantity(PAGE_SIZE_DEFAULT_VALUE) + 1;
    filterDocumentsSteps.checkFilteredDocumentsResponseIsEmpty(
        filterDocumentsSteps.getListOfDocumentsWithPagination(pageNumber));
  }

  @Test
  @DisplayName("Get filtered documents with specified zero page number")
  void getFilteredDocumentsWithSpecifiedZeroPageNumberTest() {
    Response filterResponse = filterDocumentsSteps.getListOfDocumentsWithPagination(0);
    filterDocumentsSteps.checkResponseStatusCodeIs400(filterResponse,
        "Get filtered documents with specified zero page number");
    filterDocumentsSteps.checkIncorrectPageNumberErrorMsg(filterResponse);
  }

  @Test
  @DisplayName("Get filtered documents with specified negative page number")
  void getFilteredDocumentsWithSpecifiedNegativePageNumberTest() {
    Response filterResponse = filterDocumentsSteps.getListOfDocumentsWithPagination(
        TestDataUtil.getRandomNegativeInteger());
    filterDocumentsSteps.checkResponseStatusCodeIs400(filterResponse,
        "Get filtered documents with specified negative page number");
    filterDocumentsSteps.checkIncorrectPageNumberErrorMsg(filterResponse);
  }

  @Test
  @DisplayName("Get filtered documents with specified invalid page number value")
  void getFilteredDocumentsWithSpecifiedIncompatiblePageNumberValueTest() {
    String invalidValue = "fifty";
    Response filterResponse = filterDocumentsSteps.getListOfDocumentsWithPagination(invalidValue);
    filterDocumentsSteps.checkResponseStatusCodeIs400(filterResponse,
        "Get filtered documents with specified invalid page number value");
    filterDocumentsSteps.checkInvalidPageNumberErrorMsg(filterResponse, invalidValue);
  }

  @Test
  @DisplayName("Get filtered documents with specified page number and page size")
  void getFilteredDocumentsWithSpecifiedPageNumberAndSizeTest() {
    int pageSize = TestDataUtil.getRandomInteger(1, 1000);
    int totalPageQty = filterDocumentsSteps.getAvailablePageQuantity(pageSize);
    int pageNumber = TestDataUtil.getRandomInteger(1, totalPageQty);
    Response filterResponse = filterDocumentsSteps.getListOfDocumentsWithPaginationAndPageSize(
        pageNumber, pageSize);
    filterDocumentsSteps.checkResponseStatusCodeIs200(filterResponse,
        "Get filtered documents with specified page number");
    List<DocumentDetailsObject> listOfDocuments =
        filterDocumentsSteps.getDocumentDetailsObjectsListFromResponse(filterResponse);
    filterDocumentsSteps.checkNotDeletedDocumentsPerPageNumberAndSize(listOfDocuments, pageNumber,
        pageSize);
  }

  //------------------Get filtered documents using alternative pagination: offset and limit----
  @Test
  @DisplayName("Get filtered documents with specified page limit")
  void getFilteredDocumentsWithSpecifiedPageLimitTest() {
    int limitValue = TestDataUtil.getRandomInteger(1, 1000);
    Response filterResponse = filterDocumentsSteps.getListOfDocumentsWithPageLimit(limitValue);
    filterDocumentsSteps.checkResponseStatusCodeIs200(filterResponse,
        "Get filtered documents with specified page limit");
    List<DocumentDetailsObject> listOfDocuments =
        filterDocumentsSteps.getDocumentDetailsObjectsListFromResponse(filterResponse);
    filterDocumentsSteps.checkNotDeletedDocumentsPerPageSize(listOfDocuments, limitValue);
  }

  @Test
  @DisplayName("Get filtered documents without specified page limit value")
  void getFilteredDocumentsWithoutSpecifiedPageLimitTest() {
    Response filterResponse = filterDocumentsSteps.getListOfDocumentsWithPageLimit(null);
    // Successful response with default page size is expected.
    checkDefaultPageSizeResponse(filterResponse,
        "Get filtered documents without specified page limit value");
  }

  @Test
  @DisplayName("Get filtered documents with specified exceeded page limit")
  void getFilteredDocumentsWithSpecifiedExceedsPageLimitTest() {
    Response filterResponse = filterDocumentsSteps.getListOfDocumentsWithPageLimit(1001);
    filterDocumentsSteps.checkResponseStatusCodeIs400(filterResponse,
        "Get filtered documents with specified exceeded page limit");
    filterDocumentsSteps.checkExceededPageSizeErrorMsg(filterResponse);
  }

  @Test
  @DisplayName("Get filtered documents with specified zero page limit")
  void getFilteredDocumentsWithSpecifiedZeroPageLimitTest() {
    Response filterResponse = filterDocumentsSteps.getListOfDocumentsWithPageLimit(0);
    filterDocumentsSteps.checkResponseStatusCodeIs400(filterResponse,
        "Get filtered documents with specified zero page limit");
    filterDocumentsSteps.checkIncorrectPageSizeErrorMsg(filterResponse);
  }

  @Test
  @DisplayName("Get filtered documents with specified negative page limit")
  void getFilteredDocumentsWithSpecifiedNegativePageLimitTest() {
    Response filterResponse = filterDocumentsSteps.getListOfDocumentsWithPageLimit(
        TestDataUtil.getRandomNegativeInteger());
    filterDocumentsSteps.checkResponseStatusCodeIs400(filterResponse,
        "Get filtered documents with specified negative page limit");
    filterDocumentsSteps.checkIncorrectPageSizeErrorMsg(filterResponse);
  }

  @Test
  @DisplayName("Get filtered documents with specified invalid page limit value")
  void getFilteredDocumentsWithSpecifiedIncompatiblePageLimitValueTest() {
    String invalidValue = TestDataUtil.getRandomString(3);
    Response filterResponse = filterDocumentsSteps.getListOfDocumentsWithPageLimit(invalidValue);
    filterDocumentsSteps.checkResponseStatusCodeIs400(filterResponse,
        "Get filtered documents with specified invalid page limit value");
    filterDocumentsSteps.checkInvalidPageLimitErrorMsg(filterResponse, invalidValue);
  }

  @Test
  @DisplayName("Get filtered documents with specified page offset and limit values")
  void getFilteredDocumentsWithSpecifiedPageOffsetAndLimitTest() {
    int pageLimit = TestDataUtil.getRandomInteger(1, 1000);
    int maxOffsetValue = filterDocumentsSteps.getQuantityOfNotDeletedDocuments() - 1;
    int offsetValue = TestDataUtil.getRandomInteger(0, maxOffsetValue);
    Response filterResponse =
        filterDocumentsSteps.getListOfDocumentsWithPageOffsetAndLimit(offsetValue, pageLimit);
    filterDocumentsSteps.checkResponseStatusCodeIs200(filterResponse,
        "Get filtered documents with specified page offset");
    List<DocumentDetailsObject> listOfDocuments =
        filterDocumentsSteps.getDocumentDetailsObjectsListFromResponse(filterResponse);
    filterDocumentsSteps.checkNotDeletedDocumentsPageSizeWithOffset(listOfDocuments, offsetValue,
        pageLimit);
  }

  @Test
  @DisplayName("Get filtered documents without specified page offset value")
  void getFilteredDocumentsWithoutSpecifiedPageOffsetTest() {
    Response filterResponse = filterDocumentsSteps.getListOfDocumentsWithPagination(null);
    // Successful response with default page limit is expected.
    checkDefaultPageSizeResponse(filterResponse,
        "Get filtered documents without specified page offset value");
  }

  @Test
  @DisplayName("Get filtered documents with specified exceeded page offset")
  void getFilteredDocumentsWithSpecifiedExceedsPageOffsetTest() {
    int offsetValue = filterDocumentsSteps.getQuantityOfNotDeletedDocuments();
    filterDocumentsSteps.checkFilteredDocumentsResponseIsEmpty(
        filterDocumentsSteps.getListOfDocumentsWithOffset(offsetValue));
  }

  @Test
  @DisplayName("Get filtered documents with specified negative page offset")
  void getFilteredDocumentsWithSpecifiedNegativePageOffsetTest() {
    Response filterResponse = filterDocumentsSteps.getListOfDocumentsWithOffset(
        TestDataUtil.getRandomNegativeInteger());
    filterDocumentsSteps.checkResponseStatusCodeIs400(filterResponse,
        "Get filtered documents with specified negative page offset");
    filterDocumentsSteps.checkIncorrectPageOffsetErrorMsg(filterResponse);
  }

  @Test
  @DisplayName("Get filtered documents with specified invalid page offset value")
  void getFilteredDocumentsWithSpecifiedIncompatiblePageOffsetValueTest() {
    String invalidValue = TestDataUtil.getRandomString(3);
    Response filterResponse = filterDocumentsSteps.getListOfDocumentsWithOffset(invalidValue);
    filterDocumentsSteps.checkResponseStatusCodeIs400(filterResponse,
        "Get filtered documents with specified invalid page offset value");
    filterDocumentsSteps.checkInvalidPageOffsetErrorMsg(filterResponse, invalidValue);
  }

  private void checkDefaultPageSizeResponse(Response filterResponse, String assertionMessage) {
    filterDocumentsSteps.checkResponseStatusCodeIs200(filterResponse, assertionMessage);
    List<DocumentDetailsObject> listOfDocuments =
        filterDocumentsSteps.getDocumentDetailsObjectsListFromResponse(filterResponse);
    filterDocumentsSteps.checkDocumentsDefaultPerPageSize(listOfDocuments);
  }
}
