package com.antfin.ledgerdb.sdk.common;

import com.antfin.ledgerdb.sdk.proto.SetTrustPointResponsePayload;
import com.antfin.ledgerdb.sdk.proto.SetTrustPointResponsePayload;

public class SetTrustPointResponse extends LedgerResponse<SetTrustPointResponsePayload> {

  public SetTrustPointResponsePayload responsePayload;

  @Override
  public void setPayload(SetTrustPointResponsePayload payload) {
    this.responsePayload = payload;
  }

  @Override
  public SetTrustPointResponsePayload getPayload() {
    return responsePayload;
  }
}
