package com.baks.reporting;

import com.baks.constants.ConfigurationConstants;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;


/**
 * Allure Report utils.
 */
public class AllureReporter {

  private static final String PROJECT_BASEDIR = "project.basedir";
  private static final String ENV_PROPERTIES = System.getProperty(PROJECT_BASEDIR)
      + "/src/test/resources/environment.properties";
  private static final String RESULT_ENV_PROPERTIES = System.getProperty(PROJECT_BASEDIR)
      + "/target/allure-results/environment.properties";


  /**
   * Creates environment.properties file to add information to Allure Report Environment widget.
   */
  public static void attachEnvironmentInfo() {
    try (FileInputStream inputStream = new FileInputStream(ENV_PROPERTIES);
        FileOutputStream outputStream = new FileOutputStream(RESULT_ENV_PROPERTIES)) {
      Properties envProperties = new Properties();
      envProperties.load(inputStream);
      envProperties.setProperty("Dp.gateway.host",
          System.getProperty(ConfigurationConstants.GATEWAY_HOST));
      envProperties.setProperty("Dp.gateway.path",
          System.getProperty(ConfigurationConstants.GATEWAY_DOCUMENTS_PATH));
      envProperties.setProperty("identity.host",
          System.getProperty(ConfigurationConstants.AUTHORIZATION_HOST));
      envProperties.setProperty("identity.path",
          System.getProperty(ConfigurationConstants.AUTHORIZATION_PATH));
      envProperties.store(outputStream, null);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
