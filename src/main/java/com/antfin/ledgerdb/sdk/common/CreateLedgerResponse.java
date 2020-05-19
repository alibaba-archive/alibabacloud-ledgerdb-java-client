package com.antfin.ledgerdb.sdk.common;

import com.antfin.ledgerdb.sdk.proto.CreateLedgerResponsePayload;

public class CreateLedgerResponse extends MasterLedgerResponse<CreateLedgerResponsePayload> {

  public void setPayload(CreateLedgerResponsePayload payLoad) {
    this.responsePayload = payLoad;
    setLedgerInfo(responsePayload.getLedgerInfo());
  }

  @Override
  public CreateLedgerResponsePayload getPayload() {
    return responsePayload;
  }

}
