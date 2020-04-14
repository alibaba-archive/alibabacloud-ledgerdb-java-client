package com.antfin.ledgerdb.sdk.crypto.key;

public enum KeyTypes {

  UNKNOWN(0),

  KEY_ECCK1_PKCS8(1),
  KEY_ECCR1_PKCS8(2),
  KEY_SM2_PKCS8(3),
  KEY_RSA2048_PKCS8(4),

  //    PKEY_ECCK1_KMS = 1001,
  ;

  public static final int MAX_KEY_TYPE_PKCS8 = 1000;
  private int value;

  private KeyTypes(int value) {
    this.value = value;
  }

  /**
   * For number message type.
   *
   * @param value the value
   * @return the message type
   */
  public static KeyTypes valueOf(final int value) {
    for (KeyTypes e : KeyTypes.values()) {
      if (e.value == value) {
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
  public static KeyTypes valueOf(final byte[] bytes) {
    if (bytes.length < 2) {
      return KeyTypes.UNKNOWN;
    }
    int value = ((bytes[0] & 0xFF) << 8) + (bytes[1] & 0xFF);

    return valueOf(value);
  }

  /**
   * @return
   */
  public int getValue() {
    return this.value;
  }

  /**
   * return 2 bytes
   *
   * @return
   */
  public byte[] toBytes() {
    int value = this.value;
    byte[] bytes = new byte[2];

    bytes[0] = (byte)((value >>> 8) & 0xFF);
    bytes[1] = (byte)(value & 0xFF);
    return bytes;
  }
}
