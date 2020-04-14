package com.antfin.ledgerdb.sdk.common;

import com.antfin.ledgerdb.sdk.proto.UpdateLedgerResponsePayload;
import com.antfin.ledgerdb.sdk.proto.UpdateLedgerResponsePayload;

public class UpdateLedgerResponse extends MasterLedgerResponse<UpdateLedgerResponsePayload> {

  public UpdateLedgerResponsePayload payload;

  public void setPayload(UpdateLedgerResponsePayload payload) {
    this.payload = payload;
    setLedgerInfo(payload.getLedgerInfo());
  }

  @Override
  public UpdateLedgerResponsePayload getPayload() {
    return payload;
  }
}
