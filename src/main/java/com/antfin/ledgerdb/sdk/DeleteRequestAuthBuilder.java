package com.antfin.ledgerdb.sdk;

import com.antfin.ledgerdb.sdk.proto.RequestAuth;
import com.antfin.ledgerdb.sdk.proto.TxRequest;
import com.antfin.ledgerdb.sdk.proto.RequestAuth;
import com.antfin.ledgerdb.sdk.proto.TxRequest;

public interface DeleteRequestAuthBuilder {

  public RequestAuth build(TxRequest reqeust, LedgerSignerEntity signerEntity);
}
