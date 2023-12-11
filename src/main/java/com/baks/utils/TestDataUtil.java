package com.baks.utils;

import static com.baks.constants.ApiRequestsConstants.CONTENT_DISPOSITION_HEADER;
import static com.baks.constants.ApiRequestsConstants.CONTENT_DISPOSITION_VALUE_TEMPLATE;
import static com.baks.constants.ApiRequestsConstants.DIGEST_HEADER;
import static com.baks.constants.ApiRequestsConstants.META_HEADER_TEMPLATE;
import static com.baks.constants.TestDataConstants.ILLEGAL_FILE_NAME_CHARACTERS;
import static com.baks.constants.TestDataConstants.METADATA_OTHER_STRING_VALUE;
import static com.baks.constants.TestDataConstants.METADATA_STRING_VALUE;
import static com.baks.constants.TestDataConstants.TEST;
import static com.baks.constants.TestDataConstants.TEST_DOCUMENT_CATEGORY_CODE;
import static com.baks.enums.TestDocumentType.getDocumentExtensionByType;

import com.baks.enums.MetadataType;
import com.baks.enums.TestDocumentType;
import com.baks.exceptions.TestDataFileNotFoundException;
import com.baks.pojo.response.DocumentCategoryObject;
import com.baks.pojo.response.MetadataObject;
import com.baks.pojo.response.SystemDocumentTypeObject;
import com.baks.pojo.testdata.AccessTokenTestData;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.path.json.JsonPath;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.CRC32;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.util.ResourceUtils;

/**
 * Test data generation class.
 */
public final class TestDataUtil {

  private static final ObjectMapper objectMapper = new ObjectMapper();
  private static final String SAMPLE_DOCUMENT_PATH_TEMPLATE = "classpath:testdata/sampleDocument%s";
  private static final String INFECTED_DOCUMENT_PATH = "classpath:testdata/eicar.txt";
  private static final String TEST_TOKEN_DATA_PATH = "classpath:testdata/testTokens.json";
  private static final String SYSTEM_DOCUMENT_TYPE_JSON_PATH = "classpath:sdt.json";

  private TestDataUtil() {
  }

  /**
   * Returns File object By document type.
   *
   * @param documentType type of document, stored in test data folder.
   * @return a corresponding File object
   */
  public static File getSampleDocumentByType(String documentType) {
    String sampleDocumentExtension = getDocumentExtensionByType(documentType);
    try {
      if (documentType.equals(TestDocumentType.INFECTED.getType())) {
        return ResourceUtils.getFile(INFECTED_DOCUMENT_PATH);
      } else {
        return ResourceUtils.getFile(
            String.format(SAMPLE_DOCUMENT_PATH_TEMPLATE, sampleDocumentExtension));
      }
    } catch (IOException ex) {
      throw new TestDataFileNotFoundException(
          String.format("Failed to read test document file with type: %s. %n%s",
              documentType, ex.getMessage()));
    }
  }

  /**
   * Returns the size of File object By document type in bytes.
   *
   * @param documentType type of document, stored in test data folder.
   * @return size of the file in bytes
   */
  public static int getTestDocumentSize(String documentType) {
    return (int) getSampleDocumentByType(documentType).length();
  }

  /**
   * Get Access Token Test Data as single Object from testTokens.json.
   *
   * @return AccessTokenTestData POJO
   */
  public static AccessTokenTestData getTokensForTest() {
    AccessTokenTestData accessTokenTestData;
    try {
      File jsonData = ResourceUtils.getFile(TEST_TOKEN_DATA_PATH);
      accessTokenTestData = objectMapper.readValue(jsonData, AccessTokenTestData.class);
    } catch (IOException ex) {
      throw new TestDataFileNotFoundException(
          "Failed to read access token test data file. \n" + ex.getMessage());
    }
    return accessTokenTestData;
  }

  /**
   * Get List of system document types from sdt.json.
   *
   * @return List of system document types objects
   */
  public static List<SystemDocumentTypeObject> getSystemDocumentTypes() {
    List<SystemDocumentTypeObject> systemDocumentTypes;
    try {
      //TODO replace project local file usage to remote json download from GitHub
      File sdtJsonData = ResourceUtils.getFile(SYSTEM_DOCUMENT_TYPE_JSON_PATH);
      systemDocumentTypes = JsonPath.from(sdtJsonData)
          .getList("systemDocumentTypeList", SystemDocumentTypeObject.class);
    } catch (IOException ex) {
      throw new TestDataFileNotFoundException(
          "Failed to read json with system document types. \n" + ex.getMessage());
    }
    return systemDocumentTypes;
  }

  /**
   * Get test document category id from list of document categories.
   */
  public static String getTestCategoryIdFromDocumentCategoriesList(
      List<DocumentCategoryObject> documentCategoriesList) {
    return documentCategoriesList.stream()
        .filter(category -> category.getCategoryCode().equals(TEST_DOCUMENT_CATEGORY_CODE))
        .findFirst().orElseThrow(
            () -> new NoSuchElementException(String.format(
                "List of user document categories doesn't contain category with code: %s",
                TEST_DOCUMENT_CATEGORY_CODE)))
        .getCategoryId();
  }

  /**
   * Creates a random string whose length is the number of characters specified. Characters will be
   * chosen from the set of Latin alphabetic characters (a-z, A-Z).
   *
   * @param length the length of random string to create
   * @return the random string
   */
  public static String getRandomString(int length) {
    return RandomStringUtils.randomAlphabetic(length);
  }

  /**
   * Returns a pseudorandom negative int value between -100 (inclusive) and -1 (inclusive).
   */
  public static int getRandomNegativeInteger() {
    return getRandomInteger(-100, -1);
  }

  /**
   * Returns a pseudorandom int value between the specified origin (inclusive) and the specified
   * bound (inclusive).
   */
  public static int getRandomInteger(int origin, int bound) {
    return ThreadLocalRandom.current().nextInt(origin, bound + 1);
  }

  /**
   * Returns a pseudorandom int value between the specified origin (inclusive) and the specified
   * bound (exclusive).
   */
  public static int getRandomIntExclusiveBound(int origin, int bound) {
    return ThreadLocalRandom.current().nextInt(origin, bound);
  }

  /**
   * Returns a pseudorandom BigDecimal value between the specified origin (inclusive) and the
   * specified bound (inclusive).
   */
  public static BigDecimal getRandomDecimal(double origin, double bound) {
    return BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(origin, bound + 1))
        .setScale(2, RoundingMode.HALF_UP);
  }

  /**
   * Creates Content Disposition Header using specified file name.
   */
  public static Map<String, Object> createContentDispositionHeader(String fileName) {
    String headerValue = String.format(CONTENT_DISPOSITION_VALUE_TEMPLATE, fileName);
    return Map.of(CONTENT_DISPOSITION_HEADER, headerValue);
  }

  /**
   * Creates Digest Header Value based on test document size.
   */
  public static Map<String, String> createDigestData(String documentType) {
    int fileSize = getTestDocumentSize(documentType);
    CRC32 crc32 = new CRC32();
    crc32.update(fileSize);
    return Map.of("MD5", DigestUtils.md5Hex(String.valueOf(fileSize)),
        "CRC32", String.valueOf(crc32.getValue()));
  }

  /**
   * Creates Digest Header.
   */
  public static Map<String, Object> createDigestHeader(Map<String, String> digestData) {
    String headerValueTemplate = String.format("MD5=%s,CRC32=%s", digestData.get("MD5"),
        digestData.get("CRC32"));
    return Map.of(DIGEST_HEADER, headerValueTemplate);
  }

  /**
   * Creates Meta Header from specified data.
   */
  public static Map<String, Object> createMetaDataHeader(Map<String, Object> metaData) {
    Map<String, Object> headersMetaData = new HashMap<>();
    for (var metaDataEntry : metaData.entrySet()) {
      String metaHeaderKey = String.format(META_HEADER_TEMPLATE, metaDataEntry.getKey());
      headersMetaData.put(metaHeaderKey, metaDataEntry.getValue());
    }
    return headersMetaData;
  }

  /**
   * Generates Metadata values by type from list of Metadata objects.
   */
  public static Map<String, Object> generateMetaDataValues(List<MetadataObject> metadata) {
    Map<String, Object> generatedMetadata = new HashMap<>();
    for (MetadataObject metadataEntry : metadata) {
      MetadataType metadataType = MetadataType.valueOf(metadataEntry.getType().toUpperCase());
      Object metadataValue;
      switch (metadataType) {
        case DATE:
          metadataValue = DateUtil.getCurrentFormattedDatePlusDays(getRandomInteger(-7, 28));
          break;
        case DATETIME:
          metadataValue = DateUtil.getCurrentIsoDateTimePlusSpecifiedDays(getRandomInteger(0, 28));
          break;
        case BOOLEAN:
          metadataValue = ThreadLocalRandom.current().nextBoolean();
          break;
        case INTEGER:
          metadataValue = getRandomInteger(1, 28);
          break;
        case DECIMAL:
          metadataValue = getRandomDecimal(1, 100);
          break;
        case STRING:
        default:
          List<String> stringValues = List.of(METADATA_STRING_VALUE, METADATA_OTHER_STRING_VALUE);
          metadataValue = stringValues.get(getRandomIntExclusiveBound(0, stringValues.size()));
          break;
      }
      generatedMetadata.put(metadataEntry.getKey(), metadataValue);
    }
    return generatedMetadata;
  }

  /**
   * Creates Metadata Header with generated values based on specified list of Metadata objects.
   */
  public static Map<String, Object> generateMetadataHeader(List<MetadataObject> metadata) {
    return createMetaDataHeader(generateMetaDataValues(metadata));
  }

  /**
   * Adds mandatory metadata headers to the request parameters based on provided mandatory metadata
   * list.
   */
  public static Map<String, Object> getRequestHeadersWithMandatoryMeta(
      Map<String, Object> requestParameters, List<MetadataObject> mandatoryMetadataList) {
    return Stream.of(new HashMap<>(requestParameters),
            generateMetadataHeader(mandatoryMetadataList))
        .flatMap(map -> map.entrySet().stream())
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            Map.Entry::getValue,
            (value1, value2) -> value1));
  }

  /**
   * Generates document category metadata fields.
   */
  public static List<MetadataObject> generateDocumentCategoryMetadata() {
    List<MetadataObject> metadataList = new ArrayList<>();
    for (MetadataType metadataType : MetadataType.values()) {
      String metadataTypeValue = metadataType.getSystemValue();
      MetadataObject metadataObject = new MetadataObject();
      metadataObject.setKey((TEST + metadataTypeValue).toLowerCase());
      metadataObject.setName(String.format("%s type metadata", metadataTypeValue));
      metadataObject.setType(metadataTypeValue);
      metadataObject.setRequired(ThreadLocalRandom.current().nextBoolean());
      metadataList.add(metadataObject);
    }
    return metadataList;
  }

  /**
   * Returns random 2-letter country code defined in ISO 3166.
   */
  public static String getRandomCountryCode() {
    List<String> countryCodes = new ArrayList<>(List.of(Locale.getISOCountries()));
    return countryCodes.get(getRandomIntExclusiveBound(0, countryCodes.size()));
  }

  /**
   * Generates random UUID value.
   */
  public static String generateUuid() {
    return UUID.randomUUID().toString();
  }

  /**
   * Returns random illegal file name character.
   */
  public static String getIllegalFileNameCharacter() {
    List<String> illegalCharacters = ILLEGAL_FILE_NAME_CHARACTERS;
    return illegalCharacters.get(getRandomIntExclusiveBound(0, illegalCharacters.size()));
  }

  /**
   * Returns random non-Alphanumeric character.
   */
  public static String getNonAlphanumericCharacter() {
    List<String> nonAlphanumericCharacters = new ArrayList<>(ILLEGAL_FILE_NAME_CHARACTERS);
    nonAlphanumericCharacters.addAll(List.of(" ", "-", "_", "."));
    return nonAlphanumericCharacters.get(
        getRandomIntExclusiveBound(0, nonAlphanumericCharacters.size()));
  }
}
