package com.antfin.ledgerdb.sdk.hash;

import org.bouncycastle.jcajce.provider.digest.Keccak;


public class Keccak256 implements IHash {
  @Override
  public HashTypeEnum getHashType() {
    return HashTypeEnum.Keccak;
  }

  @Override
  public byte[] hash(byte[] data) {
    Keccak.DigestKeccak digest = new Keccak.Digest256();
    digest.update(data, 0, data.length);
    return digest.digest();
  }
}