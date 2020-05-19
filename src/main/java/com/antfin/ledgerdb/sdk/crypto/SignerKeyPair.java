package com.antfin.ledgerdb.sdk.crypto;

import com.antfin.ledgerdb.sdk.proto.Signature;

public interface SignerKeyPair {

  byte[] sign(byte[] message);

  boolean verify(byte[] sig, byte[] msg);

  byte[] getPublicKeyWithHeader();

  Signature.SignatureType getSignatureType();
}
