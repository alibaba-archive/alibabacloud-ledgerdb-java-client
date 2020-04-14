package com.antfin.ledgerdb.sdk.crypto;

import com.antfin.ledgerdb.sdk.crypto.key.LedgerKeyPair;
import com.antfin.ledgerdb.sdk.LedgerException;

import com.antfin.ledgerdb.sdk.crypto.key.LedgerKeyPair;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.gm.GMNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.params.*;
import org.bouncycastle.crypto.signers.SM2Signer;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Strings;

import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

public class SM2KeyPair extends KeyPair {

  private static final X9ECParameters CURVE_PARAMS = GMNamedCurves.getByName("sm2p256v1");
  private static final ECDomainParameters CURVE = new ECDomainParameters(
      CURVE_PARAMS.getCurve(),
      CURVE_PARAMS.getG(),
      CURVE_PARAMS.getN(),
      CURVE_PARAMS.getH());

  public SM2KeyPair(LedgerKeyPair keypair) {
    super(keypair);
  }

  public SM2KeyPair(byte[] publicKey) {
    super(publicKey);
  }

  //public byte[] sign(byte[] message) {
  //  byte[] sign = signer.sign(message);
  //  return Arrays.copyOfRange(sign, 6, sign.length);
  // }

  public byte[] sign(byte[] message) {
    BigInteger privateKeyD = new BigInteger(1, getPrivateKey());
    ECPrivateKeyParameters privateKeyParameters = new ECPrivateKeyParameters(privateKeyD,
        this.CURVE);

    SM2Signer sm2Signer = new SM2Signer();

    try {
      ParametersWithID parameters = new ParametersWithID(
          new ParametersWithRandom(privateKeyParameters, SecureRandom
              .getInstance("SHA1PRNG")), Strings.toByteArray("1234567812345678"));
      sm2Signer.init(true, parameters);
      sm2Signer.update(message, 0, message.length);
      byte[] bigIntegers = sm2Signer.generateSignature();
      ASN1Sequence asn1Encodables = ASN1Sequence.getInstance(ASN1Primitive.fromByteArray(bigIntegers));
      return bigIntegers;
    } catch (NoSuchAlgorithmException e) {
      //throw new MychainSdkException(ErrorCode.OTHERS, ExceptionUtils.getStackTrace(e));
      throw new LedgerException(e);
    } catch (CryptoException e) {
      //throw new MychainSdkException(ErrorCode.OTHERS, ExceptionUtils.getStackTrace(e));
      throw new LedgerException(e);
    } catch (IOException e) {
      throw new LedgerException(e);
    }
  }

  public boolean verify(byte[] sig, byte[] msg) {
    byte[] publicKey = getPublicKey();
    byte[] pubX = Arrays.copyOfRange(publicKey, 1, 33);
    byte[] pubY = Arrays.copyOfRange(publicKey, 33, 65);
    ECPoint point = this.CURVE.getCurve().createPoint(new BigInteger(1, pubX),
        new BigInteger(1, pubY));

    ECPublicKeyParameters publicKeyParameters = new ECPublicKeyParameters(point, this.CURVE);
    SM2Signer sm2Signer = new SM2Signer();

    ParametersWithID parametersWithID = new ParametersWithID(publicKeyParameters,
        Strings.toByteArray("1234567812345678"));
    sm2Signer.init(false, parametersWithID);
    sm2Signer.update(msg, 0, msg.length);

    return sm2Signer.verifySignature(sig);
  }


}
