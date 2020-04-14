package com.antfin.ledgerdb.sdk.common;

import com.antfin.ledgerdb.sdk.proto.GetLastGrantTimeResponsePayload;
import com.antfin.ledgerdb.sdk.proto.GetLastGrantTimeResponsePayload;

public class GetLastGrantTimeResponse extends LedgerResponse<GetLastGrantTimeResponsePayload>{

  public GetLastGrantTimeResponsePayload responsePayload;

  @Override
  public void setPayload(GetLastGrantTimeResponsePayload payload) {
    this.responsePayload = payload;
  }

  @Override
  public GetLastGrantTimeResponsePayload getPayload() {
    return responsePayload;
  }
}
