/*
 * MIT License
 *
 * Copyright (c) 2020 Alibaba Cloud
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.antfin.ledgerdb.client.util;

import java.math.BigInteger;

/**
 * Message codec functions.
 *
 * <p> Adapted from <a
 * href="https://github.com/web3j/web3j/blob/master/utils/src/main/java/org/web3j/utils/Numeric.java">
 * NumericUtils</a> implementation
 *
 */
public class NumericUtils {

  private static final String HEX_PREFIX = "0x";

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
      stringBuilder.append(HEX_PREFIX);
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
