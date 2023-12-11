package com.baks.config;

import com.baks.constants.ConfigurationConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration for gateway endpoint from application.yml.
 */
@Component
@ConfigurationProperties(prefix = "gateway")
public class GatewayConfiguration {

  private String host;

  private String path;

  public String getHost() {
    System.setProperty(ConfigurationConstants.GATEWAY_HOST, host);
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public String getPath() {
    System.setProperty(ConfigurationConstants.GATEWAY_DOCUMENTS_PATH, path);
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }
}
