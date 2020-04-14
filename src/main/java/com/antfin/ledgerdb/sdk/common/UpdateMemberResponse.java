package com.antfin.ledgerdb.sdk.common;

import com.antfin.ledgerdb.sdk.proto.UpdateMemberResponsePayload;
import com.antfin.ledgerdb.sdk.proto.UpdateMemberResponsePayload;

public class UpdateMemberResponse extends LedgerResponse<UpdateMemberResponsePayload> {

  public UpdateMemberResponsePayload payload;

  @Override
  public void setPayload(UpdateMemberResponsePayload payload) {
    this.payload = payload;
  }

  @Override
  public UpdateMemberResponsePayload getPayload() {
    return payload;
  }
}
