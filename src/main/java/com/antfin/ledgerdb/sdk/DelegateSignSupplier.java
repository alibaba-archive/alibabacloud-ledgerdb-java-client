package com.antfin.ledgerdb.sdk;

public interface DelegateSignSupplier {

  public interface DelegateID {

  }

  public SignInfo delegateSign(DelegateID delegateId);
}
