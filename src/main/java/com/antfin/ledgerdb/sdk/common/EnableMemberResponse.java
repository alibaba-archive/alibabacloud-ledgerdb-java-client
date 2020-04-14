package com.antfin.ledgerdb.sdk.common;

import com.antfin.ledgerdb.sdk.proto.EnableMemberResponsePayload;

public class EnableMemberResponse extends LedgerResponse<EnableMemberResponsePayload> {

  private EnableMemberResponsePayload payload;

  public void setPayload(EnableMemberResponsePayload payload) {
    this.payload = payload;
  }

  public EnableMemberResponsePayload getPayload() {
    return payload;
  }

}
