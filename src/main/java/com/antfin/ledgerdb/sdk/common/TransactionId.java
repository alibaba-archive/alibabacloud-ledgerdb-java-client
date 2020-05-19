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

package com.antfin.ledgerdb.sdk.common;

import com.google.protobuf.ByteString;
import org.bouncycastle.util.encoders.Hex;

import java.util.Arrays;

public class TransactionId {

  protected byte[] transactionId;

  public TransactionId(byte[] rawBytes) {
    transactionId = Arrays.copyOf(rawBytes, rawBytes.length);
  }

  public TransactionId(String hexString) {
    transactionId = Hex.decode(hexString);
  }

  public TransactionId(ByteString byteString) {
    transactionId = byteString.toByteArray();
  }

  public byte[] getRawTransactionId() {
    if (transactionId == null) {
      return null;
    }
    return Arrays.copyOf(transactionId, transactionId.length);
  }

  void setWithRawBytes(byte[] transactionId) {
    this.transactionId =
        Arrays.copyOf(transactionId, transactionId.length);
  }

  void setWithHexString(String transactionHex) {
    this.transactionId = Hex.decode(transactionHex);
  }

  void setWithByteString(ByteString byteString) {
    this.transactionId = byteString.toByteArray();
  }

  public String toHexString() {
    return Hex.toHexString(transactionId);
  }

  @Override
  public String toString() {
    return "Transaction id in hex: " + toHexString();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj == null) {
      return false;
    }

    if (!(obj instanceof TransactionId)) {
      return false;
    }

    TransactionId other = (TransactionId) obj;

    return Arrays.equals(transactionId, other.transactionId);
  }

  public static boolean equals(TransactionId a, TransactionId b) {
    if (a == b) {
      return true;
    }

    if (a == null || b == null) {
      return false;
    }

    return a.equals(b);
  }
}
