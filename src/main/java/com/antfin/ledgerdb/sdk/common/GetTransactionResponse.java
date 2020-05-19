package com.antfin.ledgerdb.sdk.common;

import com.antfin.ledgerdb.sdk.proto.GetTxResponsePayload;
import com.antfin.ledgerdb.sdk.proto.Tx;

public class GetTransactionResponse
    extends ServerLedgerResponse<GetTxResponsePayload> {

  private Tx tx;

  public Tx getTx() {
    return tx;
  }

  void setTx(Tx tx) {
    this.tx = tx;
  }

  @Override
  public void setPayload(GetTxResponsePayload payload) {
    this.responsePayload = payload;
    setTx(payload.getTx());
  }

}
