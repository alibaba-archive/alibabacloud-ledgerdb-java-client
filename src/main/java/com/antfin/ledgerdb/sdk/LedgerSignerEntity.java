package com.antfin.ledgerdb.sdk;

import com.antfin.ledgerdb.sdk.proto.Signature;

public interface LedgerSignerEntity {

  public byte[] sign(byte[] msg);

  public Signature.SignatureType getSignatureType();

  public byte[] getPublicKey();
}
