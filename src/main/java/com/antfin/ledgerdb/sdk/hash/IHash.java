package com.antfin.ledgerdb.sdk.hash;

import com.antfin.ledgerdb.sdk.proto.Digest;

public interface IHash {

  /**
   * get hash type
   *
   * @return
   */
  Digest.HashType getHashType();

  /**
   * get HashUtils result
   *
   * @param data
   * @return
   */
  byte[] hash(byte[] data);
}

