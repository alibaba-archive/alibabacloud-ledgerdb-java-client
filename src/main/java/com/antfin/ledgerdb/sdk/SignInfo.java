package com.antfin.ledgerdb.sdk;

import com.antfin.ledgerdb.sdk.proto.Signature;

public class SignInfo {

  public byte[] sigBytes;
  public Signature.SignatureType sigType;

  public byte[] getSigBytes() {
    return sigBytes;
  }

  public void setSigBytes(byte[] sigBytes) {
    this.sigBytes = sigBytes;
  }

  public Signature.SignatureType getSigType() {
    return sigType;
  }

  public void setSigType(Signature.SignatureType sigType) {
    this.sigType = sigType;
  }
}
