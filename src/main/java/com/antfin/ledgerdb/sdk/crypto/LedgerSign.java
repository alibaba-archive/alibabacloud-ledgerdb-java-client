package com.antfin.ledgerdb.sdk.crypto;

import com.antfin.ledgerdb.sdk.hash.HashTypeEnum;
import com.antfin.ledgerdb.sdk.hash.HashFactory;
import com.antfin.ledgerdb.sdk.hash.IHash;

public class LedgerSign {

  // public static byte[] signMessageWithSM3(byte[] message, KeyPair keyPair) {
  //  IHash hash = HashFactory.getHash(HashTypeEnum.SM3);
  //  byte[] messageHash = hash.hash(message);
  //  return keyPair.sign(messageHash);
  // }

  public static byte[] signMessageWithSHA256(byte[] message, KeyPair keyPair) {
    IHash hash = HashFactory.getHash(HashTypeEnum.SHA256);
    byte[] messageHash = hash.hash(message);
    return keyPair.sign(messageHash);
  }



}
