package com.antfin.ledgerdb.sdk.common;

public class AppendTransactionResponse
    extends ServerLedgerResponse<byte[]> {

  private byte[] responsePayload;

  public TransactionId transactionId;

  public TransactionId getTransactionId() {
    return transactionId;
  }

  public void setTransactionId(TransactionId transactionId) {
    this.transactionId = transactionId;
  }

  @Override
  public void setPayload(byte[] payload) {
    this.responsePayload = payload;
    this.transactionId = new TransactionId(payload);
  }

  @Override
  public byte[] getPayload() {
    return responsePayload;
  }
}