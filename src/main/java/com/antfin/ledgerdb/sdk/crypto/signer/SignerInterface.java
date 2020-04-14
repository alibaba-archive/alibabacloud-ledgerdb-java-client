package com.antfin.ledgerdb.sdk.crypto.signer;


public interface SignerInterface {

  boolean isSigner();

  boolean isVerifier();

  byte[] sign(byte[] message);

  boolean verify(byte[] sig, byte[] msg);

  byte[] recover(byte[] sig, byte[] msg);
}
