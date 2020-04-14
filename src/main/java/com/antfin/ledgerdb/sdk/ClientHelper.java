package com.antfin.ledgerdb.sdk;

//import com.alipay.mychain.sdk.crypto.hash.HashFactory;
//import com.alipay.mychain.sdk.crypto.hash.HashTypeEnum;
import com.antfin.ledgerdb.sdk.common.*;
import com.antfin.ledgerdb.sdk.hash.HashFactory;
import com.antfin.ledgerdb.sdk.hash.HashTypeEnum;
import com.antfin.ledgerdb.sdk.proto.*;
import com.antfin.ledgerdb.sdk.hash.HashFactory;
import com.antfin.ledgerdb.sdk.hash.HashTypeEnum;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

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

  private static TxRequest.Builder generateTxRequestTemplate(
      OperationControl opControl,
      MyLedgerLightClient client) {
    TxRequest.Builder builder = TxRequest.newBuilder();
    builder.setApiVersion(ApiVersion.API_VERSION_1);
    builder.setClientToken(client.getClientId());
    builder.setClientSequence(client.getClientSequenceAfterIncrement());
    builder.setTimestampMillis(opControl.getTimestampMills());
    builder.setNonce(opControl.getNonce());
    builder.setLedgerId(opControl.getLedgerId());
    builder.addAllClues(opControl.getClues());
    if (opControl.isUseDelegateSigner()) {
      throw new UnsupportedOperationException("Delete signer is not supported now");
    } else {
      System.out.println("add regular senders");
      for (SignerProfile signerProfile : opControl.getSignerProfiles()) {
        System.out.print(signerProfile.getMemberId());
        builder.addSenders(
            Sender.newBuilder()
                .setMemberId(signerProfile.getMemberId())
                .setPublicKey(ByteString.copyFrom(signerProfile.getKeyPair().getPublicKeyWithHeader()))
                .setSenderType(Sender.SenderType.REGULAR));
      }
    }
    return builder;
  }

  private static RequestAuth buildRequestAuth(
      OperationControl opControl,
      TxRequest request) {
    RequestAuth.Builder builder = RequestAuth.newBuilder();
    byte[] msgHash = HashFactory.getHash(HashTypeEnum.SHA256).hash(request.toByteArray());
    builder.setDigest(
        Digest.newBuilder()
            .setHashType(Digest.HashType.SHA256)
            .setHash(ByteString.copyFrom(msgHash))
            .build());

    if (opControl.isUseDelegateSigner()) {
      for (LedgerSignerEntity signer : opControl.getDelegateSigners()) {
        byte[] sig = signer.sign(msgHash);
        Signature signature =
            Signature.newBuilder()
                .setSign(ByteString.copyFrom(sig))
                .setSignType(signer.getSignatureType())
                .build();
        builder.addSigns(signature);
      }
    } else {
      for (SignerProfile signerProfile : opControl.getSignerProfiles()) {
        byte[] sig = signerProfile.getKeyPair().sign(msgHash);
        Signature signature =
            Signature.newBuilder()
                .setSign(ByteString.copyFrom(sig))
                .setSignType(signerProfile.getKeyPair().getSignatureType())
                .build();
        builder.addSigns(signature);
      }
    }

    return builder.build();
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
      MyLedgerLightClient client) {
    TxRequest.Builder txBuilder = generateTxRequestTemplate(opControl, client);
    txBuilder.setCustomPayload(ByteString.copyFrom(data));
    return buildExecuteTxRequest(ledgerUri, txBuilder.build(), opControl);
  }

  static ExecuteTxRequest buildGetTransactionRequest(
      String ledgerUri,
      long txSequence,
      OperationControl opControl,
      MyLedgerLightClient client) {
    TxRequest.Builder txBuilder = generateTxRequestTemplate(opControl, client);
    txBuilder.setGetTxPayload(
        GetTxRequestPayload.newBuilder().setSequence(txSequence).build());
    return buildExecuteTxRequest(ledgerUri, txBuilder.build(), opControl);
  }

  static ExecuteTxRequest buildVerifyTransactionRequest(
      String ledgerUri,
      long txSequence,
      OperationControl opControl,
      MyLedgerLightClient client) {
    TxRequest.Builder txBuilder = generateTxRequestTemplate(opControl, client);
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
      MyLedgerLightClient client) {
    TxRequest.Builder txBuilder = generateTxRequestTemplate(opControl, client);
    txBuilder.setListTxsPayload(ListTxsRequestPayload.newBuilder().setStartSeq(start).setLimit(limit).setClue(clue));
    return buildExecuteTxRequest(ledgerUri, txBuilder.build(), opControl);
  }

  static ExecuteTxRequest builGetBlockInfoRequest(
      String ledgerUri,
      long blockSequeneNumber,
      OperationControl opControl,
      MyLedgerLightClient client) {
    TxRequest.Builder txBuilder = generateTxRequestTemplate(opControl, client);
    txBuilder.setGetBlockinfoPayload(GetBlockInfoRequestPayload.newBuilder().setBlockSequence(blockSequeneNumber).build());
    return buildExecuteTxRequest(ledgerUri, txBuilder.build(), opControl);
  }

  static ExecuteTxRequest buildCreateLedgerRequest(
      String ledgerUri,
      LedgerMeta options,
      OperationControl opControl,
      MyLedgerLightClient client) {
    TxRequest.Builder txBuilder = generateTxRequestTemplate(opControl, client);
    txBuilder.setCreateLedgerPayload(
        CreateLedgerRequestPayload.newBuilder()
            .setLedgerMeta(options));
    return buildExecuteTxRequest(ledgerUri,
        txBuilder.build(),
        opControl);
  }

  static ExecuteTxRequest buildDeleteLedgerRequest(
      String ledgerUri,
      OperationControl opControl,
      MyLedgerLightClient client) {
    TxRequest.Builder txBuilder = generateTxRequestTemplate(opControl, client);
    txBuilder.setDeleteLedgerPayload(DeleteLedgerRequestPayload.newBuilder());
    return buildExecuteTxRequest(ledgerUri,
        txBuilder.build(),
        opControl);
  }

  static ExecuteTxRequest buildUpdateLedgerRequest(
      String ledgerUri,
      LedgerMeta options,
      OperationControl opControl,
      MyLedgerLightClient client) {
    TxRequest.Builder txBuilder = generateTxRequestTemplate(opControl, client);
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
      MyLedgerLightClient client) {
    TxRequest.Builder txBuilder = generateTxRequestTemplate(opControl, client);
    txBuilder.setStatLedgerPayload(StatLedgerRequestPayload.newBuilder().build());
    return buildExecuteTxRequest(ledgerUri, txBuilder.build(), opControl);
  }

  static ExecuteTxRequest buildRecoverLedgerRequest(
      String ledgerUri,
      OperationControl opControl,
      MyLedgerLightClient client) {
    TxRequest.Builder txBuilder = generateTxRequestTemplate(opControl, client);
    txBuilder.setRecoverLedgerPayload(RecoverLedgerRequestPayload.newBuilder().build());
    return buildExecuteTxRequest(ledgerUri, txBuilder.build(), opControl);
  }

  static ExecuteTxRequest bulidEnableMemberRequest(
      String ledgerUri,
      String memberToEnable,
      OperationControl opControl,
      MyLedgerLightClient client) {
    TxRequest.Builder txBuilder = generateTxRequestTemplate(opControl, client);
    txBuilder.setEnableMemberPayload(EnableMemberRequestPayload.newBuilder().setMemberId(memberToEnable).build());
    return buildExecuteTxRequest(ledgerUri, txBuilder.build(), opControl);
  }

  static ExecuteTxRequest buildDisableMemberRequest(
      String ledgerUri,
      String memberId,
      OperationControl opControl,
      MyLedgerLightClient client) {
    TxRequest.Builder txBuilder = generateTxRequestTemplate(opControl, client);
    txBuilder.setDisableMemberPayload(DisableMemberRequestPayload.newBuilder().setMemberId(memberId).build());
    return buildExecuteTxRequest(ledgerUri, txBuilder.build(), opControl);
  }

  static ExecuteTxRequest buildCreateMemberRequest(
      String ledgerUri,
      MemberInfo memberInfo,
      OperationControl operationControl,
      MyLedgerLightClient client) {
    TxRequest.Builder txBuilder = generateTxRequestTemplate(operationControl, client);
    txBuilder.setCreateMemberPayload(CreateMemberRequestPayload.newBuilder().setMemberInfo(memberInfo).build());
    return buildExecuteTxRequest(ledgerUri, txBuilder.build(), operationControl);
  }

  static ExecuteTxRequest buildUpdateMemberRequest(
      String ledgerUri,
      MemberInfo memberInfo,
      OperationControl operationControl,
      MyLedgerLightClient client) {
    TxRequest.Builder txBuilder = generateTxRequestTemplate(operationControl, client);
    txBuilder.setUpdateMemberPayload(UpdateMemberRequestPayload.newBuilder().setMemberInfo(memberInfo).build());
    return buildExecuteTxRequest(ledgerUri, txBuilder.build(), operationControl);
  }

  static ExecuteTxRequest buildGetMemberRequest(
      String ledgerUri,
      String memberId,
      OperationControl operationControl,
      MyLedgerLightClient client) {
    TxRequest.Builder txBuilder = generateTxRequestTemplate(operationControl, client);
    //
    txBuilder.setGetMemberPayload(
        GetMemberRequestPayload.newBuilder().setMemberId(memberId).build());
    return buildExecuteTxRequest(ledgerUri, txBuilder.build(), operationControl);
  }

  static ExecuteTxRequest buildDeleteMemberRequest(
      String ledgerUri,
      String memberId,
      OperationControl operationControl,
      MyLedgerLightClient client) {
    TxRequest.Builder txBuilder = generateTxRequestTemplate(operationControl, client);
    txBuilder.setDeleteMemberPayload(
        DeleteMemberRequestPayload.newBuilder().setMemberId(memberId).build());
    return buildExecuteTxRequest(ledgerUri, txBuilder.build(), operationControl);
  }

  static ExecuteTxRequest buildGrantTimeRequest(
      String ledgerUri,
      long timeStampInSeconds,
      byte[] proof,
      OperationControl op,
      MyLedgerLightClient client) {
    TxRequest.Builder txBuilder = generateTxRequestTemplate(op, client);
    txBuilder.setGrantTimePayload(
        GrantTimeRequestPayload.newBuilder()
            .setTimestampSeconds(timeStampInSeconds)
            .setProof(ByteString.copyFrom(proof)).build());
    return buildExecuteTxRequest(ledgerUri, txBuilder.build(), op);
  }

  static ExecuteTxRequest buildGetLastGrantTimeRequest(
      String ledgerUri,
      OperationControl op,
      MyLedgerLightClient client) {
    TxRequest.Builder txBuilder = generateTxRequestTemplate(op, client);
    txBuilder.setGetLastgranttimePayload(GetLastGrantTimeRequestPayload.newBuilder().build());
    return buildExecuteTxRequest(ledgerUri, txBuilder.build(), op);
  }

  static ExecuteTxRequest buildSetTrustPointRequest(
      String ledgerUri,
      long sequence,
      OperationControl op,
      MyLedgerLightClient client) {
    TxRequest.Builder txBuilder = generateTxRequestTemplate(op, client);
    txBuilder.setSetTrustpointPayload(
        SetTrustPointRequestPayload.newBuilder().setTrustSequence(sequence).build());
    return buildExecuteTxRequest(ledgerUri, txBuilder.build(), op);
  }

  static ExecuteTxRequest buildGetTrustPointRequest(
      String ledgerUri,
      OperationControl op,
      MyLedgerLightClient client) {
    TxRequest.Builder txBuilder = generateTxRequestTemplate(op, client);
    txBuilder.setGetTrustpointPayload(
        GetTrustPointRequestPayload.newBuilder().build());
    return buildExecuteTxRequest(ledgerUri, txBuilder.build(), op);
  }


  static void setLedgerResponse(
      ExecuteTxResponse executeTxResponse,
      LedgerResponse<?> response) {
    response.setApiStatus(executeTxResponse.getStatus());
    response.setResponseAuth(executeTxResponse.getResponseAuth());
  }

  static void setLedgerResponse(
      TxResponse txResponse,
      LedgerResponse<?> response) {
    response.setOpStatus(txResponse.getOpStatus());
    response.setOpTimeNanos(txResponse.getOpTimeNanos());
    response.setRequestDigest(txResponse.getRequestDigest());
    response.setApiVersion(txResponse.getApiVersion());
    response.setTotalSequence(txResponse.getTotalSequence());
  }

  static TxResponse buildResponse2(
      ExecuteTxResponse executeTxResponse,
      LedgerResponse<?> response) {
    setLedgerResponse(executeTxResponse, response);
    //if (response.getApiStatus().getCode() != ApiStatus.Code.OK) {
    //  return TxResponse.getDefaultInstance();
    //}
    response.setResponseAuth(executeTxResponse.getResponseAuth());
    TxResponse txResponse = getTxResponse(executeTxResponse);
    setLedgerResponse(txResponse, response);
    return txResponse;
  }

  static AppendTransactionResponse buildAppendTransactionResponse(
      ExecuteTxResponse response) {
    AppendTransactionResponse appendTransactionResponse =
        new AppendTransactionResponse();
    setLedgerResponse(response, appendTransactionResponse);
    if (appendTransactionResponse.getApiStatus().getCode() != ApiStatus.Code.OK) {
      return appendTransactionResponse;
    }
    appendTransactionResponse.setResponseAuth(response.getResponseAuth());
    try {
      TxResponse txResponse = TxResponse.parseFrom(response.getResponseMessage());
      setLedgerResponse(txResponse, appendTransactionResponse);
      appendTransactionResponse.setTransactionId(
          new TransactionId(txResponse.getTxHash()));
    } catch (InvalidProtocolBufferException e) {
      // ignore for now
    }
    return appendTransactionResponse;
  }

  static GetTransactionResponse buildGetTransactionResponse(
      ExecuteTxResponse response) {
    GetTransactionResponse getTransactionResponse =
        new GetTransactionResponse();
    setLedgerResponse(response, getTransactionResponse);
    //if (getTransactionResponse.getApiStatus().getCode() != ApiStatus.Code.OK) {
    //  return getTransactionResponse;
    //}
    getTransactionResponse.setResponseAuth(response.getResponseAuth());
    try {
      TxResponse txResponse = TxResponse.parseFrom(response.getResponseMessage());
      setLedgerResponse(txResponse, getTransactionResponse);
      GetTxResponsePayload payload = txResponse.getGetTxPayload();
      getTransactionResponse.setPayload(payload);
    } catch (InvalidProtocolBufferException e) {
      // ignore for now
    }
    return getTransactionResponse;
  }

  static VerifyTransactionResponse buildVerifyTransactionResponse(
      ExecuteTxResponse response) {
    VerifyTransactionResponse verifyTransactionResponse =
        new VerifyTransactionResponse();
    setLedgerResponse(response, verifyTransactionResponse);
    //if (verifyTransactionResponse.getApiStatus().getCode() != ApiStatus.Code.OK) {
    //  return verifyTransactionResponse;
    //}
    verifyTransactionResponse.setResponseAuth(response.getResponseAuth());
    try {
      TxResponse txResponse = TxResponse.parseFrom(response.getResponseMessage());
      setLedgerResponse(txResponse, verifyTransactionResponse);
    } catch (InvalidProtocolBufferException e) {
      // ignore for now
    }

    return verifyTransactionResponse;
  }

  static ListTransactionsResponse buildListTransactionResponse(
      ExecuteTxResponse executeTxResponse) {
    ListTransactionsResponse response = new ListTransactionsResponse();
    TxResponse txResponse = buildResponse2(executeTxResponse, response);
    response.setPayload(txResponse.getListTxsPayload());
    return response;
  }

  static CreateLedgerResponse buildCreateLedgerResponse(
      ExecuteTxResponse executeTxResponse) {
    CreateLedgerResponse response = new CreateLedgerResponse();
    TxResponse txResponse = buildResponse2(executeTxResponse, response);
    response.setPayload(txResponse.getCreateLedgerPayload());
    return response;
  }

  static UpdateLedgerResponse buildUpdateLedgerResponse(
      ExecuteTxResponse executeTxResponse) {
    UpdateLedgerResponse response = new UpdateLedgerResponse();
    TxResponse txResponse = buildResponse2(executeTxResponse, response);
    response.setPayload(txResponse.getUpdateLedgerPayload());
    return response;
  }

  static StatLedgerResponse buildStatLedgerResponse(
      ExecuteTxResponse executeTxResponse) {
    StatLedgerResponse response = new StatLedgerResponse();
    TxResponse txResponse = buildResponse2(executeTxResponse, response);
    response.setPayload(txResponse.getStatLedgerPayload());
    return response;
  }

  static DeleteLedgerResponse buildDeleteLedgerResponse(
      ExecuteTxResponse executeTxResponse) {
    DeleteLedgerResponse response = new DeleteLedgerResponse();
    TxResponse txResponse = buildResponse2(executeTxResponse, response);
    response.setPayload(txResponse.getDeleteLedgerPayload());
    return response;
  }

  static CreateMemberResponse buildCreateMemberResponse(
      ExecuteTxResponse executeTxResponse) {
    CreateMemberResponse response = new CreateMemberResponse();
    TxResponse txResponse = buildResponse2(executeTxResponse, response);
    response.setPayload(txResponse.getCreateMemberPayload());
    return response;
  }

  static UpdateMemberResponse buildUpdateMemberResponse(
      ExecuteTxResponse executeTxResponse) {
    UpdateMemberResponse response = new UpdateMemberResponse();
    TxResponse txResponse = buildResponse2(executeTxResponse, response);
    response.setPayload(txResponse.getUpdateMemberPayload());
    return response;
  }

  static DeleteMemberResponse buildDeleteMemberResponse(
      ExecuteTxResponse executeTxResponse) {
    DeleteMemberResponse response = new DeleteMemberResponse();
    TxResponse txResponse = buildResponse2(executeTxResponse, response);
    response.setPayload(txResponse.getDeleteMemberPayload());
    return response;
  }

  static GetMemberResponse buildGetMemberResponse(
      ExecuteTxResponse executeTxResponse) {
    GetMemberResponse response = new GetMemberResponse();
    TxResponse txResponse = buildResponse2(executeTxResponse, response);
    response.setPayload(txResponse.getGetMemberPayload());
    return response;
  }

  static GrantTimeResponse buildGrantTimeResponse(
      ExecuteTxResponse executeTxResponse) {
    GrantTimeResponse response = new GrantTimeResponse();
    TxResponse txResponse = buildResponse2(executeTxResponse, response);
    response.setPayload(txResponse.getGrantTimePayload());
    return response;
  }

  static GetLastGrantTimeResponse buildGetLastGrantTimeResponse(
      ExecuteTxResponse executeTxResponse) {
    GetLastGrantTimeResponse response = new GetLastGrantTimeResponse();
    TxResponse txResponse = buildResponse2(executeTxResponse, response);
    response.setPayload(txResponse.getGetLastgranttimePayload());
    return response;
  }

  static SetTrustPointResponse buildSetTrustPointResponse(
      ExecuteTxResponse executeTxResponse) {
    SetTrustPointResponse response = new SetTrustPointResponse();
    TxResponse txResponse = buildResponse2(executeTxResponse, response);
    response.setPayload(txResponse.getSetTrustpointPayload());
    return response;
  }

  static GetTrustPointResponse buildGetTrustPointResponse(
      ExecuteTxResponse executeTxResponse) {
    GetTrustPointResponse response = new GetTrustPointResponse();
    TxResponse txResponse = buildResponse2(executeTxResponse, response);
    response.setPayload(txResponse.getGetTrustpointPayload());
    return response;
  }

  static TxResponse getTxResponse(ExecuteTxResponse executeTxResponse) {
    try {
      return TxResponse.parseFrom(executeTxResponse.getResponseMessage());
    } catch (InvalidProtocolBufferException e) {
      // ignore
      return TxResponse.getDefaultInstance();
    }
  }
}
