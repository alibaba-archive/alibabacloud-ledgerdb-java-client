/*
 * MIT License
 *
 * Copyright (c) 2020 Alibaba Cloud
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.antfin.ledgerdb.client.common;

import com.antfin.ledgerdb.client.proto.ApiStatus;
import com.antfin.ledgerdb.client.proto.ApiVersion;
import com.antfin.ledgerdb.client.proto.Digest;
import com.antfin.ledgerdb.client.proto.LedgerInfo;
import com.antfin.ledgerdb.client.proto.ResponseAuth;

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
