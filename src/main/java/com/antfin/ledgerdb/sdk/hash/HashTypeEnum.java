package com.antfin.ledgerdb.sdk.hash;

public enum HashTypeEnum {
  /**
   * Sha256 hash type enum.
   */
  SHA256(0),
  /**
   * Sm3 hash type enum.
   */
  SM3(1),
  /**
   * Keccak hash type enum.
   */
  Keccak(2),;
  private int algId;

  private HashTypeEnum(int algId) {
    this.algId = algId;
  }
}
