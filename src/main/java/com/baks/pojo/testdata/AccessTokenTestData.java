package com.baks.pojo.testdata;

/**
 * POJO for test data stored in testTokens.json.
 */
public class AccessTokenTestData {

  private String expiredToken;
  private String invalidToken;

  public String getInvalidToken() {
    return invalidToken;
  }

  public void setInvalidToken(String invalidToken) {
    this.invalidToken = invalidToken;
  }

  public String getExpiredToken() {
    return expiredToken;
  }

  public void setExpiredToken(String expiredToken) {
    this.expiredToken = expiredToken;
  }
}
