package com.baks.config;

import com.baks.constants.ConfigurationConstants;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration for access token endpoint from application.yml.
 */
@Component
@ConfigurationProperties(prefix = "identity")
public class IdentityConfiguration {

  private String host;
  private String path;
  private Map<String, String> formParams;
  private Map<String, String> authentication;

  public String getHost() {
    System.setProperty(ConfigurationConstants.AUTHORIZATION_HOST, host);
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public String getPath() {
    System.setProperty(ConfigurationConstants.AUTHORIZATION_PATH, path);
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public Map<String, String> getFormParams() {
    return formParams;
  }

  public void setFormParams(Map<String, String> formParams) {
    this.formParams = formParams;
  }

  public Map<String, String> getAuthentication() {
    return authentication;
  }

  public void setAuthentication(Map<String, String> authentication) {
    this.authentication = authentication;
  }
}

