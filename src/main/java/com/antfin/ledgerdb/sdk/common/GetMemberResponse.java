package com.antfin.ledgerdb.sdk.common;

import com.antfin.ledgerdb.sdk.proto.GetMemberResponsePayload;
import com.antfin.ledgerdb.sdk.proto.GetMemberResponsePayload;

public class GetMemberResponse extends LedgerResponse<GetMemberResponsePayload> {

  private GetMemberResponsePayload payload;

  @Override
  public void setPayload(GetMemberResponsePayload payload) {
    this.payload = payload;
  }

  @Override
  public GetMemberResponsePayload getPayload() {
    return payload;
  }
}
