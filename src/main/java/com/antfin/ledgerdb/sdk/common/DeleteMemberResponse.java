package com.antfin.ledgerdb.sdk.common;

import com.antfin.ledgerdb.sdk.proto.DeleteMemberResponsePayload;
import com.antfin.ledgerdb.sdk.proto.DeleteMemberResponsePayload;

public class DeleteMemberResponse extends LedgerResponse<DeleteMemberResponsePayload> {

  protected DeleteMemberResponsePayload payload;

  @Override
  public void setPayload(DeleteMemberResponsePayload payload) {
      this.payload = payload;
  }

  @Override
  public DeleteMemberResponsePayload getPayload() {
    return payload;
  }
}
