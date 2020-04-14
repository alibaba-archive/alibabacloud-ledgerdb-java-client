package com.antfin.ledgerdb.sdk.common;

import com.antfin.ledgerdb.sdk.proto.CreateMemberResponsePayload;
import com.antfin.ledgerdb.sdk.proto.CreateMemberResponsePayload;

public class CreateMemberResponse extends LedgerResponse<CreateMemberResponsePayload> {

  CreateMemberResponsePayload payload;

  @Override
  public void setPayload(CreateMemberResponsePayload payload) {
    this.payload = payload;
  }

  @Override
  public CreateMemberResponsePayload getPayload() {
    return payload;
  }
}
