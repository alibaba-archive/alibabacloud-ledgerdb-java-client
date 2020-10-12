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

package com.antfin.ledgerdb.client.crypto;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.ECGenParameterSpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import static com.antfin.ledgerdb.client.crypto.SecureRandomUtils.secureRandom;

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
