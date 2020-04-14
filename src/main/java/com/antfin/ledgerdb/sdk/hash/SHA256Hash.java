package com.antfin.ledgerdb.sdk.hash;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256Hash implements IHash {
  @Override
  public HashTypeEnum getHashType() {
    return HashTypeEnum.SHA256;
  }

  @Override
  public byte[] hash(byte[] data) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      digest.reset();
      return digest.digest(data);
    } catch (NoSuchAlgorithmException e) {
      return null;
    }
  }
}
