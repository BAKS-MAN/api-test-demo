package com.baks.runners;

import com.baks.reporting.AllureReporter;
import com.baks.service.AuthorizationService;
import com.baks.service.DocumentPlatformService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;


/**
 * JUnit5 Test Runner super class.
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EndpointTestRunner {

  Logger log = LoggerFactory.getLogger(EndpointTestRunner.class);

  @BeforeEach
  void logInfoAboutTest(TestInfo testInfo) {
    testInfo.getTestMethod().ifPresent(
        testMethod -> log.info("Start execution for test: {}", testMethod.getName()));
  }

  @AfterAll
  static void tearDown() {
    AllureReporter.attachEnvironmentInfo();
    AuthorizationService.clearAccessToken();
    DocumentPlatformService.clearTestDocumentCategoryData();
  }
}
