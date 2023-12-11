package com.baks.service;

import static com.baks.constants.ApiRequestsConstants.BEARER_HEADER_VALUE;

import com.baks.exceptions.UnexpectedResponseException;
import com.baks.specs.TokenRequestSpecifications;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.net.HttpURLConnection;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Authorization service class. Generates access token for Dp requests.
 */
@Component
public class AuthorizationService {

  @Autowired
  TokenRequestSpecifications tokenRequestSpecifications;
  Logger log = LoggerFactory.getLogger(AuthorizationService.class);
  private static final String ACCESS_TOKEN_PATH = "access_token";
  private static final ThreadLocal<String> ACCESS_TOKEN = new ThreadLocal<>();
  private static final ThreadLocal<LocalDateTime> LAST_REQUEST_TIME = new ThreadLocal<>();
  private static final int EXPIRATION_INTERVAL_IN_MINUTES = 10;
  private static final int TOKEN_REQUEST_TRIES = 3;
  private static final long TOKEN_REQUEST_INTERVAL_IN_MILLIS = Duration.ofSeconds(30).toMillis();

  /**
   * Clears access tokens.
   */
  public static void clearAccessToken() {
    ACCESS_TOKEN.remove();
    LAST_REQUEST_TIME.remove();
  }

  private static void setAccessToken(String accessToken) {
    ACCESS_TOKEN.set(accessToken);
  }


  /**
   * Generates access token for Dp requests.
   *
   * @return access token
   */
  private String getAccessToken() {
    if (StringUtils.isEmpty(ACCESS_TOKEN.get()) || isTokenExpired()) {
      setAccessToken(getOauthAccessToken());
      LAST_REQUEST_TIME.set(LocalDateTime.now());
    }
    return ACCESS_TOKEN.get();
  }

  private String getOauthAccessToken() {
    Response accessTokenResponse = requestAccessTokenWithRetry();
    checkAccessTokenResponse(accessTokenResponse);
    return extractAccessTokenFromResponse(accessTokenResponse);
  }

  // Is used for cases when auth service is down for a while.
  private Response requestAccessTokenWithRetry() {
    Response accessTokenResponse = requestAccessToken();
    try {
      for (int tryNumber = 1; tryNumber <= TOKEN_REQUEST_TRIES; tryNumber++) {
        if (accessTokenResponse.getStatusCode() != HttpURLConnection.HTTP_OK) {
          log.warn("Access token requests is not successful, status code is {};"
                  + "\n Performing additional try #{}",
              accessTokenResponse.getStatusCode(), tryNumber);
          Thread.sleep(TOKEN_REQUEST_INTERVAL_IN_MILLIS);
          accessTokenResponse = requestAccessToken();
        } else {
          break;
        }
      }
    } catch (InterruptedException e) {
      log.warn(e.getMessage());
      Thread.currentThread().interrupt();
    }
    return accessTokenResponse;
  }

  private Response requestAccessToken() {
    return RestAssured
        .given(tokenRequestSpecifications.getDefaultUserTokenRequestSpecification())
        .filters(new AllureRestAssured())
        .post();
  }

  private String extractAccessTokenFromResponse(Response accessTokenResponse) {
    String accessToken = accessTokenResponse.getBody().jsonPath().getString(ACCESS_TOKEN_PATH);
    if (accessToken.isBlank()) {
      throw new UnexpectedResponseException(
          String.format("Access token was not found in the response: %n%s",
              accessTokenResponse.getBody().asPrettyString()));
    } else {
      return accessToken;
    }
  }

  private void checkAccessTokenResponse(Response accessTokenResponse) {
    if (accessTokenResponse.getStatusCode() != HttpURLConnection.HTTP_OK) {
      throw new UnexpectedResponseException(
          String.format("Access Token Response is not successful."
                  + "%n Response status code is: %s;"
                  + "%n Response message is: %s",
              accessTokenResponse.getStatusCode(), accessTokenResponse.getBody().asPrettyString()));
    } else if (Arrays.stream(ContentType.JSON.getContentTypeStrings())
        .noneMatch(content -> content.equals(accessTokenResponse.getContentType()))) {
      throw new UnexpectedResponseException(
          String.format("Access token was not found in the response: %n%s",
              accessTokenResponse.getBody().asPrettyString()));
    }
  }

  /*
  To reduce quantity of access token requests.
  Token should expire in 3600 sec. (1h)
  Is defined in src/main/resources/application.yml "expires_in" field,
   but currently, generated token is expired in 5-10 min ¯\_(ツ)_/¯
   so it was decided to set 5 min into EXPIRATION_INTERVAL field.
   */
  private static boolean isTokenExpired() {
    LocalDateTime lastRequestTime =
        LAST_REQUEST_TIME.get().plusMinutes(EXPIRATION_INTERVAL_IN_MINUTES);
    LocalDateTime currentTime = LocalDateTime.now();
    return currentTime.isAfter(lastRequestTime);
  }

  /**
   * Generates Bearer access token for Dp requests.
   *
   * @return bearer access token
   */
  public String getBearerToken() {
    return String.format(BEARER_HEADER_VALUE, getAccessToken());
  }
}
