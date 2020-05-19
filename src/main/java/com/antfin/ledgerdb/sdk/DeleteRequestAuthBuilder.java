package com.antfin.ledgerdb.sdk;

import com.antfin.ledgerdb.sdk.proto.RequestAuth;
import com.antfin.ledgerdb.sdk.proto.TxRequest;

public interface DeleteRequestAuthBuilder {

  RequestAuth build(TxRequest request, LedgerSignerEntity signerEntity);
}
