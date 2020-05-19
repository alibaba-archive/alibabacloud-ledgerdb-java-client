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

package com.antfin.ledgerdb.sdk;

import com.antfin.ledgerdb.sdk.common.*;
import com.antfin.ledgerdb.sdk.exception.LedgerRequestException;
import com.antfin.ledgerdb.sdk.proto.*;
import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyChannelBuilder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import java.io.File;

import javax.net.ssl.SSLException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LedgerDBLightClient {

  private static final int ESTIMATE_CLIENT_ID_LENGTH = 50;

  private final ManagedChannel channel;
  private final String clientId;
  private final String ledgerUriPrefix;

  public LedgerDBLightClient(String proxyHost, int port) {
    this.ledgerUriPrefix = "ledger://" + proxyHost + ":" + port + "/";
    this.channel =
        ManagedChannelBuilder.forAddress(proxyHost, port)
                             .usePlaintext()
                             .build();
    this.clientId = generateClientId();
  }

  public LedgerDBLightClient(
      String proxyHost,
      int port,
      SslContext sslContext){
    this.ledgerUriPrefix = "ledger://" + proxyHost + ":" + port + "/";
    this.channel =
        NettyChannelBuilder.forAddress(proxyHost, port)
            .sslContext(sslContext).build();
    this.clientId = generateClientId();
  }

  public String getLedgerUriPrefix() {
    return ledgerUriPrefix;
  }

  private static String generateClientId() {
    StringBuilder clientIdBuilder = new StringBuilder(ESTIMATE_CLIENT_ID_LENGTH);
    clientIdBuilder.append("LedgerDB-Client4j-");
    try {
      String ip = Inet4Address.getLocalHost().getHostAddress();
      clientIdBuilder.append(ip);
    } catch (Exception e) {
      clientIdBuilder.append("UNKNOWN");
    }
    return  clientIdBuilder.toString();
  }

  public static SslContext buildSslContext(
      String trustCertCollectionFilePath) throws SSLException {
    SslContextBuilder builder = GrpcSslContexts.forClient();
    builder.trustManager(new File(trustCertCollectionFilePath));
    return builder.build();
  }

  public static SslContext buildSslContext(
      File trustCertCollectionFile) throws SSLException {
    SslContextBuilder builder = GrpcSslContexts.forClient();
    builder.trustManager(trustCertCollectionFile);
    return builder.build();
  }

  public static SslContext buildSslContext(
      InputStream trustCertCollectionFileInputStream) throws SSLException {
    SslContextBuilder builder = GrpcSslContexts.forClient();
    builder.trustManager(trustCertCollectionFileInputStream);
    return builder.build();
  }

  /**
   * 创建账本
   * @param ledgerUri     待创建的ledger的URI标识
   * @param ledgerMeta    ledger元信息
   * @param memberInfos   账本成员列表
   * @param opControl     请求控制项
   * @return
   */
  public CreateLedgerResponse createLedger(
      String ledgerUri,
      LedgerMeta ledgerMeta,
      List<MemberInfo> memberInfos,
      OperationControl opControl) {
    ExecuteTxRequest request =
        ClientHelper.buildCreateLedgerRequest(ledgerUri, ledgerMeta, memberInfos ,opControl, this);
    ExecuteTxResponse response = callTxResponse(request, opControl);
    return ClientHelper.buildCreateLedgerResponse(response);
  }

  /**
   * 删除账本
   * @param ledgerUri   待删除的ledger的URI标识
   * @param opControl   请求控制项
   * @return
   */
  public DeleteLedgerResponse deleteLedger(
      String ledgerUri,
      OperationControl opControl) {
    ExecuteTxRequest request =
        ClientHelper.buildDeleteLedgerRequest(ledgerUri, opControl, this);
    ExecuteTxResponse response = callTxResponse(request, opControl);
    return ClientHelper.buildDeleteLedgerResponse(response);
  }

  /**
   * 更新账本元信息
   * @param ledgerUri       待更新的ledger的URI
   * @param ledgerMeta      ledger元信息
   * @param opControl       请求控制项
   * @return
   */
  public UpdateLedgerResponse updateLedger(
      String ledgerUri,
      LedgerMeta ledgerMeta,
      OperationControl opControl) {
    ExecuteTxRequest request =
        ClientHelper.buildUpdateLedgerRequest(ledgerUri, ledgerMeta, opControl, this);
    ExecuteTxResponse response = callTxResponse(request, opControl);
    return ClientHelper.buildUpdateLedgerResponse(response);
  }

  /**
   * 查询账本信息
   * @param ledgerUri       待更新的ledger的URI
   * @param opControl       请求控制项
   * @return
   */
  public StatLedgerResponse statLedger(
      String ledgerUri,
      OperationControl opControl) {
    ExecuteTxRequest request =
        ClientHelper.buildStatLedgerRequest(ledgerUri, opControl, this);
    ExecuteTxResponse response = callTxResponse(request, opControl);
    return ClientHelper.buildStatLedgerResponse(response);
  }

  /**
   * 新增记录
   * @param ledgerUri       目标ledger的URI标识
   * @param data            记录内容
   * @param opControl       请求控制项
   * @return
   */
  public AppendTransactionResponse appendTransaction(
      String ledgerUri,
      byte[] data,
      OperationControl opControl) {
    ExecuteTxRequest request =
        ClientHelper.buildAppendTransactionRequest(ledgerUri, data, opControl, this);
    ExecuteTxResponse response = callTxResponse(request, opControl);
    return ClientHelper.buildAppendTransactionResponse(response);
  }

  /**
   * 读取记录
   * @param ledgerUri     目标ledger的URI标识
   * @param txSequence    交易序号
   * @param opControl     请求控制项
   * @return
   */
  public GetTransactionResponse getTransaction(
      String ledgerUri,
      long txSequence,
      OperationControl opControl) {
    ExecuteTxRequest request =
        ClientHelper.buildGetTransactionRequest(ledgerUri, txSequence, opControl, this);
    ExecuteTxResponse response = callTxResponse(request, opControl);
    return ClientHelper.buildGetTransactionResponse(response);
  }

  /**
   * 查询记录是否存在
   * @param ledgerUri     目标ledger的URI标识
   * @param txSequence    交易序号
   * @param opControl     请求控制项
   * @return
   */
  public ExistTransactionResponse existTransaction(
      String ledgerUri,
      long txSequence,
      OperationControl opControl) {
    ExecuteTxRequest request =
        ClientHelper.buildVerifyTransactionRequest(ledgerUri, txSequence, opControl, this);
    ExecuteTxResponse response = callTxResponse(request, opControl);
    return ClientHelper.buildExistTransactionResponse(response);
  }

  /**
   * 顺序批量获取历史记录
   * @param ledgerUri       目标ledger的URI标识
   * @param clue            线索id, ""代表无clue
   * @param beginSequence   起始记录的序号
   * @param limit           返回的历史交易数量
   * @param opControl       请求控制项
   * @return
   */
  public ListTransactionsResponse listTransactions(
      String ledgerUri,
      String clue,
      long beginSequence,
      int limit,
      OperationControl opControl) {
    ExecuteTxRequest request =
        ClientHelper.buildListTransactionRequest(ledgerUri, clue, beginSequence, limit, opControl, this);
    ExecuteTxResponse response = callTxResponse(request, opControl);
    return ClientHelper.buildListTransactionResponse(response);
  }

  /**
   * 新增成员
   * @param ledgerUri         目标ledgerURI
   * @param memberInfo        成员信息
   * @param operationControl  请求控制项
   * @return
   */
  public CreateMemberResponse createMember(
      String ledgerUri,
      MemberInfo memberInfo,
      OperationControl operationControl) {
    ExecuteTxRequest request =
        ClientHelper.buildCreateMemberRequest(ledgerUri, memberInfo, operationControl, this);
    ExecuteTxResponse response = callTxResponse(request, operationControl);
    return ClientHelper.buildCreateMemberResponse(response);
  }

  /**
   * 更新成员状态
   * @param ledgerUri         目标ledgerURI
   * @param memberInfo        成员信息（包含成员的公钥信息）
   * @param operationControl  请求控制项
   * @return
   */
  public UpdateMemberResponse updateMember(
      String ledgerUri,
      MemberInfo memberInfo,
      OperationControl operationControl) {
    ExecuteTxRequest request =
        ClientHelper.buildUpdateMemberRequest(ledgerUri, memberInfo, operationControl, this);
    ExecuteTxResponse response = callTxResponse(request, operationControl);
    return ClientHelper.buildUpdateMemberResponse(response);
  }

  /**
   * 更新成员认证方式
   * @param ledgerUri           目标ledgerURI
   * @param memberId            成员Id
   * @param publicKey           公钥
   * @param operationControl    请求控制项
   * @return
   */
  public UpdateMemberKeyResponse updateMemberKey(
          String ledgerUri,
          String memberId,
          ByteString publicKey,
          OperationControl operationControl) {
    ClientHelper.checkUserPublicKey(publicKey);
    ExecuteTxRequest request =
            ClientHelper.buildUpdateMemberKeyRequest(ledgerUri, memberId, publicKey, operationControl, this);
    ExecuteTxResponse response = callTxResponse(request, operationControl);
    return ClientHelper.buildUpdateMemberKeyResponse(response);
  }

  /**
   * 更新成员权限列表
   * @param ledgerUri           目标ledgerURI
   * @param memberId            成员Id
   * @param permissions         权限列表
   * @param operationControl    请求控制项
   * @return
   */
  public UpdateMemberAclsResponse updateMemberAcls(
          String ledgerUri,
          String memberId,
          List<PermissionItem> permissions,
          OperationControl operationControl) {
    ExecuteTxRequest request =
            ClientHelper.buildUpdateMemberAclsRequest(ledgerUri, memberId, permissions, operationControl, this);
    ExecuteTxResponse response = callTxResponse(request, operationControl);
    return ClientHelper.buildUpdateMemberAclsResponse(response);
  }

  /**
   * 删除成员
   * @param ledgerUri       目标ledgerURI
   * @param memberId        成员ID
   * @param opControl       请求控制项
   * @return
   */
  public DeleteMemberResponse deleteMember(
      String ledgerUri,
      String memberId,
      OperationControl opControl) {
    ExecuteTxRequest request =
        ClientHelper.buildDeleteMemberRequest(ledgerUri, memberId, opControl, this);
    ExecuteTxResponse response = callTxResponse(request, opControl);
    return ClientHelper.buildDeleteMemberResponse(response);
  }

  /**
   * 获取账本上的成员
   * @param ledgerUri           目标ledgerURI
   * @param memberId            成员ID
   * @param operationControl    请求控制项
   * @return
   */
  public GetMemberResponse getMember(
      String ledgerUri,
      String memberId,
      OperationControl operationControl) {
    ExecuteTxRequest request =
        ClientHelper.buildGetMemberRequest(ledgerUri, memberId, operationControl, this);
    ExecuteTxResponse response = callTxResponse(request, operationControl);
    return ClientHelper.buildGetMemberResponse(response);
  }

  /**
   * 获取成员列表
   * @param ledgerUri         目标ledgerURI
   * @param lastMemberId      上次查询最后一个memberId，第一次为""
   * @param limit             返回数目
   * @param operationControl  请求控制项
   * @return
   */
  public ListMembersResponse listMembers(
          String ledgerUri,
          String lastMemberId,
          int limit,
          OperationControl operationControl) {
    ExecuteTxRequest request =
            ClientHelper.buildListMembersRequest(ledgerUri, lastMemberId, limit, operationControl, this);
    ExecuteTxResponse response = callTxResponse(request, operationControl);
    return ClientHelper.buildListMembersResponse(response);
  }

  /**
   * 设置信任锚点
   * @param ledgerUri     目标ledgerURI
   * @param sequence      信任锚点所在的交易序号
   * @param op            请求控制项
   * @return
   */
  public SetTrustPointResponse setTrustPoint(
      String ledgerUri,
      long sequence,
      OperationControl op) {
    ExecuteTxRequest request =
        ClientHelper.buildSetTrustPointRequest(ledgerUri, sequence, op, this);
    ExecuteTxResponse response = callTxResponse(request, op);
    return ClientHelper.buildSetTrustPointResponse(response);
  }

  /**
   * 获取信任锚点
   * @param ledgerUri           目标ledger的URI标识
   * @param op                  请求控制项
   * @return
   */
  public GetTrustPointResponse getTrustPoint(
      String ledgerUri,
      OperationControl op) {
    ExecuteTxRequest request =
        ClientHelper.buildGetTrustPointRequest(ledgerUri, op, this);
    ExecuteTxResponse response = callTxResponse(request, op);
    return ClientHelper.buildGetTrustPointResponse(response);
  }

  /**
   * 创建时间锚点
   * @param ledgerUri           目标账本的URI标识
   * @param timestampInSeconds  创建时间
   * @param proof               可信授时证书
   * @param op                  请求控制项
   * @return
   */
  public CreateTimeAnchorResponse createTimeAnchor(
      String ledgerUri,
      long timestampInSeconds,
      byte[] proof,
      OperationControl op) {
    ExecuteTxRequest request =
        ClientHelper.buildGrantTimeRequest(ledgerUri, timestampInSeconds, proof, op, this);
    ExecuteTxResponse response = callTxResponse(request, op);
    return ClientHelper.buildCreateTimeAnchorResponse(response);
  }

  /**
   * 获取最新的可是授时
   * @param ledgerUri   目标ledger的URI的标识
   * @param op          请求控制项
   * @return
   */
  public GetLastTimeAnchorResponse getLastTimeAnchor(
      String ledgerUri,
      OperationControl op) {
    ExecuteTxRequest request =
        ClientHelper.buildGetLastGrantTimeRequest(ledgerUri, op, this);
    ExecuteTxResponse response = callTxResponse(request, op);
    return ClientHelper.buildGetLastTimeAnchorResponse(response);
  }

  /**
   * 激活账本成员
   * @param ledgerUri   目标ledger的URI的标识
   * @param memberId    成员ID
   * @param op          请求控制项
   * @return
   */
  public EnableMemberResponse enableMember(
      String ledgerUri,
      String memberId,
      OperationControl op) {
    ExecuteTxRequest request =
        ClientHelper.buildEnableMemberRequest(ledgerUri, memberId, op, this);
    ExecuteTxResponse response = callTxResponse(request, op);
    return ClientHelper.buildEnableMemberResponse(response);
  }

  /**
   * 禁用账本成员
   * @param ledgerUri   目标ledger的URI的标识
   * @param memberId    成员ID
   * @param op          请求控制项
   * @return
   */
  public DisableMemberResponse disableMember(
      String ledgerUri,
      String memberId,
      OperationControl op) {
    ExecuteTxRequest request =
        ClientHelper.buildDisableMemberRequest(ledgerUri, memberId, op, this);
    ExecuteTxResponse response = callTxResponse(request, op);
    return ClientHelper.buildDisableMemberResponse(response);
  }

  /**
   * 批量查看时间锚点
   * @param ledgerUri       目标ledger的URI的标识
   * @param startSequence   起始序号
   * @param limit           最大返回数量
   * @param op              请求控制项
   * @return
   */
  public ListTimeAnchorsResponse listTimeAnchors(
      String ledgerUri,
      long startSequence,
      int limit,
      boolean reverse,
      OperationControl op) {
    ExecuteTxRequest request =
        ClientHelper.buildListTimeAnchorsRequest(ledgerUri, startSequence, limit, reverse, op, this);
    ExecuteTxResponse response = callTxResponse(request, op);
    return ClientHelper.buildListTimeAnchorsResponse(response);
  }

  /**
   * 获取区块信息
   * @param ledgerUri       目标ledger的URI的标识
   * @param blockSequence   区块序号
   * @param op              请求控制项
   * @return
   */
  public GetBlockInfoResponse getBlockInfo(
      String ledgerUri,
      long blockSequence,
      OperationControl op) {
    ExecuteTxRequest request =
        ClientHelper.buildGetBlockInfoRequest(ledgerUri, blockSequence, op, this);
    ExecuteTxResponse response = callTxResponse(request, op);
    return ClientHelper.buildGetBlockInfoResponse(response);
  }

  public GetProofResponse getProof(
      String ledgerUri,
      long txSequence,
      OperationControl op) {
    ExecuteTxRequest request =
        ClientHelper.buildGetProofRequest(ledgerUri, txSequence, op, this);
    ExecuteTxResponse response = callTxResponse(request, op);
    return ClientHelper.buildGetProofResponse(response);
  }


  LedgerDBServiceGrpc.LedgerDBServiceBlockingStub getBlockingStub(
      OperationControl operationControl) {
    return LedgerDBServiceGrpc.newBlockingStub(channel)
        .withDeadlineAfter(operationControl.getTimeoutInMillis(),
                           TimeUnit.MILLISECONDS);
  }

  ExecuteTxResponse callTxResponse(
      ExecuteTxRequest executeTxRequest,
      OperationControl operationControl) {
    LedgerDBServiceGrpc.LedgerDBServiceBlockingStub blockingStub =
        getBlockingStub(operationControl);
    try {
      ExecuteTxResponse executeTxResponse =
          blockingStub.executeTx(executeTxRequest);
      return executeTxResponse;
    } catch (StatusRuntimeException t) {
      throw new LedgerRequestException(t);
    }
  }

  public String getClientId() {
    return clientId;
  }

}
