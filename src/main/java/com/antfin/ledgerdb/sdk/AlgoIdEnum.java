package com.antfin.ledgerdb.sdk;

//import com.alipay.mychain.sdk.crypto.keypair.KeyTypes;

import com.antfin.ledgerdb.sdk.crypto.key.KeyTypes;
import com.antfin.ledgerdb.sdk.crypto.key.KeyTypes;

import java.util.HashMap;
import java.util.Map;

public enum AlgoIdEnum {
  /**
   * Unknown alg id enum.
   */
  UNKNOWN(0),
  /**
   * signer ecc k1 local v0 alg id enum.
   */
  SIGNER_ECCK1_LOCAL_V0(1),
  /**
   * signer ecc r1 local v1 alg id enum.
   */
  SIGNER_ECCR1_LOCAL_V1(2),
  /**
   * signer sm2 local v1 alg id enum.
   */
  SIGNER_SM2_LOCAL_V1(3),
  /**
   * signer rsa local v1 alg id enum.
   */
  SIGNER_RSA_LOCAL_V1(4),

  //    SIGN_RSA_KMS_V1 = 5,

  /**
   * pkey cipher rsa oaep local v0 alg id enum.
   */
  PKEY_CIPHER_RSA_OAEP_LOCAL_V0(100),
  /**
   * pkey cipher ecc k1 local v1 alg id enum.
   */
  PKEY_CIPHER_ECCK1_LOCAL_V1(101),
  /**
   * pkey cipher sm2 local v1 alg id enum.
   */
  PKEY_CIPHER_SM2_LOCAL_V1(102),
  /**
   * pkey cipher rsa oaep local v1 alg id enum.
   */
  PKEY_CIPHER_RSA_OAEP_LOCAL_V1(103),
  /**
   * pkey cipher ecc r1 local v1 alg id enum.
   */
  PKEY_CIPHER_ECCR1_LOCAL_V1(104),

  /**
   * KDF of ENVELOPE_ECCK1_LOCAL_V1
   */
  PKEY_KDF_ECCK1_LOCAL_V1(151),

  /**
   * cipher aes gcm local v1 alg id enum.
   */
  CIPHER_AES_GCM_LOCAL_V1(201),
  /**
   * cipher sm4 cbc local v1 alg id enum.
   */
  CIPHER_SM4_CBC_LOCAL_V1(202), /* openssl have no SM4-GCM support */
  /**
   * cipher sm4 gcm local v1 alg id enum.
   */
  CIPHER_SM4_GCM_LOCAL_V1(203),

  /**
   * envelope rsa local sgx id enum.
   */
  ENVELOPE_RSA_LOCAL_SGX(300),
  /**
   * envelope ecc k1 local v1 id enum.
   */
  ENVELOPE_ECCK1_LOCAL_V1(301),
  /**
   * envelope rsa local v1 id enum.
   */
  ENVELOPE_RSA_LOCAL_V1(302),
  /**
   * envelope sm2 local v1 id enum.
   */
  ENVELOPE_SM2_LOCAL_V1(303),

  ENVELOPE_ECCK1_LOCAL_SGX(304),

  ENVELOPE_ECCR1_LOCAL_V1(305);
  private static Map<KeyTypes, AlgoIdEnum> keySignerMapping = new HashMap();
  private static Map<KeyTypes, AlgoIdEnum> keyPkeyCipherMapping = new HashMap();
  private static Map<KeyTypes, AlgoIdEnum> keyEnvelopeMapping = new HashMap();

  static {
    keySignerMapping.put(KeyTypes.KEY_ECCK1_PKCS8, AlgoIdEnum.SIGNER_ECCK1_LOCAL_V0);
    keySignerMapping.put(KeyTypes.KEY_ECCR1_PKCS8, AlgoIdEnum.SIGNER_ECCR1_LOCAL_V1);
    keySignerMapping.put(KeyTypes.KEY_SM2_PKCS8, AlgoIdEnum.SIGNER_SM2_LOCAL_V1);
    keySignerMapping.put(KeyTypes.KEY_RSA2048_PKCS8, AlgoIdEnum.SIGNER_RSA_LOCAL_V1);

    keyPkeyCipherMapping
        .put(KeyTypes.KEY_ECCK1_PKCS8, AlgoIdEnum.PKEY_CIPHER_ECCK1_LOCAL_V1);
    keyPkeyCipherMapping.put(KeyTypes.KEY_SM2_PKCS8, AlgoIdEnum.PKEY_CIPHER_SM2_LOCAL_V1);
    keyPkeyCipherMapping.put(KeyTypes.KEY_RSA2048_PKCS8,
        AlgoIdEnum.PKEY_CIPHER_RSA_OAEP_LOCAL_V0);

    keyEnvelopeMapping.put(KeyTypes.KEY_ECCK1_PKCS8, AlgoIdEnum.ENVELOPE_ECCK1_LOCAL_V1);
    keyEnvelopeMapping.put(KeyTypes.KEY_SM2_PKCS8, AlgoIdEnum.ENVELOPE_SM2_LOCAL_V1);
    keyEnvelopeMapping.put(KeyTypes.KEY_RSA2048_PKCS8, AlgoIdEnum.ENVELOPE_RSA_LOCAL_SGX);
  }

  private int algId;

  private AlgoIdEnum(int algId) {
    this.algId = algId;
  }

  /**
   * For number message type.
   *
   * @param value the value
   * @return the message type
   */
  public static AlgoIdEnum valueOf(final int value) {
    for (AlgoIdEnum e : AlgoIdEnum.values()) {
      if (e.algId == value) {
        return e;
      }
    }
    return UNKNOWN;
  }

  /**
   * For number message type.
   *
   * @param bytes the value
   * @return the message type
   */
  public static AlgoIdEnum valueOf(final byte[] bytes) {
    if (bytes.length < 2) {
      return AlgoIdEnum.UNKNOWN;
    }
    int value = ((bytes[0] & 0xFF) << 8) + (bytes[1] & 0xFF);

    return valueOf(value);
  }

  /**
   * return AlgoIdEnum.UNKNOWN or keySignerMapping.value
   *
   * @param kt
   * @return
   */
  public static AlgoIdEnum getSignerAlgoByKeyType(KeyTypes kt) {
    AlgoIdEnum algoIdEnum = keySignerMapping.get(kt);
    if (algoIdEnum == null) {
      return AlgoIdEnum.UNKNOWN;
    }
    return algoIdEnum;
  }

  /**
   * return AlgoIdEnum.UNKNOWN or keyPkeyCipherMapping.value
   *
   * @param kt
   * @return
   */
  public static AlgoIdEnum getPkeyCipherAlgoByKeyType(KeyTypes kt) {
    AlgoIdEnum algoIdEnum = keyPkeyCipherMapping.get(kt);
    if (algoIdEnum == null) {
      return AlgoIdEnum.UNKNOWN;
    }
    return algoIdEnum;
  }

  /**
   * return AlgoIdEnum.UNKNOWN or keyEnvelopeMapping.value
   *
   * @param kt
   * @return
   */
  public static AlgoIdEnum getEnvelopeAlgoByKeyType(KeyTypes kt) {
    AlgoIdEnum algoIdEnum = keyEnvelopeMapping.get(kt);
    if (algoIdEnum == null) {
      return AlgoIdEnum.UNKNOWN;
    }
    return algoIdEnum;
  }

  /**
   * @return
   */
  public int getValue() {
    return this.algId;
  }

  /**
   * return 2 bytes
   *
   * @return
   */
  public byte[] toBytes() {
    int value = this.algId;
    byte[] bytes = new byte[2];

    bytes[0] = (byte)((value >>> 8) & 0xFF);
    bytes[1] = (byte)(value & 0xFF);
    return bytes;
  }
}
