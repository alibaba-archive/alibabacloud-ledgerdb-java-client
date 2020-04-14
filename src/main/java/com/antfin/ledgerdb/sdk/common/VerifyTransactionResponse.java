package com.antfin.ledgerdb.sdk.common;

import com.antfin.ledgerdb.sdk.proto.ExistTxResponsePayload;
import com.antfin.ledgerdb.sdk.proto.ExistTxResponsePayload;

public class VerifyTransactionResponse
    extends ServerLedgerResponse<ExistTxResponsePayload> {

  public ExistTxResponsePayload payload;

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    return builder.toString();
  }

  @Override
  public void setPayload(ExistTxResponsePayload payload) {
    // ExistTxResponsePayload ia an empty struct now
    this.payload = payload;
  }

  @Override
  public ExistTxResponsePayload getPayload() {
    return payload;
  }
}
