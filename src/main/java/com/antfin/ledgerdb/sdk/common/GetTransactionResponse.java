package com.antfin.ledgerdb.sdk.common;

import com.antfin.ledgerdb.sdk.proto.GetTxResponsePayload;
import com.antfin.ledgerdb.sdk.proto.Tx;
import com.antfin.ledgerdb.sdk.proto.GetTxRequestPayload;
import com.antfin.ledgerdb.sdk.proto.GetTxResponsePayload;
import com.antfin.ledgerdb.sdk.proto.Tx;

public class GetTransactionResponse
    extends ServerLedgerResponse<GetTxResponsePayload> {

  public TransactionId transactionId;

  private Tx tx;

  private GetTxResponsePayload payload;

  public Tx getTx() {
    return tx;
  }

  public void setTx(Tx tx) {
    this.tx = tx;
    setTransactionId(new TransactionId(this.tx.getTxHash()));
  }


  public TransactionId getTransactionId() {
    return transactionId;
  }

  void setTransactionId(TransactionId transactionId) {
    this.transactionId = transactionId;
  }


  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(" transactionId: ").append(transactionId.toHexString());
    return builder.toString();
  }

  @Override
  public void setPayload(GetTxResponsePayload payload) {
    this.payload = payload;
    setTx(payload.getTx());
  }

  @Override
  public GetTxResponsePayload getPayload() {
    return payload;
  }
}
