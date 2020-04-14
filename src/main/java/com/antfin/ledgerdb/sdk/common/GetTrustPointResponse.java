package com.antfin.ledgerdb.sdk.common;

import com.antfin.ledgerdb.sdk.proto.GetTrustPointResponsePayload;
import com.antfin.ledgerdb.sdk.proto.GetTrustPointResponsePayload;

public class GetTrustPointResponse extends LedgerResponse<GetTrustPointResponsePayload>{

  public GetTrustPointResponsePayload responsePayload;

  @Override
  public void setPayload(GetTrustPointResponsePayload payload) {
    this.responsePayload = payload;
  }

  @Override
  public GetTrustPointResponsePayload getPayload() {
    return responsePayload;
  }
}
