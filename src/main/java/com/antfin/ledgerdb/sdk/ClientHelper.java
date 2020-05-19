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
import com.antfin.ledgerdb.sdk.exception.LedgerException;
import com.antfin.ledgerdb.sdk.hash.HashFactory;
import com.antfin.ledgerdb.sdk.proto.*;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import java.net.URI;
import java.util.List;

class ClientHelper {

  private static ExecuteTxRequest buildExecuteTxRequest(
      TxRequest txRequest,
      RequestAuth requestAuth,
      String ledgerUri) {
    ExecuteTxRequest.Builder builder = ExecuteTxRequest.newBuilder();
    builder.setRequestMessage(ByteString.copyFrom(txRequest.toByteArray()))
        .setRequestAuth(requestAuth).build();
    builder.setUri(ledgerUri);
    return builder.build();
  }

  static URI checkLedgerUri(String ledgerUri) {
    URI uri = URI.create(ledgerUri);
    if (!"ledger".equals(uri.getScheme())) {
      throw new LedgerException("Invalid schema in ledger uri: " + ledgerUri);
    }
    if (uri.getPath().length() == 0 || "/".equals(uri.getPath())) {
      throw new LedgerException("Ledger id not found in uri: " +ledgerUri);
    }

    String ledgerId = uri.getPath().substring(1);
    for (int i = 0; i < ledgerId.length(); i++) {
      char c = ledgerId.charAt(i);
      if ((!Character.isLetterOrDigit(c)) && (c != '_') && (c != '-')) {
        throw new LedgerException("Invalid ledger id: " + ledgerId);
      }
    }
    return uri;
  }

  static void checkUserPublicKey(ByteString publicKey) {
    byte[] publicKeyBytes = publicKey.toByteArray();
    if (publicKeyBytes.length != 65) {
      throw new LedgerException("Public key should be 65 bytes long");
    }
    if (publicKeyBytes[0] != 0x04) {
      throw new LedgerException("First byte of public key should be 0x04");
    }
  }

  private static TxRequest.Builder generateTxRequestTemplate(
      String ledgerUri,
      OperationControl opControl,
      LedgerDBLightClient client) {
    TxRequest.Builder builder = TxRequest.newBuilder();
    builder.setApiVersion(ApiVersion.API_VERSION_1);
    builder.setClientToken(client.getClientId());
    builder.setTimestampMillis(opControl.getTimestampMills());
    builder.setNonce(opControl.getNonce());
    URI uri = checkLedgerUri(ledgerUri);
    String ledgerId = uri.getPath().substring(1);
    builder.setLedgerId(ledgerId);
    for (String clue : opControl.getClues()) {
      builder.addClues(ByteString.copyFromUtf8(clue));
    }
    for (SignerProfile signerProfile : opControl.getSignerProfiles()) {
      builder.addSenders(
              Sender.newBuilder()
                      .setMemberId(signerProfile.getMemberId())
                      .setSenderType(signerProfile.getSenderType())
                      .setPublicKey(ByteString.copyFrom(signerProfile.getSignerKeyPair().getPublicKeyWithHeader())));
    }
    return builder;
  }

  private static RequestAuth buildRequestAuth(
      OperationControl opControl,
      TxRequest request) {
    RequestAuth.Builder builder = RequestAuth.newBuilder();
    byte[] requestMessage = request.toByteArray();
    opControl.setRequestMessage(requestMessage);
    byte[] msgHash = HashFactory.getHash(opControl.getDigestHashType()).hash(requestMessage);
    builder.setDigest(
        Digest.newBuilder()
            .setHashType(opControl.getDigestHashType())
            .setHash(ByteString.copyFrom(msgHash))
            .build());
    for (SignerProfile signerProfile : opControl.getSignerProfiles()) {
      byte[] sig = signerProfile.getSignerKeyPair().sign(msgHash);
      Signature signature =
              Signature.newBuilder()
                      .setSign(ByteString.copyFrom(sig))
                      .setSignType(signerProfile.getSignerKeyPair().getSignatureType())
                      .build();
      builder.addSigns(signature);
    }
    RequestAuth requestAuth = builder.build();
    opControl.setRequestAuth(requestAuth);
    return requestAuth;
  }

  private static ExecuteTxRequest buildExecuteTxRequest(
      String ledgerUri,
      TxRequest txRequest,
      OperationControl opControl) {
    RequestAuth requestAuth = buildRequestAuth(opControl, txRequest);
    return buildExecuteTxRequest(txRequest, requestAuth, ledgerUri);
  }

  static ExecuteTxRequest buildAppendTransactionRequest(
      String ledgerUri,
      byte[] data,
      OperationControl opControl,
      LedgerDBLightClient client) {
    TxRequest.Builder txBuilder = generateTxRequestTemplate(ledgerUri, opControl, client);
    txBuilder.setCustomPayload(ByteString.copyFrom(data));
    return buildExecuteTxRequest(ledgerUri, txBuilder.build(), opControl);
  }

  static ExecuteTxRequest buildGetTransactionRequest(
      String ledgerUri,
      long txSequence,
      OperationControl opControl,
      LedgerDBLightClient client) {
    TxRequest.Builder txBuilder = generateTxRequestTemplate(ledgerUri, opControl, client);
    txBuilder.setGetTxPayload(
        GetTxRequestPayload.newBuilder().setSequence(txSequence).build());
    return buildExecuteTxRequest(ledgerUri, txBuilder.build(), opControl);
  }

  static ExecuteTxRequest buildVerifyTransactionRequest(
      String ledgerUri,
      long txSequence,
      OperationControl opControl,
      LedgerDBLightClient client) {
    TxRequest.Builder txBuilder = generateTxRequestTemplate(ledgerUri, opControl, client);
    txBuilder.setExistTxPayload(
        ExistTxRequestPayload.newBuilder().setSequence(txSequence).build());
    return buildExecuteTxRequest(ledgerUri, txBuilder.build(), opControl);
  }

  static ExecuteTxRequest buildListTransactionRequest(
      String ledgerUri,
      String clue,
      long start,
      int limit,
      OperationControl opControl,
      LedgerDBLightClient client) {
    TxRequest.Builder txBuilder = generateTxRequestTemplate(ledgerUri, opControl, client);
    txBuilder.setListTxsPayload(
        ListTxsRequestPayload.newBuilder()
            .setStartSeq(start)
            .setLimit(limit)
            .setClue(ByteString.copyFromUtf8(clue)));
    return buildExecuteTxRequest(ledgerUri, txBuilder.build(), opControl);
  }

  static ExecuteTxRequest buildGetBlockInfoRequest(
      String ledgerUri,
      long blockSequence,
      OperationControl opControl,
      LedgerDBLightClient client) {
    TxRequest.Builder txBuilder = generateTxRequestTemplate(ledgerUri, opControl, client);
    txBuilder.setGetBlockinfoPayload(GetBlockInfoRequestPayload.newBuilder().setBlockSequence(blockSequence).build());
    return buildExecuteTxRequest(ledgerUri, txBuilder.build(), opControl);
  }

  static ExecuteTxRequest buildCreateLedgerRequest(
      String ledgerUri,
      LedgerMeta options,
      List<MemberInfo> memberInfos,
      OperationControl opControl,
      LedgerDBLightClient client) {
    TxRequest.Builder txBuilder = generateTxRequestTemplate(ledgerUri, opControl, client);
    txBuilder.setCreateLedgerPayload(
        CreateLedgerRequestPayload.newBuilder()
            .setLedgerMeta(options).addAllMemberInfo(memberInfos));
    return buildExecuteTxRequest(ledgerUri,
        txBuilder.build(),
        opControl);
  }

  static ExecuteTxRequest buildDeleteLedgerRequest(
      String ledgerUri,
      OperationControl opControl,
      LedgerDBLightClient client) {
    TxRequest.Builder txBuilder = generateTxRequestTemplate(ledgerUri, opControl, client);
    txBuilder.setDeleteLedgerPayload(DeleteLedgerRequestPayload.newBuilder());
    return buildExecuteTxRequest(ledgerUri,
        txBuilder.build(),
        opControl);
  }

  static ExecuteTxRequest buildUpdateLedgerRequest(
      String ledgerUri,
      LedgerMeta options,
      OperationControl opControl,
      LedgerDBLightClient client) {
    TxRequest.Builder txBuilder = generateTxRequestTemplate(ledgerUri, opControl, client);
    txBuilder.setUpdateLedgerPayload(
        UpdateLedgerRequestPayload.newBuilder().setLedgerMeta(options));
    return buildExecuteTxRequest(
        ledgerUri,
        txBuilder.build(),
        opControl);
  }

  static ExecuteTxRequest buildStatLedgerRequest(
      String ledgerUri,
      OperationControl opControl,
      LedgerDBLightClient client) {
    TxRequest.Builder txBuilder = generateTxRequestTemplate(ledgerUri, opControl, client);
    txBuilder.setStatLedgerPayload(StatLedgerRequestPayload.newBuilder().build());
    return buildExecuteTxRequest(ledgerUri, txBuilder.build(), opControl);
  }

  static ExecuteTxRequest buildRecoverLedgerRequest(
      String ledgerUri,
      OperationControl opControl,
      LedgerDBLightClient client) {
    TxRequest.Builder txBuilder = generateTxRequestTemplate(ledgerUri, opControl, client);
    txBuilder.setRecoverLedgerPayload(RecoverLedgerRequestPayload.newBuilder().build());
    return buildExecuteTxRequest(ledgerUri, txBuilder.build(), opControl);
  }

  static ExecuteTxRequest buildGetProofRequest(
      String ledgerUri,
      long txSequence,
      OperationControl operationControl,
      LedgerDBLightClient client) {
    TxRequest.Builder txBuilder = generateTxRequestTemplate(ledgerUri, operationControl, client);
    txBuilder.setGetProofPayload(GetProofRequestPayload.newBuilder().setTxSequence(txSequence).build());
    return buildExecuteTxRequest(ledgerUri, txBuilder.build(), operationControl);
  }

  static ExecuteTxRequest buildEnableMemberRequest(
      String ledgerUri,
      String memberToEnable,
      OperationControl opControl,
      LedgerDBLightClient client) {
    TxRequest.Builder txBuilder = generateTxRequestTemplate(ledgerUri, opControl, client);
    txBuilder.setEnableMemberPayload(EnableMemberRequestPayload.newBuilder().setMemberId(memberToEnable).build());
    return buildExecuteTxRequest(ledgerUri, txBuilder.build(), opControl);
  }

  static ExecuteTxRequest buildDisableMemberRequest(
      String ledgerUri,
      String memberId,
      OperationControl opControl,
      LedgerDBLightClient client) {
    TxRequest.Builder txBuilder = generateTxRequestTemplate(ledgerUri, opControl, client);
    txBuilder.setDisableMemberPayload(DisableMemberRequestPayload.newBuilder().setMemberId(memberId).build());
    return buildExecuteTxRequest(ledgerUri, txBuilder.build(), opControl);
  }

  static ExecuteTxRequest buildCreateMemberRequest(
      String ledgerUri,
      MemberInfo memberInfo,
      OperationControl operationControl,
      LedgerDBLightClient client) {
    TxRequest.Builder txBuilder = generateTxRequestTemplate(ledgerUri, operationControl, client);
    txBuilder.setCreateMemberPayload(CreateMemberRequestPayload.newBuilder().setMemberInfo(memberInfo).build());
    return buildExecuteTxRequest(ledgerUri, txBuilder.build(), operationControl);
  }

  static ExecuteTxRequest buildUpdateMemberRequest(
      String ledgerUri,
      MemberInfo memberInfo,
      OperationControl operationControl,
      LedgerDBLightClient client) {
    TxRequest.Builder txBuilder = generateTxRequestTemplate(ledgerUri, operationControl, client);
    txBuilder.setUpdateMemberPayload(UpdateMemberRequestPayload.newBuilder().setMemberInfo(memberInfo).build());
    return buildExecuteTxRequest(ledgerUri, txBuilder.build(), operationControl);
  }

  static ExecuteTxRequest buildUpdateMemberKeyRequest(
          String ledgerUri,
          String memberId,
          ByteString publicKey,
          OperationControl operationControl,
          LedgerDBLightClient client) {
    TxRequest.Builder txBuilder = generateTxRequestTemplate(ledgerUri, operationControl, client);
    txBuilder.setUpdateMemberKeyPayload(UpdateMemberKeyRequestPayload.newBuilder().setMemberId(memberId).setPublicKey(publicKey).build());
    return buildExecuteTxRequest(ledgerUri, txBuilder.build(), operationControl);
  }

  static ExecuteTxRequest buildUpdateMemberAclsRequest(
          String ledgerUri,
          String memberId,
          List<PermissionItem> permissions,
          OperationControl operationControl,
          LedgerDBLightClient client) {
    TxRequest.Builder txBuilder = generateTxRequestTemplate(ledgerUri, operationControl, client);
    txBuilder.setUpdateMemberPermissionsPayload(
        UpdateMemberPermissionsRequestPayload.newBuilder()
            .setMemberId(memberId).addAllPermissions(permissions).build());
    return buildExecuteTxRequest(ledgerUri, txBuilder.build(), operationControl);
  }

  static ExecuteTxRequest buildGetMemberRequest(
          String ledgerUri,
          String memberId,
          OperationControl operationControl,
          LedgerDBLightClient client) {
    TxRequest.Builder txBuilder = generateTxRequestTemplate(ledgerUri, operationControl, client);
    txBuilder.setGetMemberPayload(
            GetMemberRequestPayload.newBuilder().setMemberId(memberId).build());
    return buildExecuteTxRequest(ledgerUri, txBuilder.build(), operationControl);
  }

  static ExecuteTxRequest buildListMembersRequest(
          String ledgerUri,
          String lastMemberId,
          int limit,
          OperationControl operationControl,
          LedgerDBLightClient client) {
    TxRequest.Builder txBuilder = generateTxRequestTemplate(ledgerUri, operationControl, client);
    txBuilder.setListMembersPayload(
            ListMembersRequestPayload.newBuilder().setLastMemberId(lastMemberId).setLimit(limit).build());
    return buildExecuteTxRequest(ledgerUri, txBuilder.build(), operationControl);
  }

  static ExecuteTxRequest buildDeleteMemberRequest(
      String ledgerUri,
      String memberId,
      OperationControl operationControl,
      LedgerDBLightClient client) {
    TxRequest.Builder txBuilder = generateTxRequestTemplate(ledgerUri, operationControl, client);
    txBuilder.setDeleteMemberPayload(
        DeleteMemberRequestPayload.newBuilder().setMemberId(memberId).build());
    return buildExecuteTxRequest(ledgerUri, txBuilder.build(), operationControl);
  }

  static ExecuteTxRequest buildGrantTimeRequest(
      String ledgerUri,
      long timeStampInSeconds,
      byte[] proof,
      OperationControl op,
      LedgerDBLightClient client) {
    TxRequest.Builder txBuilder = generateTxRequestTemplate(ledgerUri, op, client);
    txBuilder.setCreateTimeanchorPayload(
        CreateTimeAnchorRequestPayload.newBuilder()
            .setTimestampSeconds(timeStampInSeconds)
            .setProof(ByteString.copyFrom(proof)).build());
    return buildExecuteTxRequest(ledgerUri, txBuilder.build(), op);
  }

  static ExecuteTxRequest buildGetLastGrantTimeRequest(
      String ledgerUri,
      OperationControl op,
      LedgerDBLightClient client) {
    TxRequest.Builder txBuilder = generateTxRequestTemplate(ledgerUri, op, client);
    txBuilder.setGetLastTimeanchorPayload(GetLastTimeAnchorRequestPayload.newBuilder().build());
    return buildExecuteTxRequest(ledgerUri, txBuilder.build(), op);
  }

  static ExecuteTxRequest buildSetTrustPointRequest(
      String ledgerUri,
      long sequence,
      OperationControl op,
      LedgerDBLightClient client) {
    TxRequest.Builder txBuilder = generateTxRequestTemplate(ledgerUri, op, client);
    txBuilder.setSetTrustpointPayload(
        SetTrustPointRequestPayload.newBuilder().setTrustSequence(sequence).build());
    return buildExecuteTxRequest(ledgerUri, txBuilder.build(), op);
  }

  static ExecuteTxRequest buildGetTrustPointRequest(
      String ledgerUri,
      OperationControl op,
      LedgerDBLightClient client) {
    TxRequest.Builder txBuilder = generateTxRequestTemplate(ledgerUri, op, client);
    txBuilder.setGetTrustpointPayload(
        GetTrustPointRequestPayload.newBuilder().build());
    return buildExecuteTxRequest(ledgerUri, txBuilder.build(), op);
  }

  static ExecuteTxRequest buildListTimeAnchorsRequest(
      String ledgerUri,
      long startSequence,
      int limit,
      boolean reverse,
      OperationControl op,
      LedgerDBLightClient client) {
    TxRequest.Builder txBuilder = generateTxRequestTemplate(ledgerUri, op, client);
    txBuilder.setListTimeanchorsPayload(
        ListTimeAnchorsRequestPayload.newBuilder()
            .setStartSeq(startSequence).setLimit(limit).setReverse(reverse).build());
    return buildExecuteTxRequest(ledgerUri, txBuilder.build(), op);
  }

  private static void setLedgerResponse(
      ExecuteTxResponse executeTxResponse,
      LedgerResponse<?> response) {
    response.setApiStatus(executeTxResponse.getStatus());
    response.setResponseAuth(executeTxResponse.getResponseAuth());
  }

  private static void setLedgerResponse(
      TxResponse txResponse,
      LedgerResponse<?> response) {
    response.setOpStatus(txResponse.getOpStatus());
    response.setOpTimeNanos(txResponse.getOpTimeNanos());
    response.setRequestDigest(txResponse.getRequestDigest());
    response.setApiVersion(txResponse.getApiVersion());
    response.setTotalSequence(txResponse.getTotalSequence());
    response.setBlockSequence(txResponse.getBlockSequence());
    response.setStateRootHash(txResponse.getStateRootHash().toByteArray());
    response.setTxHash(txResponse.getTxHash().toByteArray());

  }

  static TxResponse buildResponse(
      ExecuteTxResponse executeTxResponse,
      LedgerResponse<?> response) {
    setLedgerResponse(executeTxResponse, response);
    response.setResponseAuth(executeTxResponse.getResponseAuth());
    TxResponse txResponse = getTxResponse(executeTxResponse);
    setLedgerResponse(txResponse, response);
    return txResponse;
  }

  static AppendTransactionResponse buildAppendTransactionResponse(
      ExecuteTxResponse executeTxResponse) {
    AppendTransactionResponse response =
        new AppendTransactionResponse();
    TxResponse txResponse = buildResponse(executeTxResponse, response);
    response.setPayload(txResponse.getCustomPayload().toByteArray());
    return response;
  }

  static GetTransactionResponse buildGetTransactionResponse(
      ExecuteTxResponse executeTxResponse) {
    GetTransactionResponse response =
        new GetTransactionResponse();
    TxResponse txResponse = buildResponse(executeTxResponse, response);
    response.setPayload(txResponse.getGetTxPayload());
    return response;
  }

  static ExistTransactionResponse buildExistTransactionResponse(
      ExecuteTxResponse executeTxResponse) {
    ExistTransactionResponse response =
        new ExistTransactionResponse();
    TxResponse txResponse = buildResponse(executeTxResponse, response);
    response.setPayload(txResponse.getExistTxPayload());
    return response;
  }

  static ListTransactionsResponse buildListTransactionResponse(
      ExecuteTxResponse executeTxResponse) {
    ListTransactionsResponse response = new ListTransactionsResponse();
    TxResponse txResponse = buildResponse(executeTxResponse, response);
    response.setPayload(txResponse.getListTxsPayload());
    return response;
  }

  static CreateLedgerResponse buildCreateLedgerResponse(
      ExecuteTxResponse executeTxResponse) {
    CreateLedgerResponse response = new CreateLedgerResponse();
    TxResponse txResponse = buildResponse(executeTxResponse, response);
    response.setPayload(txResponse.getCreateLedgerPayload());
    return response;
  }

  static UpdateLedgerResponse buildUpdateLedgerResponse(
      ExecuteTxResponse executeTxResponse) {
    UpdateLedgerResponse response = new UpdateLedgerResponse();
    TxResponse txResponse = buildResponse(executeTxResponse, response);
    response.setPayload(txResponse.getUpdateLedgerPayload());
    return response;
  }

  static StatLedgerResponse buildStatLedgerResponse(
      ExecuteTxResponse executeTxResponse) {
    StatLedgerResponse response = new StatLedgerResponse();
    TxResponse txResponse = buildResponse(executeTxResponse, response);
    response.setPayload(txResponse.getStatLedgerPayload());
    return response;
  }

  static DeleteLedgerResponse buildDeleteLedgerResponse(
      ExecuteTxResponse executeTxResponse) {
    DeleteLedgerResponse response = new DeleteLedgerResponse();
    TxResponse txResponse = buildResponse(executeTxResponse, response);
    response.setPayload(txResponse.getDeleteLedgerPayload());
    return response;
  }

  static CreateMemberResponse buildCreateMemberResponse(
      ExecuteTxResponse executeTxResponse) {
    CreateMemberResponse response = new CreateMemberResponse();
    TxResponse txResponse = buildResponse(executeTxResponse, response);
    response.setPayload(txResponse.getCreateMemberPayload());
    return response;
  }

  static UpdateMemberResponse buildUpdateMemberResponse(
      ExecuteTxResponse executeTxResponse) {
    UpdateMemberResponse response = new UpdateMemberResponse();
    TxResponse txResponse = buildResponse(executeTxResponse, response);
    response.setPayload(txResponse.getUpdateMemberPayload());
    return response;
  }

  static UpdateMemberKeyResponse buildUpdateMemberKeyResponse(
          ExecuteTxResponse executeTxResponse) {
    UpdateMemberKeyResponse response = new UpdateMemberKeyResponse();
    TxResponse txResponse = buildResponse(executeTxResponse, response);
    response.setPayload(txResponse.getUpdateMemberKeyPayload());
    return response;
  }

  static UpdateMemberAclsResponse buildUpdateMemberAclsResponse(
          ExecuteTxResponse executeTxResponse) {
    UpdateMemberAclsResponse response = new UpdateMemberAclsResponse();
    TxResponse txResponse = buildResponse(executeTxResponse, response);
    response.setPayload(txResponse.getUpdateMemberPermissionsPayload());
    return response;
  }

  static DeleteMemberResponse buildDeleteMemberResponse(
      ExecuteTxResponse executeTxResponse) {
    DeleteMemberResponse response = new DeleteMemberResponse();
    TxResponse txResponse = buildResponse(executeTxResponse, response);
    response.setPayload(txResponse.getDeleteMemberPayload());
    return response;
  }

  static GetMemberResponse buildGetMemberResponse(
      ExecuteTxResponse executeTxResponse) {
    GetMemberResponse response = new GetMemberResponse();
    TxResponse txResponse = buildResponse(executeTxResponse, response);
    response.setPayload(txResponse.getGetMemberPayload());
    return response;
  }

  static ListMembersResponse buildListMembersResponse(
          ExecuteTxResponse executeTxResponse) {
    ListMembersResponse response = new ListMembersResponse();
    TxResponse txResponse = buildResponse(executeTxResponse, response);
    response.setPayload(txResponse.getListMembersPayload());
    return response;
  }

  static CreateTimeAnchorResponse buildCreateTimeAnchorResponse(
      ExecuteTxResponse executeTxResponse) {
    CreateTimeAnchorResponse response = new CreateTimeAnchorResponse();
    TxResponse txResponse = buildResponse(executeTxResponse, response);
    response.setPayload(txResponse.getCreateTimeanchorPayload());
    return response;
  }

  static GetLastTimeAnchorResponse buildGetLastTimeAnchorResponse(
      ExecuteTxResponse executeTxResponse) {
    GetLastTimeAnchorResponse response = new GetLastTimeAnchorResponse();
    TxResponse txResponse = buildResponse(executeTxResponse, response);
    response.setPayload(txResponse.getGetLastTimeanchorPayload());
    return response;
  }

  static SetTrustPointResponse buildSetTrustPointResponse(
      ExecuteTxResponse executeTxResponse) {
    SetTrustPointResponse response = new SetTrustPointResponse();
    TxResponse txResponse = buildResponse(executeTxResponse, response);
    response.setPayload(txResponse.getSetTrustpointPayload());
    return response;
  }

  static GetTrustPointResponse buildGetTrustPointResponse(
      ExecuteTxResponse executeTxResponse) {
    GetTrustPointResponse response = new GetTrustPointResponse();
    TxResponse txResponse = buildResponse(executeTxResponse, response);
    response.setPayload(txResponse.getGetTrustpointPayload());
    return response;
  }

  static EnableMemberResponse buildEnableMemberResponse(
      ExecuteTxResponse executeTxResponse) {
    EnableMemberResponse response = new EnableMemberResponse();
    TxResponse txResponse = buildResponse(executeTxResponse, response);
    response.setPayload(txResponse.getEnableMemberPayload());
    return response;
  }

  static DisableMemberResponse buildDisableMemberResponse(
      ExecuteTxResponse executeTxResponse) {
    DisableMemberResponse response = new DisableMemberResponse();
    TxResponse txResponse = buildResponse(executeTxResponse, response);
    response.setPayload(txResponse.getDisableMemberPayload());
    return response;
  }

  static ListTimeAnchorsResponse buildListTimeAnchorsResponse(
      ExecuteTxResponse executeTxResponse) {
    ListTimeAnchorsResponse response = new ListTimeAnchorsResponse();
    TxResponse txResponse = buildResponse(executeTxResponse, response);
    response.setPayload(txResponse.getListTimeanchorsPayload());
    return response;
  }

  static GetBlockInfoResponse buildGetBlockInfoResponse(
      ExecuteTxResponse executeTxResponse) {
    GetBlockInfoResponse response = new GetBlockInfoResponse();
    TxResponse txResponse = buildResponse(executeTxResponse, response);
    response.setPayload(txResponse.getGetBlockinfoPayload());
    return response;
  }

  static GetProofResponse buildGetProofResponse(
      ExecuteTxResponse executeTxResponse) {
    GetProofResponse response = new GetProofResponse();
    TxResponse txResponse = buildResponse(executeTxResponse, response);
    response.setPayload(txResponse.getGetProofPayload());
    return response;
  }

  static TxResponse getTxResponse(ExecuteTxResponse executeTxResponse) {
    try {
      return TxResponse.parseFrom(executeTxResponse.getResponseMessage());
    } catch (InvalidProtocolBufferException e) {
      throw new LedgerException("Invalid proto exception", e);
    }
  }
}
