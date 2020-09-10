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

package com.antfin.ledgerdb.client;

import com.antfin.ledgerdb.client.common.SignerProfile;
import com.antfin.ledgerdb.client.crypto.SecureRandomUtils;
import com.antfin.ledgerdb.client.proto.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class OperationControl {

  /**
   * 请求超时时间
   */
  private long timeoutInMillis = 10000;

  /**
   * 线索
   */
  private List<String> clues = new ArrayList<>();

  /**
   * 对请求内容进行摘要计算的算法类型
   */
  private Digest.HashType digestHashType = Digest.HashType.SHA256;

  /**
   * 对请求数据进行签名的相关信息
   */
  private List<SignerProfile> signerProfiles = new LinkedList<>();

  /**
   * 幂等token
   */
  private String clientToken;

  /**
   * 请求发起时间
   */
  private long timestampMills = System.currentTimeMillis();

  /**
   * 防重放随机数
   */
  private long nonce = (SecureRandomUtils.secureRandom()).nextLong();

  /**
   * 用户对请求信息进行签名的相关信息
   */
  private RequestAuth requestAuth;

  /**
   * 请求在被发送前序列化后结果，也是利用用户秘钥进行签名的输入
   */
  private byte[] requestMessage;

  public byte[] getRequestMessage() {
    return requestMessage;
  }

  public void setRequestMessage(byte[] requestMessage) {
    this.requestMessage = requestMessage;
  }

  public void setRequestAuth(RequestAuth requestAuth) {
    this.requestAuth = requestAuth;
  }

  public RequestAuth getRequestAuth() {
    return requestAuth;
  }

  public void setClientToken(String clientToken) {
    this.clientToken = clientToken;
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

  public long getTimeoutInMillis() {
    return timeoutInMillis;
  }

  public String getClientToken() {
    return clientToken;
  }

  public long getTimestampMills() {
    return timestampMills;
  }

  public long getNonce() {
    return nonce;
  }

  public void setClues(List<String> clues) {
    this.clues = clues;
  }

  public List<String> getClues() {
    return clues;
  }

  public List<SignerProfile> getSignerProfiles() {
    return signerProfiles;
  }

  public void setSignerProfiles(List<SignerProfile> signerProfiles) {
    this.signerProfiles = signerProfiles;
  }

  public void addSignerProfile(SignerProfile signerProfile) {
    this.signerProfiles.add(signerProfile);
  }

  public void setDigestHashType(Digest.HashType digestHashType) {
    this.digestHashType = digestHashType;
  }

  public Digest.HashType getDigestHashType() {
    return digestHashType;
  }

}
