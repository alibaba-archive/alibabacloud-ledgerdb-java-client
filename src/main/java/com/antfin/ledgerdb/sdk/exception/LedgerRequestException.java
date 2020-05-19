package com.antfin.ledgerdb.sdk.exception;

import io.grpc.StatusRuntimeException;

public class LedgerRequestException extends RuntimeException {

  private final StatusRuntimeException statusRuntimeException;

  public LedgerRequestException(StatusRuntimeException t) {
    super(t);
    this.statusRuntimeException = t;
  }

  public void getStatus() {
    statusRuntimeException.getStatus();
  }

}
