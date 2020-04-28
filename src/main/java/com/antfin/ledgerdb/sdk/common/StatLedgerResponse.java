package com.antfin.ledgerdb.sdk.common;

import com.antfin.ledgerdb.sdk.proto.StatLedgerResponsePayload;

public class StatLedgerResponse extends MasterLedgerResponse<StatLedgerResponsePayload> {

  public StatLedgerResponsePayload payload;

  public void setPayload(StatLedgerResponsePayload payload) {
    this.payload = payload;
    setLedgerInfo(payload.getLedgerInfo());
  }

  @Override
  public StatLedgerResponsePayload getPayload() {
    return payload;
  }
}
