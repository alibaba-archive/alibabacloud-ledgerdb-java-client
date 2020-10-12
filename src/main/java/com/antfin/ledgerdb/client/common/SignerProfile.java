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

import com.antfin.ledgerdb.client.crypto.SignerKeyPair;
import com.antfin.ledgerdb.client.proto.Sender;

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
