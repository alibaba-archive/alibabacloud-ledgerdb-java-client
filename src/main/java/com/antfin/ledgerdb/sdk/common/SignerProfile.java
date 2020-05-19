package com.antfin.ledgerdb.sdk.common;

import com.antfin.ledgerdb.sdk.crypto.SignerKeyPair;
import com.antfin.ledgerdb.sdk.proto.Sender;

public class SignerProfile {

    private String memberId;

    private Sender.SenderType senderType = Sender.SenderType.REGULAR;

    private final SignerKeyPair signerKeyPair;

    public SignerProfile(String memberId, Sender.SenderType senderType, SignerKeyPair signerKeyPair) {
        this.memberId = memberId;
        this.senderType = senderType;
        this.signerKeyPair = signerKeyPair;
    }

    public SignerProfile(SignerKeyPair signerKeyPair) {
        this.memberId = "";
        this.signerKeyPair = signerKeyPair;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setSenderType(Sender.SenderType senderType) {
        this.senderType = senderType;
    }

    public Sender.SenderType getSenderType() { return senderType; }

    public SignerKeyPair getSignerKeyPair() {
        return signerKeyPair;
    }
}
