package com.baks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Configuration class. &#064;SpringBootApplication same as &#064;Configuration
 * &#064;EnableAutoConfiguration &#064;ComponentScan.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class FeatureTestsApplication {

  public static void main(String[] args) {
    SpringApplication.run(FeatureTestsApplication.class, args);
  }
}
