package com.antfin.ledgerdb.sdk;

public interface DelegateSignSupplier {

  interface DelegateID {

  }

  SignInfo delegateSign(DelegateID delegateId);
}
