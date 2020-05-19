package com.antfin.ledgerdb.sdk.crypto;

import java.security.SecureRandom;

/**
 * Utility class for working with SecureRandom implementation.
 *
 * <p> Adapted from <a
 * href="https://github.com/web3j/web3j/blob/master/crypto/src/main/java/org/web3j/crypto/SecureRandomUtils.java">
 * SecureRandomUtils</a> implementation
 *
 */
public final class SecureRandomUtils {

  private static final SecureRandom SECURE_RANDOM;

  static {
    SECURE_RANDOM = new SecureRandom();
  }

  public static SecureRandom secureRandom() {
    return SECURE_RANDOM;
  }

  private SecureRandomUtils() {}
}