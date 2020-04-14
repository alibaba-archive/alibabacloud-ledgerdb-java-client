package com.antfin.ledgerdb.sdk.common;

import com.antfin.ledgerdb.sdk.crypto.KeyPair;
import com.antfin.ledgerdb.sdk.proto.MemberInfo;

public class SignerProfile {

    private String memberId;

    private final KeyPair keyPair;

    public SignerProfile(String memberId, KeyPair keyPair) {
        this.memberId = memberId;
        this.keyPair = keyPair;
    }

    public SignerProfile(KeyPair keyPair) {
        this.memberId = "";
        this.keyPair = keyPair;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getMemberId() {
        return memberId;
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }
}
