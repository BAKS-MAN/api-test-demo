## Demo project for API testing of a document platform

### Technologies stack:
* Spring-boot-starter Framework;
* Java 11;
* Rest Assured;
* Allure;
* Maven;

### Configuration
* Gateway configuration is stored in [application.yml](src/main/resources/application.yml)
* RestAssured request specifications are stored in [DpApiService class](src/main/java/com/baks/service/DpApiService.java)

### Test execution
To run all tests:
```sh
mvn clean install
```
<> Before test execution Checkstyle plugin performs analyzes the code style using GoogleCheckstyle configuration [checkstyle-checker.xml](checkstyle-checker.xml)
and generates a report on violations. A build with checkstyle violations is defined as failed. 0 violations are allowed.
The number of allowed violations is defined in the [pom.xml](pom.xml) in the 'maxAllowedViolations' configuration property.
<br />

To generate a report one of the following command should be used:
```sh
mvn allure:serve
```
Report will be generated into temp folder. Web server with results will start.
```sh
mvn allure:report
```
Report will be generated tо directory: target/allure-results/index.html
