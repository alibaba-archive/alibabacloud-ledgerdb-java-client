package com.antfin.ledgerdb.sdk.util;

import java.math.BigInteger;

public class NumericUtils {
  private NumericUtils() {
  }

  public static BigInteger toBigInt(byte[] value) {
    return new BigInteger(1, value);
  }

  public static byte[] toBytesPadded(BigInteger value, int length) {
    byte[] result = new byte[length];
    byte[] bytes = value.toByteArray();
    int bytesLength;
    byte srcOffset;
    if (bytes[0] == 0) {
      bytesLength = bytes.length - 1;
      srcOffset = 1;
    } else {
      bytesLength = bytes.length;
      srcOffset = 0;
    }

    if (bytesLength > length) {
      throw new RuntimeException("Input is too large to put in byte array of size " + length);
    } else {
      int destOffset = length - bytesLength;
      System.arraycopy(bytes, srcOffset, result, destOffset, bytesLength);
      return result;
    }
  }

  public static String toHexString(byte[] input, int offset, int length, boolean withPrefix) {
    StringBuilder stringBuilder = new StringBuilder();
    if (withPrefix) {
      stringBuilder.append("0x");
    }

    for(int i = offset; i < offset + length; ++i) {
      stringBuilder.append(String.format("%02x", input[i] & 255));
    }

    return stringBuilder.toString();
  }

  public static String toHexString(byte[] input) {
    return toHexString(input, 0, input.length, true);
  }
}
