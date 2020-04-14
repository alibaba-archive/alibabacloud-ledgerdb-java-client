package com.antfin.ledgerdb.sdk;

import com.antfin.ledgerdb.sdk.common.SignerProfile;
import com.antfin.ledgerdb.sdk.proto.ApiStatus;
import com.antfin.ledgerdb.sdk.proto.RequestAuth;
import com.antfin.ledgerdb.sdk.proto.ResponseAuth;
import com.antfin.ledgerdb.sdk.common.SignerProfile;
import com.antfin.ledgerdb.sdk.crypto.KeyPair;
import com.antfin.ledgerdb.sdk.proto.ApiStatus;
import com.antfin.ledgerdb.sdk.proto.RequestAuth;
import com.antfin.ledgerdb.sdk.proto.ResponseAuth;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class OperationControl {

  private long timeoutInMillis = 10000;

  private List<String> clues = new ArrayList<>();

  private List<SignerProfile> signerProfiles = new LinkedList<>();

  private String clientToken;

  private long clientSequence;

  private String ledgerId;

  private long timestampMills = System.currentTimeMillis();

  private long nonce = (new Random()).nextLong();

  private long operationTimeInNanos;

  private byte[] txHash;

  private long sequence;

  private byte[] requestMessage;

  private RequestAuth requestAuth;

  private byte[] responseMessage;

  private ResponseAuth responseAuth;

  private ApiStatus status;

  private ApiStatus opStatus;

  private boolean useDelegateSigner;

  private List<LedgerSignerEntity> delegateSigners;

  public void setClientToken(String clientToken) {
    this.clientToken = clientToken;
  }

  public void setClientSequence(long clientSequence) {
    this.clientSequence = clientSequence;
  }

  public void setNonce(long nonce) {
    this.nonce = nonce;
  }

  public void setTimeoutInMillis(long timeoutMills) {
    this.timeoutInMillis = timeoutMills;
  }

  public void setTimestampMills(long timestampMills) {
    this.timeoutInMillis = timestampMills;
  }

  public void setOperationTimeInNanos(long operationTimeInNanos) {
    this.operationTimeInNanos = operationTimeInNanos;
  }

  public void setTxHash(byte[] txHash) {
    this.txHash = txHash;
  }

  public long getTimeoutInMillis() {
    return timeoutInMillis;
  }

  public String getClientToken() {
    return clientToken;
  }

  public long getClientSequence() {
    return clientSequence;
  }

  public long getTimestampMills() {
    return timestampMills;
  }

  public long getNonce() {
    return nonce;
  }

  public long getOperationTimeInNanos() {
    return operationTimeInNanos;
  }

  public byte[] getTxHash() {
    return txHash;
  }

  public long getSequence() {
    return sequence;
  }

  public void setSequence(long sequence) {
    this.sequence = sequence;
  }

  public byte[] getRequestMessage() {
    return requestMessage;
  }

  public RequestAuth getRequestAuth() {
    return requestAuth;
  }

  public byte[] getResponseMessage() {
    return responseMessage;
  }

  public ResponseAuth getResponseAuth() {
    return responseAuth;
  }

  public ApiStatus getStatus() {
    return status;
  }

  public void setStatus(ApiStatus status) {
    this.status = status;
  }

  public ApiStatus getOpStatus() {
    return opStatus;
  }

  public void setOpStatus(ApiStatus opStatus) {
    this.opStatus = opStatus;
  }

  public void setLedgerId(String ledgerId) {
    this.ledgerId = ledgerId;
  }

  public String getLedgerId() {
    return ledgerId;
  }

  public void setClues(List<String> clues) {
    this.clues = clues;
  }

  public List<String> getClues() {
    return clues;
  }

  public boolean isUseDelegateSigner() {
    return useDelegateSigner;
  }

  public void setUseDelegateSigner(boolean useDelegateSigner) {
    this.useDelegateSigner = useDelegateSigner;
  }

  public List<LedgerSignerEntity> getDelegateSigners() {
    return delegateSigners;
  }

  public void setDelegateSigners(List<LedgerSignerEntity> delegateSigners) {
    this.delegateSigners = delegateSigners;
  }


  public List<SignerProfile> getSignerProfiles() {
    return signerProfiles;
  }

  public void setSignerProfiles(List<SignerProfile> signerProfiles) {
    this.signerProfiles = signerProfiles;
  }
}
