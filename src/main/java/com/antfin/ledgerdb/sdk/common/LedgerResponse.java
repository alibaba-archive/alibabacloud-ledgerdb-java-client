package com.antfin.ledgerdb.sdk.common;

import com.antfin.ledgerdb.sdk.proto.ApiStatus;
import com.antfin.ledgerdb.sdk.proto.ApiVersion;
import com.antfin.ledgerdb.sdk.proto.Digest;
import com.antfin.ledgerdb.sdk.proto.LedgerInfo;
import com.antfin.ledgerdb.sdk.proto.ResponseAuth;

public abstract class LedgerResponse<PayloadT> {

  protected PayloadT responsePayload;

  protected ResponseAuth responseAuth;

  protected ApiStatus apiStatus;

  protected ApiVersion apiVersion;

  protected Digest requestDigest;

  protected ApiStatus opStatus;

  protected long opTimeNanos;

  protected byte[] txHash;

  protected long totalSequence;

  protected LedgerInfo ledgerInfo;

  protected long blockSequence;

  protected byte[] stateRootHash;

  public byte[] getStateRootHash() {
    return stateRootHash;
  }

  public void setStateRootHash(byte[] stateRootHash) {
    this.stateRootHash = stateRootHash;
  }

  public long getBlockSequence() {
    return blockSequence;
  }

  public void setBlockSequence(long blockSequence) {
    this.blockSequence = blockSequence;
  }

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

  public byte[] getTxHash() {
    return txHash;
  }

  public void setTxHash(byte[] txHash) {
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

  public void setPayload(PayloadT payload) {
    this.responsePayload = payload;
  }

  public PayloadT getPayload() {
    return responsePayload;
  }


}
