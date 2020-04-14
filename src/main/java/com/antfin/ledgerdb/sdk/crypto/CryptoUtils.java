package com.antfin.ledgerdb.sdk.crypto;

import com.antfin.ledgerdb.sdk.crypto.key.KeyTypes;
import com.antfin.ledgerdb.sdk.crypto.key.LedgerKeyPair;
import com.antfin.ledgerdb.sdk.crypto.key.Pkcs8KeyOperator;
import com.antfin.ledgerdb.sdk.crypto.key.LedgerKeyPair;
import com.antfin.ledgerdb.sdk.crypto.key.Pkcs8KeyOperator;
import com.antfin.ledgerdb.sdk.crypto.key.KeyTypes;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;

import java.math.BigInteger;

public class CryptoUtils {

  public static final Pkcs8KeyOperator DEFAULT_KEY_OPERATOR = new Pkcs8KeyOperator();

  // (TODO) need to consider thread safety
  public static LedgerKeyPair generateSM2KeyPairFromPrivateKey(byte[] privateKey) {
    byte[] privateKeyEncoded = new byte[privateKey.length + 2];
    System.arraycopy(KeyTypes.KEY_SM2_PKCS8.toBytes(), 0, privateKeyEncoded, 0, 2);
    System.arraycopy(privateKey, 0, privateKeyEncoded, 2, privateKey.length);
    return DEFAULT_KEY_OPERATOR.loadFromPrivkey(privateKeyEncoded);
  }

  public static LedgerKeyPair generateEcck1KeyPairFromPrivateKey(byte[] privateKey) {
    byte[] privateKeyEncoded = new byte[privateKey.length + 2];
    System.arraycopy(KeyTypes.KEY_ECCK1_PKCS8.toBytes(), 0, privateKeyEncoded, 0, 2);
    System.arraycopy(privateKey, 0, privateKeyEncoded, 2, privateKey.length);
    return DEFAULT_KEY_OPERATOR.loadFromPrivkey(privateKeyEncoded);
  }

  /**
   * Returns public key point from the given private key.
   *
   * @param curve
   * @param privKey
   * @return
   */
  public static ECPoint publicPointFromPrivate(ECDomainParameters curve, BigInteger privKey) {
    /*
     * TODO: FixedPointCombMultiplier currently doesn't support scalars longer than the group
     * order, but that could change in future versions.
     */
    if (privKey.bitLength() > curve.getN().bitLength()) {
      privKey = privKey.mod(curve.getN());
    }
    return new FixedPointCombMultiplier().multiply(curve.getG(), privKey);
  }

  /**
   * Returns public key point from the given private key.
   *
   * @param curveName
   * @param privKey
   * @return
   */
  public static ECPoint publicPointFromPrivate(String curveName, BigInteger privKey) {
    X9ECParameters CURVE_PARAMS = CustomNamedCurves.getByName(curveName);
    ECDomainParameters CURVE = new ECDomainParameters(CURVE_PARAMS.getCurve(),
        CURVE_PARAMS.getG(), CURVE_PARAMS.getN(), CURVE_PARAMS.getH());
    return publicPointFromPrivate(CURVE, privKey);
  }



}
