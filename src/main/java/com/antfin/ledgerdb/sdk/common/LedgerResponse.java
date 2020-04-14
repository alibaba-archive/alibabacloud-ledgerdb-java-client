package com.antfin.ledgerdb.sdk.common;

import com.antfin.ledgerdb.sdk.proto.*;

public abstract class LedgerResponse<PayloadT> {

  protected ResponseAuth responseAuth;

  protected ApiStatus apiStatus;

  protected ApiVersion apiVersion;

  protected Digest requestDigest;

  protected ApiStatus opStatus;

  protected long opTimeNanos;

  protected String txHash;

  protected long totalSequence;

  protected LedgerInfo ledgerInfo;

  public long getTotalSequence() {
    return totalSequence;
  }

  public void setTotalSequence(long totalSequece) {
    this.totalSequence = totalSequece;
  }

  public long getOpTimeNanos() {
    return opTimeNanos;
  }

  public void setOpTimeNanos(long opTimeNanos) {
    this.opTimeNanos = opTimeNanos;
  }

  public String getTxHash() {
    return txHash;
  }

  public void setTxHash(String txHash) {
    this.txHash = txHash;
  }

  public ApiStatus getOpStatus() {
    return opStatus;
  }

  public void setOpStatus(ApiStatus opStatus) {
    this.opStatus = opStatus;
  }

  public Digest getRequestDigest() {
    return requestDigest;
  }

  public void setRequestDigest(Digest requestDigest) {
    this.requestDigest = requestDigest;
  }

  public ApiVersion getApiVersion() {
    return apiVersion;
  }

  public void setApiVersion(ApiVersion apiVersion) {
    this.apiVersion = apiVersion;
  }

  public ResponseAuth getResponseAuth() {
    return responseAuth;
  }

  public void setResponseAuth(ResponseAuth responseAuth) {
    this.responseAuth = responseAuth;
  }

  public ApiStatus getApiStatus() {
    return apiStatus;
  }

  public void setApiStatus(ApiStatus apiStatus) {
    this.apiStatus = apiStatus;
  }

  public void setLedgerInfo(LedgerInfo ledgerInfo) {
    this.ledgerInfo = ledgerInfo;
  }

  public LedgerInfo getLedgerInfo() {
    return ledgerInfo;
  }

  public abstract void setPayload(PayloadT payload);

  public abstract PayloadT getPayload();


}
