package com.antfin.ledgerdb.sdk.crypto;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.ECGenParameterSpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.antfin.ledgerdb.sdk.crypto.ECCK1KeyPair;

import static com.antfin.ledgerdb.sdk.crypto.SecureRandomUtils.secureRandom;

public class Keys {
  static final int PRIVATE_KEY_SIZE = 32;
  static final int PUBLIC_KEY_SIZE = 64;

  static {
    if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
      Security.addProvider(new BouncyCastleProvider());
    }
  }

  private Keys() {};

  /**
   * Create a keypair using SECP-256k1 curve.
   *
   * <p>Private keypairs are encoded using PKCS8
   *
   * <p>Private keys are encoded using X.509
   */
  static KeyPair createSecp256k1KeyPair()
      throws NoSuchProviderException, NoSuchAlgorithmException,
      InvalidAlgorithmParameterException {
    return createSecp256k1KeyPair(secureRandom());
  }

  static KeyPair createSecp256k1KeyPair(SecureRandom random)
      throws NoSuchProviderException, NoSuchAlgorithmException,
      InvalidAlgorithmParameterException {

    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDSA", "BC");
    ECGenParameterSpec ecGenParameterSpec = new ECGenParameterSpec("secp256k1");
    if (random != null) {
      keyPairGenerator.initialize(ecGenParameterSpec, random);
    } else {
      keyPairGenerator.initialize(ecGenParameterSpec);
    }
    return keyPairGenerator.generateKeyPair();
  }

  public static ECCK1KeyPair createEcKeyPair()
      throws InvalidAlgorithmParameterException, NoSuchAlgorithmException,
      NoSuchProviderException {
    return createEcKeyPair(secureRandom());
  }

  public static ECCK1KeyPair createEcKeyPair(SecureRandom random)
      throws InvalidAlgorithmParameterException, NoSuchAlgorithmException,
      NoSuchProviderException {
    KeyPair keyPair = createSecp256k1KeyPair(random);
    return ECCK1KeyPair.create(keyPair);
   }



}