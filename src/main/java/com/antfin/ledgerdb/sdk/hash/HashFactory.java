package com.antfin.ledgerdb.sdk.hash;

import com.antfin.ledgerdb.sdk.exception.LedgerException;
import com.antfin.ledgerdb.sdk.proto.Digest;

public class HashFactory {
  /**
   * Get default hash
   *
   * @return IHash
   */
  public static IHash getHash() {
    return new SHA256Hash();
  }

  public static IHash getHash(Digest.HashType hashType) {
    switch (hashType) {
      case SHA256:
        return new SHA256Hash();
      default:
        throw new LedgerException("Unimplemented hash");
    }
  }
}