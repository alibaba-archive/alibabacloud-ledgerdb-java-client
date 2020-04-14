package com.antfin.ledgerdb.sdk.hash;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;

public class SM3Hash implements IHash {
  @Override
  public HashTypeEnum getHashType() {
    return HashTypeEnum.SM3;
  }

  @Override
  public byte[] hash(byte[] data) {
    try {
      Security.addProvider(new BouncyCastleProvider());
      MessageDigest digest = MessageDigest.getInstance("SM3", "BC");
      digest.reset();
      return digest.digest(data);
    } catch (NoSuchAlgorithmException e) {
      return null;
    } catch (NoSuchProviderException e) {
      return null;
    }
  }
}
