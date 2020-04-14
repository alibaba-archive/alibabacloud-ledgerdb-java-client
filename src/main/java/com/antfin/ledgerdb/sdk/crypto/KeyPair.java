package com.antfin.ledgerdb.sdk.crypto;

import com.antfin.ledgerdb.sdk.crypto.key.LedgerKeyPair;
import com.antfin.ledgerdb.sdk.proto.Signature;
import com.antfin.ledgerdb.sdk.LedgerSignerEntity;
import com.antfin.ledgerdb.sdk.crypto.key.LedgerKeyPair;
import com.antfin.ledgerdb.sdk.proto.Signature;

public abstract class KeyPair implements LedgerSignerEntity {

  protected final byte[] publicKey;

  protected final LedgerKeyPair innerKeyPair;

  public KeyPair(LedgerKeyPair innerKeyPair) {
    this.innerKeyPair = innerKeyPair;
    this.publicKey = null;
  }

  public KeyPair(byte[] publicKey) {
    this.publicKey = publicKey;
    this.innerKeyPair = null;
  }

  public abstract byte[] sign(byte[] message);

  public byte[] getPublicKeyEncoded() {
    return innerKeyPair.getPubkeyEncoded();
  }

  public byte[] getPublicKey() {
    if (publicKey != null) {
      return publicKey;
    }
    return innerKeyPair.getPubkeyId();
  }

  public byte[] getPrivateKeyEncoded() {
    return innerKeyPair.getPrivkeyEncoded();
  }

  public byte[] getPrivateKey() {
    return innerKeyPair.getPrivkeyId();
  }

  public abstract boolean verify(byte[] sig, byte[] msg);

  public Signature.SignatureType getSignatureType() {
    switch (innerKeyPair.getType()) {
      case KEY_ECCK1_PKCS8:
        return Signature.SignatureType.SECP256K1;
      default:
        return Signature.SignatureType.UNKNOWN;
    }
  }

  public byte[] getPublicKeyWithHeader() {
    byte[] pubkey = new byte[65];
    pubkey[0] = 0x04;
    System.arraycopy(getPublicKey(),0, pubkey,1, getPublicKey().length);
    return pubkey;
  }
}
