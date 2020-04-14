package com.antfin.ledgerdb.sdk.common;

import com.antfin.ledgerdb.sdk.proto.DisableMemberResponsePayload;

public class DisableMemberResponse extends LedgerResponse<DisableMemberResponsePayload> {

  private DisableMemberResponsePayload payload;

  @Override
  public void setPayload(DisableMemberResponsePayload payload) {
    this.payload = payload;
  }

  @Override
  public DisableMemberResponsePayload getPayload() {
    return payload;
  }
}
