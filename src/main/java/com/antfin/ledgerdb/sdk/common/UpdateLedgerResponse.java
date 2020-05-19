package com.antfin.ledgerdb.sdk.common;

import com.antfin.ledgerdb.sdk.proto.UpdateLedgerResponsePayload;

public class UpdateLedgerResponse extends MasterLedgerResponse<UpdateLedgerResponsePayload> {

  public void setPayload(UpdateLedgerResponsePayload payload) {
    this.responsePayload = payload;
    setLedgerInfo(payload.getLedgerInfo());
  }

}
