package com.antfin.ledgerdb.sdk.hash;

public class HashFactory {
  /**
   * Get default hash
   *
   * @return IHash
   */
  public static IHash getHash() {
    return new SHA256Hash();
  }

  public static IHash getHash(HashTypeEnum hashTypeEnum) {
    switch (hashTypeEnum) {
      case SHA256:
        return new SHA256Hash();
      case SM3:
        return new SM3Hash();
      case Keccak:
        return new Keccak256();
      default:
        return new SHA256Hash();
    }
  }
}