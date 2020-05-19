package com.antfin.ledgerdb.sdk;

import com.antfin.ledgerdb.sdk.common.SignerProfile;
import com.antfin.ledgerdb.sdk.crypto.SecureRandomUtils;
import com.antfin.ledgerdb.sdk.proto.*;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

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
