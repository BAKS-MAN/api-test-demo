package com.baks.specs;

import static com.baks.constants.ApiRequestsConstants.GRANT_TYPE;
import static com.baks.constants.ApiRequestsConstants.PASSWORD;
import static com.baks.constants.ApiRequestsConstants.SCOPE;
import static com.baks.constants.ApiRequestsConstants.USERNAME;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;

import com.baks.config.IdentityConfiguration;
import io.restassured.authentication.AuthenticationScheme;
import io.restassured.authentication.PreemptiveBasicAuthScheme;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Class for RequestSpecification creation for token request.
 */
@Component
public class TokenRequestSpecifications {

  private static final String EXPIRES_IN = "expires_in";
  protected final IdentityConfiguration identityConfiguration;

  @Autowired
  public TokenRequestSpecifications(IdentityConfiguration identityConfiguration) {
    this.identityConfiguration = identityConfiguration;
  }

  /**
   * Get REST Assured RequestSpecification based on data from application.yml for token generation.
   */
  public RequestSpecification getDefaultUserTokenRequestSpecification() {
    return getTokenRequestSpecification(getDefaultUserBasicAuthScheme());
  }

  private PreemptiveBasicAuthScheme getDefaultUserBasicAuthScheme() {
    PreemptiveBasicAuthScheme basicAuthScheme = new PreemptiveBasicAuthScheme();
    basicAuthScheme.setUserName(identityConfiguration.getAuthentication().get(USERNAME));
    basicAuthScheme.setPassword(identityConfiguration.getAuthentication().get(PASSWORD));
    return basicAuthScheme;
  }

  private RequestSpecification getTokenRequestSpecification(AuthenticationScheme auth) {
    return new RequestSpecBuilder()
        .addHeader(CONTENT_TYPE, ContentType.URLENC.withCharset(StandardCharsets.UTF_8))
        .setBaseUri(identityConfiguration.getHost())
        .addFormParam(GRANT_TYPE, identityConfiguration.getFormParams().get(GRANT_TYPE))
        .addFormParam(SCOPE, identityConfiguration.getFormParams().get(SCOPE))
        .addFormParam(EXPIRES_IN, identityConfiguration.getFormParams().get(EXPIRES_IN))
        .setAuth(auth)
        .setBasePath(identityConfiguration.getPath())
        .build();
  }
}
