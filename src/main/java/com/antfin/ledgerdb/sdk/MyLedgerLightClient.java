package com.antfin.ledgerdb.sdk;

import com.antfin.ledgerdb.sdk.common.*;
import com.antfin.ledgerdb.sdk.proto.*;
import com.google.protobuf.InvalidProtocolBufferException;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyChannelBuilder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import java.io.File;

import javax.net.ssl.SSLException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.antfin.ledgerdb.sdk.ClientHelper.*;

public class MyLedgerLightClient {

  private static final int ESTIMATE_CLIENT_ID_LENGTH = 50;

  private final ManagedChannel channel;
  private final String clientId;
  private final  AtomicInteger clientSequenceCounter = new AtomicInteger(0);

  public MyLedgerLightClient(String proxyHost, int port) {
    this.channel =
        ManagedChannelBuilder.forAddress(proxyHost, port)
                             .usePlaintext()
                             .build();
    this.clientId = generateClientId();
  }

  public MyLedgerLightClient(
      String proxyHost,
      int port,
      SslContext sslContext){
    this.channel =
        NettyChannelBuilder.forAddress(proxyHost, port)
            .sslContext(sslContext).build();
    this.clientId = generateClientId();
  }

  private static String generateClientId() {
    StringBuilder clientIdBuilder = new StringBuilder(ESTIMATE_CLIENT_ID_LENGTH);
    clientIdBuilder.append("MyLedger-Client4j-");
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
   * @param ledgerUri 待创建的ledger的URI标识
   * @param ledgerMeta ledger元信息
   * @param opControl 请求控制项
   * @return
   */
  public CreateLedgerResponse createLedger(
      String ledgerUri,
      LedgerMeta ledgerMeta,
      OperationControl opControl) {
    ExecuteTxRequest request =
        ClientHelper.buildCreateLedgerRequest(ledgerUri, ledgerMeta, opControl, this);
    System.out.println(request);
    ExecuteTxResponse response = callTxResponse(request, opControl);
    return ClientHelper.buildCreateLedgerResponse(response);
  }

  /**
   *
   * @param ledgerUri 待删除的ledger的URI标识
   * @param opControl 请求控制项
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
   *
   * @param ledgerUri 待更新的ledger的URI
   * @param ledgerMeta ledger元信息
   * @param opControl 请求控制项
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
   *
   * @param ledgerUri 待更新的ledger的URI
   * @param opControl 请求控制项
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
   *
   * @param ledgerUri 目标ledger的URI标识
   * @param data 交易内容
   * @param opControl 请求控制项
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
   *
   * @param ledgerUri 目标ledger的URI标识
   * @param txSequence 交易序号
   * @param opControl 请求控制项
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
   *
   * @param ledgerUri 目标ledger的URI标识
   * @param txSequence 交易序号
   * @param opControl 请求控制项
   * @return
   */
  public VerifyTransactionResponse verifyTransaction(
      String ledgerUri,
      long txSequence,
      OperationControl opControl) {
    ExecuteTxRequest request =
        ClientHelper.buildVerifyTransactionRequest(ledgerUri, txSequence, opControl, this);
    ExecuteTxResponse response = callTxResponse(request, opControl);
    return ClientHelper.buildVerifyTransactionResponse(response);
  }

  /**
   * 顺序批量获取历史transaction
   * @param ledgerUri 目标ledger的URI标识
   * @param clue 线索id, ""代表无clue
   * @param beginSequence 起始交易的序号
   * @param limit 返回的历史交易数量
   * @param opControl 请求控制项
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
   * @param ledgerUri 目标ledgerURI
   * @param memberInfo 成员信息
   * @param operationControl 请求控制项
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
   * @param ledgerUri 目标ledgerURI
   * @param memberInfo 成员信息（包含成员的公钥信息）
   * @param operationControl 请求控制项
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
   * 删除成员
   * @param ledgerUri 目标ledgerURI
   * @param memberId 成员的公钥
   * @param opControl 请求控制项
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
   * @param ledgerUri 目标ledgerURI
   * @param memberId 成员ID
   * @param operationControl 请求控制项
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
   * 设置信任锚点
   * @param ledgerUri 目标ledgerURI
   * @param sequence 信任锚点所在的交易序号
   * @param op 请求控制项
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
   * @param ledgerUri 目标ledger的URI标识
   * @param op 请求控制项
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
   * 可信授时
   * @param ledgerUri 目标账本的URI标识
   * @param timestampInSeconds 可信时间戳
   * @param proof 可信授时证书
   * @param op 请求控制项
   * @return
   */
  public GrantTimeResponse grantTime(
      String ledgerUri,
      long timestampInSeconds,
      byte[] proof,
      OperationControl op) {
    ExecuteTxRequest request =
        ClientHelper.buildGrantTimeRequest(ledgerUri, timestampInSeconds, proof, op, this);
    ExecuteTxResponse response = callTxResponse(request, op);
    return ClientHelper.buildGrantTimeResponse(response);
  }

  /**
   * 获取最新的可是授时
   * @param ledgerUri 目标ledger的URI的标识
   * @param op 请求控制项
   * @return
   */
  public GetLastGrantTimeResponse getLastGrantTime(
      String ledgerUri,
      OperationControl op) {
    ExecuteTxRequest request =
        ClientHelper.buildGetLastGrantTimeRequest(ledgerUri, op, this);
    ExecuteTxResponse response = callTxResponse(request, op);
    return ClientHelper.buildGetLastGrantTimeResponse(response);
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
      operationControl.setStatus(executeTxResponse.getStatus());
      try {
        TxResponse txResponse =
                TxResponse.parseFrom(executeTxResponse.getResponseMessage());
        operationControl.setOpStatus(txResponse.getOpStatus());
      } catch (InvalidProtocolBufferException e) {
        operationControl.setOpStatus(ApiStatus.newBuilder().setCode(ApiStatus.Code.INTERNAL).build());
      }
      return executeTxResponse;
    } catch (StatusRuntimeException t) {
      System.out.println("exception in call tx");
      System.out.println(t.getMessage());
      t.printStackTrace();
      operationControl.setStatus(ApiStatus.newBuilder().setCode(ApiStatus.Code.UNAVAILABLE).setMessage("Network unablable").build());
      return handleCallTxResponseThrowable(t);
    }
  }


  ExecuteTxResponse handleCallTxResponseThrowable(Throwable t) {
    Status status = Status.fromThrowable(t);
    ApiStatus apiStatus =
        ApiStatus.newBuilder()
            .setCodeValue(status.getCode().value())
            .build();
    return ExecuteTxResponse.newBuilder()
        .setStatus(apiStatus).build();
  }

  public String getClientId() {
    return clientId;
  }

  public int getClientSequenceAfterIncrement() {
    return clientSequenceCounter.incrementAndGet();
  }

}
