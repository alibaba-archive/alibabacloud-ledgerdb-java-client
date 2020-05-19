package com.antfin.ledgerdb.sdk.exception;

public class LedgerException extends RuntimeException {

  public LedgerException() {
    super();
  }

  public LedgerException(String errorMessage) {
    super(errorMessage);
  }

  public LedgerException(Throwable t) {
    super(t);
  }

  public LedgerException(String errorMessage, Throwable t) {
    super(errorMessage, t);
  }
}
