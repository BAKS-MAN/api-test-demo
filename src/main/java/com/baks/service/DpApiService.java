package com.baks.service;

import static com.baks.constants.ApiRequestsConstants.BEARER_HEADER_VALUE;
import static com.baks.constants.ApiRequestsConstants.CONTENT_TYPE_TEST;
import static com.baks.constants.ApiRequestsConstants.CORRELATION_ID_HEADER;
import static com.baks.constants.ApiRequestsConstants.DOCUMENT_CATEGORY_HEADER;
import static com.baks.constants.ApiRequestsConstants.DOCUMENT_TITLE_HEADER;
import static com.baks.constants.ApiRequestsConstants.USER_ID_HEADER;
import static com.baks.constants.TestDataConstants.INVALID_USER_ID;
import static com.baks.constants.TestDataConstants.USER_ID_VALUE;
import static io.restassured.config.JsonConfig.jsonConfig;
import static org.apache.http.HttpHeaders.ACCEPT;
import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;

import com.baks.config.GatewayConfiguration;
import com.baks.constants.TestDataConstants;
import com.baks.utils.TestDataUtil;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.config.HeaderConfig;
import io.restassured.config.LogConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.config.SSLConfig;
import io.restassured.http.ContentType;
import io.restassured.path.json.config.JsonPathConfig;
import io.restassured.specification.RequestSpecification;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Parent class for API requests. Contains REST Assured RequestSpecifications and related
 * configurations.
 */
public abstract class DpApiService {

  @Autowired
  private GatewayConfiguration gatewayConfiguration;
  @Autowired
  private AuthorizationService authorizationService;

  private RestAssuredConfig getRestAssuredConfig() {
    return RestAssuredConfig.newConfig()
        .jsonConfig(jsonConfig()
            .numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL))
        .sslConfig(new SSLConfig().allowAllHostnames().relaxedHTTPSValidation())
        .logConfig(LogConfig.logConfig().blacklistHeader(AUTHORIZATION))
        .headerConfig(HeaderConfig.headerConfig().overwriteHeadersWithName(
            CONTENT_TYPE, ACCEPT, CORRELATION_ID_HEADER, DOCUMENT_CATEGORY_HEADER,
            USER_ID_HEADER, DOCUMENT_TITLE_HEADER));
  }

  private RequestSpecification getBaseDpSpecification() {
    return RestAssured.given()
        .baseUri(gatewayConfiguration.getHost())
        .basePath(gatewayConfiguration.getPath())
        .contentType(ContentType.JSON)
        .config(getRestAssuredConfig())
        .relaxedHTTPSValidation()
        .filters(new AllureRestAssured());
  }

  /**
   * Creates RequestSpecification for authorized requests.
   *
   * @return RequestSpecification with valid authorization data
   */
  public RequestSpecification getAuthorizedDpSpecification() {
    return getAuthorizedDpSpecificationForUser(USER_ID_VALUE);
  }

  /**
   * Creates RequestSpecification for user specified authorized requests.
   *
   * @return RequestSpecification with valid authorization data
   */
  public RequestSpecification getAuthorizedDpSpecificationForUser(String userId) {
    return RestAssured.given(getBaseDpSpecification())
        .header(USER_ID_HEADER, userId)
        .header(AUTHORIZATION, authorizationService.getBearerToken())
        .header(CORRELATION_ID_HEADER, TestDataUtil.generateUuid())
        .log().headers();
  }

  /**
   * Creates RequestSpecification for requests where authorization token should be expired.
   *
   * @return RequestSpecification with expired authorization token
   */
  public RequestSpecification getDpSpecificationWithExpiredToken() {
    return RestAssured.given(getBaseDpSpecification())
        .header(AUTHORIZATION,
            String.format(BEARER_HEADER_VALUE, TestDataUtil.getTokensForTest().getExpiredToken()))
        .header(USER_ID_HEADER, TestDataConstants.USER_ID_VALUE);
  }

  /**
   * Creates RequestSpecification for requests where authorization token should be invalid.
   *
   * @return RequestSpecification with invalid authorization token
   */
  public RequestSpecification getDpSpecificationWithInvalidAuthorizationToken() {
    return RestAssured.given(getBaseDpSpecification())
        .header(AUTHORIZATION,
            String.format(BEARER_HEADER_VALUE, TestDataUtil.getTokensForTest().getInvalidToken()))
        .header(USER_ID_HEADER, TestDataConstants.USER_ID_VALUE);
  }

  /**
   * Creates RequestSpecification for requests where authorization token should be absent.
   *
   * @return RequestSpecification without authorization token
   */
  public RequestSpecification getSpecificationWithoutAuthorizationToken() {
    return RestAssured.given(getBaseDpSpecification())
        .header(USER_ID_HEADER, TestDataConstants.USER_ID_VALUE);
  }

  /**
   * Creates RequestSpecification for requests where authorization token should be empty.
   *
   * @return RequestSpecification with empty authorization token
   */
  public RequestSpecification getSpecificationWithEmptyAuthorizationToken() {
    return RestAssured.given(getBaseDpSpecification())
        .header(AUTHORIZATION, null)
        .header(USER_ID_HEADER, TestDataConstants.USER_ID_VALUE);
  }

  /**
   * Creates RequestSpecification for requests where UserId is invalid. 'invalid' means user id
   * is not in UUID format.
   *
   * @return RequestSpecification with invalid UserId
   */
  public RequestSpecification getSpecificationWithInvalidUserId() {
    return RestAssured.given(getBaseDpSpecification())
        .header(AUTHORIZATION, authorizationService.getBearerToken())
        .header(USER_ID_HEADER, INVALID_USER_ID);
  }

  /**
   * Creates RequestSpecification for requests where UserId should be absent.
   *
   * @return RequestSpecification without User id
   */
  public RequestSpecification getSpecificationWithoutUserId() {
    return RestAssured.given(getBaseDpSpecification())
        .header(AUTHORIZATION, authorizationService.getBearerToken());
  }

  /**
   * Creates RequestSpecification for requests where UserId value is empty.
   *
   * @return RequestSpecification with empty UserId value
   */
  public RequestSpecification getSpecificationWithEmptyUserId() {
    return RestAssured.given(getBaseDpSpecification())
        .header(AUTHORIZATION, authorizationService.getBearerToken())
        .header(USER_ID_HEADER, "");
  }

  /**
   * Creates RequestSpecification for authorized requests with unsupported Accept header.
   *
   * @return RequestSpecification with unsupported Accept header
   */
  public RequestSpecification getSpecificationWithUnsupportedAcceptHeader() {
    return RestAssured.given(getBaseDpSpecification())
        .header(AUTHORIZATION, authorizationService.getBearerToken())
        .header(USER_ID_HEADER, TestDataConstants.USER_ID_VALUE)
        .accept(CONTENT_TYPE_TEST);
  }
}
