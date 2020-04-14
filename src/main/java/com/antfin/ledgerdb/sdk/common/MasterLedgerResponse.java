package com.antfin.ledgerdb.sdk.common;

import com.antfin.ledgerdb.sdk.proto.LedgerInfo;
import com.antfin.ledgerdb.sdk.proto.LedgerInfo;

public abstract class MasterLedgerResponse<Payload> extends LedgerResponse<Payload> {

  protected LedgerInfo ledgerInfo;

  public LedgerInfo getLedgerInfo() {
    return ledgerInfo;
  }

  public void setLedgerInfo(LedgerInfo ledgerInfo) {
    this.ledgerInfo = ledgerInfo;
  }

  public abstract void setPayload(Payload payload);

}
