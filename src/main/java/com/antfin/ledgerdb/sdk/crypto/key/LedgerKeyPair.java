package com.antfin.ledgerdb.sdk.crypto.key;

import java.util.Arrays;

import com.antfin.ledgerdb.sdk.LedgerException;
import org.apache.commons.lang3.ArrayUtils;

public class LedgerKeyPair {
  private KeyTypes type;
  private byte[] privkeyId; // maybe private key identity else private key itself.
  private byte[] pubkeyId;

  /**
   * Only  public key.
   * pubkeyEncoded is ahead of key type[2Bytes] except ecc k1 v0[64Bytes].
   *
   * @param pubkeyEncoded
   */
  public LedgerKeyPair(byte[] pubkeyEncoded) {
    if (ArrayUtils.isEmpty(pubkeyEncoded)) {
      throw new LedgerException("pubkeyEncoded should not be empty");
    }

    if (pubkeyEncoded.length <= 2) {
      throw new LedgerException("pubkeyEncoded is too short");
    }

    if (pubkeyEncoded.length == 64) {
      //EccK1V0
      this.pubkeyId = ArrayUtils.clone(pubkeyEncoded);
      this.type = KeyTypes.KEY_ECCK1_PKCS8;
    } else {
      this.type = KeyTypes.valueOf(pubkeyEncoded);
      if (this.type == KeyTypes.UNKNOWN) {
        //throw new MychainSdkException(ErrorCode.SDK_INVALID_PARAMETER,
        //    "invalid key type");
        throw new LedgerException("Invalid key type");
      }
      if (!isvalidPubkeySize(this.type, pubkeyEncoded.length - 2)) {
        //throw new MychainSdkException(ErrorCode.SDK_INVALID_PARAMETER,
        //    "invalid pubkey size");
        throw new LedgerException("Invalid pubkeyEncode size");
      }
      this.pubkeyId = Arrays.copyOfRange(pubkeyEncoded, 2, pubkeyEncoded.length);
    }
  }

  /**
   * generate a keypair by pubkeyEncoded and privkeyEncoded
   * pubkeyEncoded is ahead of key type[2Bytes] except ecc k1 v0[64Bytes].
   * privkeyEncoded is ahead of key type[2Bytes] except ecc k1 v0[32Bytes].
   *
   * @param pubkeyEncoded  is nullable
   * @param privkeyEncoded do not null
   */
  public LedgerKeyPair(byte[] pubkeyEncoded, byte[] privkeyEncoded) {
    if (ArrayUtils.isEmpty(privkeyEncoded)) {
      //throw new MychainSdkException(ErrorCode.SDK_INVALID_PARAMETER,
      //    "privkeyEncoded should not empty");
      throw new LedgerException("privkeyEncoded should not be empty");
    }

    if (privkeyEncoded.length <= 2) {
      //throw new MychainSdkException(ErrorCode.SDK_INVALID_PARAMETER,
      //    "privkeyEncoded is too short");
      throw new LedgerException("privkeyEncoded is too short");
    }

    if (pubkeyEncoded != null && pubkeyEncoded.length <= 2) {
      //throw new MychainSdkException(ErrorCode.SDK_INVALID_PARAMETER,
      //    "pubkeyEncoded is too short");
      throw new LedgerException("pubkeyEncoded is too short");
    }

    if (privkeyEncoded.length == 32) {
      //EccK1V0
      this.type = KeyTypes.KEY_ECCK1_PKCS8;
      this.privkeyId = ArrayUtils.clone(privkeyEncoded);
    } else {
      this.type = KeyTypes.valueOf(privkeyEncoded);

      if (this.type == KeyTypes.UNKNOWN) {
        //throw new MychainSdkException(ErrorCode.SDK_INVALID_PARAMETER,
        //    "invalid key type");
        throw new LedgerException("Invalid key type");
      }

      if (!isvalidPrikeySize(this.type, privkeyEncoded.length - 2)) {
        //throw new MychainSdkException(ErrorCode.SDK_INVALID_PARAMETER,
        //    "invalid privkey size");
        throw new LedgerException("Invalid privkey size");
      }
      this.privkeyId = Arrays.copyOfRange(privkeyEncoded, 2, privkeyEncoded.length);
    }

    if (pubkeyEncoded != null) {
      KeyTypes pubkeyType;
      if (pubkeyEncoded.length == 64) {
        //EccK1V0
        pubkeyType = KeyTypes.KEY_ECCK1_PKCS8;
      } else {
        pubkeyType = KeyTypes.valueOf(pubkeyEncoded);
      }
      if (this.type != pubkeyType) {
        //throw new MychainSdkException(ErrorCode.SDK_INVALID_PARAMETER,
        //    "pubkeyEncoded and privkeyEncoded types are different");
        throw new LedgerException("pubkeyEncode and privkeyEncoded types are different");
      }

      if (pubkeyEncoded.length == 64) {
        //EccK1V0
        this.pubkeyId = ArrayUtils.clone(pubkeyEncoded);
      } else {
        if (!isvalidPubkeySize(this.type, pubkeyEncoded.length - 2)) {
          //throw new MychainSdkException(ErrorCode.SDK_INVALID_PARAMETER,
          //    "invalid pubkey size");
          throw new LedgerException("Invalid pubkey size");
        }
        this.pubkeyId = Arrays.copyOfRange(pubkeyEncoded, 2, pubkeyEncoded.length);
      }
    }
  }

  /**
   * @return
   */
  public boolean isPubkey() {
    return !ArrayUtils.isEmpty(pubkeyId);
  }

  /**
   * @return
   */
  public boolean isPrivkey() {
    return !ArrayUtils.isEmpty(privkeyId);
  }

  /**
   * @return
   */
  public KeyTypes getType() {
    return type;
  }

  /**
   * @return
   */
  public byte[] getPrivkeyId() {
    return privkeyId;
  }

  /**
   * k1 return 64 bytes
   *
   * @return
   */
  public byte[] getPubkeyId() {
    if (pubkeyId == null) {
      return null;
    }
    if (this.type == KeyTypes.KEY_ECCK1_PKCS8) {
      return Arrays.copyOfRange(pubkeyId, pubkeyId.length - 64, pubkeyId.length);
    }
    return pubkeyId;
  }

  /**
   * @return
   */
  public byte[] getPrivkeyEncoded() {
    if (privkeyId == null) {
      return null;
    }
    if (this.type == KeyTypes.KEY_ECCK1_PKCS8) {
      return privkeyId;
    }
    return encodeKey(privkeyId);
  }

  /**
   * keyType + pubkey except k1
   * k1 return 64 bytes
   *
   * @return
   */
  public byte[] getPubkeyEncoded() {
    if (pubkeyId == null) {
      return null;
    }
    if (type == KeyTypes.KEY_ECCK1_PKCS8) {
      return getPubkeyId();
    }

    return encodeKey(pubkeyId);
  }

  /**
   * check public key size after decode
   *
   * @param type
   * @param size
   * @return
   */
  private boolean isvalidPubkeySize(KeyTypes type, int size) {
    switch (type) {
      case KEY_ECCK1_PKCS8:
      case KEY_ECCR1_PKCS8:
      case KEY_SM2_PKCS8: {
        if (size == 65) {
          return true;
        }
        return false;
      }

      case KEY_RSA2048_PKCS8: {
        return true;//do not check
      }
    }
    return false;
  }

  /**
   * check private key size after decode
   *
   * @param type
   * @param size
   * @return
   */
  private boolean isvalidPrikeySize(KeyTypes type, int size) {
    switch (type) {
      case KEY_ECCK1_PKCS8:
      case KEY_ECCR1_PKCS8:
      case KEY_SM2_PKCS8: {
        if (size == 32) {
          return true;
        }
        return false;
      }

      case KEY_RSA2048_PKCS8: {
        return true; //do not check
      }
    }
    return false;
  }

  private byte[] encodeKey(byte[] key) {
    byte[] keyEncoded = new byte[2 + key.length];
    byte[] keyType = type.toBytes();
    System.arraycopy(keyType, 0, keyEncoded, 0, 2);
    System.arraycopy(key, 0, keyEncoded, 2, key.length);
    return keyEncoded;
  }
}