package com.antfin.ledgerdb.sdk.hash;

public interface IHash {

  /**
   * get hash type
   *
   * @return
   */
  HashTypeEnum getHashType();

  /**
   * get HashUtils result
   *
   * @param data
   * @return
   */
  byte[] hash(byte[] data);
}

