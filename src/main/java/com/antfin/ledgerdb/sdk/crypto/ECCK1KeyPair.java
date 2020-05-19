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

package com.antfin.ledgerdb.sdk.crypto;

import com.antfin.ledgerdb.sdk.exception.LedgerException;
import com.antfin.ledgerdb.sdk.proto.Signature;
import com.antfin.ledgerdb.sdk.util.NumericUtils;

import org.apache.commons.lang3.ArrayUtils;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9IntegerConverter;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.crypto.signers.HMacDSAKCalculator;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;
import org.bouncycastle.math.ec.custom.sec.SecP256K1Curve;

import java.math.BigInteger;
import java.security.KeyPair;
import java.util.Arrays;

/**
 * Elliptic Curve SECP-256 k1 generated key pair
 * <p> Adapted from <a
 * href="https://github.com/web3j/web3j/blob/master/crypto/src/main/java/org/web3j/crypto/ECKeyPair.java">
 * ECKeyPair</a> and <a
 * href="https://github.com/web3j/web3j/blob/master/crypto/src/main/java/org/web3j/crypto/Sign.java">
 * Sign</a>
 */
public class ECCK1KeyPair implements SignerKeyPair {

  private static final X9ECParameters CURVE_PARAMS = CustomNamedCurves.getByName("secp256k1");
  private static final ECDomainParameters CURVE = new ECDomainParameters(
      CURVE_PARAMS.getCurve(), CURVE_PARAMS.getG(), CURVE_PARAMS.getN(), CURVE_PARAMS.getH());
  private static final BigInteger HALF_CURVE_ORDER = CURVE_PARAMS.getN().shiftRight(1);

  private final byte[] privateKey;
  private final byte[] publicKey;
  private final byte[] publicKeyEncoded;

  public static BigInteger publicKeyFromPrivate(BigInteger privKey) {
    ECPoint point = publicPointFromPrivate(privKey);

    byte[] encoded = point.getEncoded(false);
    return new BigInteger(1, Arrays.copyOfRange(encoded, 1, encoded.length)); // remove prefix
  }

  /**
   * Returns public key point from the given private key.
   *
   * @param privKey the private key to derive the public key from
   * @return ECPoint public key
   */
  public static ECPoint publicPointFromPrivate(BigInteger privKey) {
    /*
     * TODO: FixedPointCombMultiplier currently doesn't support scalars longer than the group
     * order, but that could change in future versions.
     */
    if (privKey.bitLength() > CURVE.getN().bitLength()) {
      privKey = privKey.mod(CURVE.getN());
    }
    return new FixedPointCombMultiplier().multiply(CURVE.getG(), privKey);
  }


  public ECCK1KeyPair(byte[] privateKey) {
    BigInteger privateKeyInt = NumericUtils.toBigInt(privateKey);
    byte[] encoded = publicPointFromPrivate(privateKeyInt).getEncoded(false);
    this.publicKeyEncoded = encoded;
    this.publicKey = Arrays.copyOfRange(encoded, 1, encoded.length);
    this.privateKey = Arrays.copyOfRange(privateKey, 0, privateKey.length);
  }

  public ECCK1KeyPair(byte[] privateKey, byte[] publicKey) {
    this.privateKey = Arrays.copyOfRange(privateKey, 0, privateKey.length);
    this.publicKey = Arrays.copyOfRange(publicKey, 0, publicKey.length);
    byte[] pubKeyEncoded = new byte[65];
    pubKeyEncoded[0] = 0x04;
    System.arraycopy(getPublicKey(),0, pubKeyEncoded,1, getPublicKey().length);
    this.publicKeyEncoded = pubKeyEncoded;
  }

  public ECCK1KeyPair(KeyPair keyPair) {
    BCECPrivateKey privateKeyBC = (BCECPrivateKey) keyPair.getPrivate();
    BCECPublicKey publicKeyBC = (BCECPublicKey) keyPair.getPublic();

    BigInteger privateKeyValue = privateKeyBC.getD();
    privateKey = NumericUtils.toBytesPadded(privateKeyValue, 32);

    byte[] publicKeyBytes = publicKeyBC.getQ().getEncoded(false);
    publicKey = Arrays.copyOfRange(publicKeyBytes, 1, publicKeyBytes.length);
    this.publicKeyEncoded = publicKeyBytes;
  }

  public static ECCK1KeyPair create(KeyPair keyPair) {
    return new ECCK1KeyPair(keyPair);
  }

  public static ECCK1KeyPair createFromPrivateKey(byte[] privateKey) {
    return new ECCK1KeyPair(privateKey);
  }

  public byte[] sign(byte[] message) {
    if (ArrayUtils.isEmpty(message) || message.length != 32) {
      throw new LedgerException("need hash size 32 but " + (ArrayUtils.isEmpty(message) ? 0 : message.length));
    }

    ECDSASigner signer = new ECDSASigner(new HMacDSAKCalculator(new SHA256Digest()));

    ECPrivateKeyParameters privKey = new ECPrivateKeyParameters(new BigInteger(1, getPrivateKey()), this.CURVE);
    signer.init(true, privKey);
    BigInteger[] components = signer.generateSignature(message);
    components[1] = toCanonicalised(components[1]);

    int recId = -1;
    int length = 4;

    BigInteger pubKey = new BigInteger(1, getPublicKey());
    for (int i = 0; i < length; i++) {
      BigInteger k = recoverPublicKey(i, components[0], components[1], message);
      if (k != null && k.equals(pubKey)) {
        recId = i;
        break;
      }
    }
    if (recId == -1) {
      throw new RuntimeException("Could not construct a recoverable key. This should never happen.");
    }

    // 32 bytes for R + 32 bytes for S
    byte[] r = NumericUtils.toBytesPadded(components[0], 32);
    byte[] s = NumericUtils.toBytesPadded(components[1], 32);

    // v is not included in ledger sign
    byte[] sig = new byte[64];
    System.arraycopy(r, 0, sig, 0, 32);
    System.arraycopy(s, 0, sig, 32, 32);
    return sig;
  }

  public boolean verify(byte[] signature, byte[] data) {
    if (ArrayUtils.isEmpty(signature) || signature.length < 64) {
      throw new LedgerException("need signature size 64 but "
          + (ArrayUtils.isEmpty(signature) ? 0 : signature.length));
    }
    if (ArrayUtils.isEmpty(data) || data.length != 32) {
      throw new LedgerException("need hash size 32 but " + (ArrayUtils.isEmpty(data) ? 0 : data.length));
    }

    byte[] publicKey = getPublicKey();
    byte[] pubX = Arrays.copyOfRange(publicKey, 0, 32);
    byte[] pubY = Arrays.copyOfRange(publicKey, 32, 64);
    ECPoint point = this.CURVE.getCurve().createPoint(new BigInteger(1, pubX), new BigInteger(1, pubY));
    byte[] sigR = Arrays.copyOfRange(signature, 0, 32);
    byte[] sigS = Arrays.copyOfRange(signature, 32, 64);

    ECDSASigner signer = new ECDSASigner(new HMacDSAKCalculator(new SHA256Digest()));

    ECPublicKeyParameters pubKeyParameters = new ECPublicKeyParameters(point, this.CURVE);
    signer.init(false, pubKeyParameters);
    return signer.verifySignature(data, new BigInteger(1, sigR), new BigInteger(1, sigS));
  }

  private static BigInteger recoverPublicKey(int recId, BigInteger sigR, BigInteger sigS, byte[] message) {
    BigInteger n = CURVE.getN();
    BigInteger i = BigInteger.valueOf((long)recId / 2);
    BigInteger x = sigR.add(i.multiply(n));
    //   1.2. Convert the integer x to an octet string X of length mlen using the conversion
    //        routine specified in Section 2.3.7, where mlen = ⌈(log2 p)/8⌉ or mlen = ⌈m/8⌉.
    //   1.3. Convert the octet string (16 set binary digits)||X to an elliptic curve point r
    //        using the conversion routine specified in Section 2.3.4. If this conversion
    //        routine outputs "invalid", then do another iteration of Step 1.
    //
    // More concisely, what these points mean is to use X as a compressed public key.
    BigInteger prime = SecP256K1Curve.q;
    if (x.compareTo(prime) >= 0) {
      // Cannot have point co-ordinates larger than this as everything takes place modulo Q.
      return null;
    }
    // Compressed keys require you to know an extra bit of data about the y-coord as there are
    // two possibilities. So it's encoded in the recId.
    ECPoint r = decompressKey(x, (recId & 1) == 1);
    //   1.4. If nR != point at infinity, then do another iteration of Step 1 (callers
    //        responsibility).
    if (!r.multiply(n).isInfinity()) {
      return null;
    }
    //   1.5. Compute e from M using Steps 2 and 3 of ECDSA signature verification.
    BigInteger e = new BigInteger(1, message);
    //   1.6. For k from 1 to 2 do the following.   (loop is outside this function via
    //        iterating recId)
    //   1.6.1. Compute a candidate public key as:
    //               Q = mi(r) * (sR - eG)
    //
    // Where mi(x) is the modular multiplicative inverse. We transform this into the following:
    //               Q = (mi(r) * s ** r) + (mi(r) * -e ** G)
    // Where -e is the modular additive inverse of e, that is z such that z + e = 0 (mod n).
    // In the above equation ** is point multiplication and + is point addition (the EC group
    // operator).
    //
    // We can find the additive inverse by subtracting e from zero then taking the mod. For
    // example the additive inverse of 3 modulo 11 is 8 because 3 + 8 mod 11 = 0, and
    // -3 mod 11 = 8.
    BigInteger eInv = BigInteger.ZERO.subtract(e).mod(n);
    BigInteger rInv = sigR.modInverse(n);
    BigInteger srInv = rInv.multiply(sigS).mod(n);
    BigInteger eInvrInv = rInv.multiply(eInv).mod(n);
    ECPoint q = ECAlgorithms.sumOfTwoMultiplies(CURVE.getG(), eInvrInv, r, srInv);

    byte[] qBytes = q.getEncoded(false);
    // We remove the prefix
    return new BigInteger(1, Arrays.copyOfRange(qBytes, 1, qBytes.length));
  }

  /**
   * Decompress a compressed public key (x co-ord and low-bit of y-coord).
   */
  private static ECPoint decompressKey(BigInteger xBN, boolean yBit) {
    X9IntegerConverter x9 = new X9IntegerConverter();
    byte[] compEnc = x9.integerToBytes(xBN, 1 + x9.getByteLength(CURVE.getCurve()));
    compEnc[0] = (byte)(yBit ? 0x03 : 0x02);
    return CURVE.getCurve().decodePoint(compEnc);
  }

  /**
   * @return true if the S component is "low", that means it is below
   * <a href="https://github.com/bitcoin/bips/blob/master/bip-0062.mediawiki#Low_S_values_in_signatures">
   * BIP62</a>.
   */
  private boolean isCanonical(BigInteger s) {
    return s.compareTo(HALF_CURVE_ORDER) <= 0;
  }

  /**
   * Will automatically adjust the S component to be less than or equal to half the curve
   * order, if necessary. This is required because for every signature (r,s) the signature
   * (r, -s (mod N)) is a valid signature of the same message. However, we dislike the
   * ability to modify the bits of a Bitcoin transaction after it's been signed, as that
   * violates various assumed invariants. Thus in future only one of those forms will be
   * considered legal and the other will be banned.
   *
   * @return the signature in a canonicalised form.
   */

  private BigInteger toCanonicalised(BigInteger s) {
    if (!isCanonical(s)) {
      s = CURVE.getN().subtract(s);
    }
    return s;
  }

  public byte[] getPublicKeyWithHeader() {
    return Arrays.copyOfRange(publicKeyEncoded, 0, publicKeyEncoded.length);
  }

  @Override
  public Signature.SignatureType getSignatureType() {
    return Signature.SignatureType.SECP256K1;
  }

  public byte[] getPublicKey() {
    return Arrays.copyOfRange(publicKey, 0 ,publicKey.length);
  }

  public byte[] getPrivateKey() {
    return Arrays.copyOfRange(privateKey, 0, privateKey.length);
  }
}
