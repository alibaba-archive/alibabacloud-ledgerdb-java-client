package com.antfin.ledgerdb.sdk.common;

import com.antfin.ledgerdb.sdk.proto.ListTxsResponsePayload;
import com.antfin.ledgerdb.sdk.proto.Tx;
import com.antfin.ledgerdb.sdk.proto.ListTxsResponsePayload;
import com.antfin.ledgerdb.sdk.proto.Tx;

import java.util.List;

public class ListTransactionsResponse extends ServerLedgerResponse<ListTxsResponsePayload> {

  public ListTxsResponsePayload responsePayload;

  public void setPayload(ListTxsResponsePayload payload) {
    this.responsePayload = payload;
  }

  @Override
  public ListTxsResponsePayload getPayload() {
    return responsePayload;
  }

  public List<Tx> getTxList() {
    return responsePayload.getTxList();
  }

  public Tx getTx(int i) {
    return responsePayload.getTx(i);
  }

  public int getTxCount() {
    return responsePayload.getTxCount();
  }
}
