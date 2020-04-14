package com.antfin.ledgerdb.sdk.common;

import com.antfin.ledgerdb.sdk.proto.GrantTimeResponsePayload;
import com.antfin.ledgerdb.sdk.proto.GrantTimeResponsePayload;

public class GrantTimeResponse extends LedgerResponse<GrantTimeResponsePayload> {

  GrantTimeResponsePayload payload;

  @Override
  public void setPayload(GrantTimeResponsePayload payload) {
    this.payload = payload;
  }

  @Override
  public GrantTimeResponsePayload getPayload() {
    return payload;
  }
}
