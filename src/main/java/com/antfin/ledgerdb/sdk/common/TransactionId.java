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
