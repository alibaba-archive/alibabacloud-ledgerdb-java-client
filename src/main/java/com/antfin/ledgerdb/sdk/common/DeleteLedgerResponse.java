package com.antfin.ledgerdb.sdk.common;

import com.antfin.ledgerdb.sdk.proto.DeleteLedgerResponsePayload;

public class DeleteLedgerResponse extends MasterLedgerResponse<DeleteLedgerResponsePayload> {

  public DeleteLedgerResponsePayload responsePayload;

  public void setPayload(DeleteLedgerResponsePayload payload) {
    this.responsePayload = payload;
    setLedgerInfo(payload.getLedgerInfo());
  }

}
