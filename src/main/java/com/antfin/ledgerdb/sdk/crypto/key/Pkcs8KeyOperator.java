package com.antfin.ledgerdb.sdk.crypto.key;


import java.io.*;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.ECPrivateKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import com.antfin.ledgerdb.sdk.LedgerException;
import com.antfin.ledgerdb.sdk.util.NumericUtils;

//import com.alipay.mychain.sdk.crypto.keypair.Keypair;
//import com.alipay.mychain.sdk.utils.NumericUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.PKCS8Generator;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.openssl.jcajce.JcaPKCS8Generator;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8EncryptorBuilder;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.pkcs.PKCSException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.io.pem.PemObject;

/**
 * @author evan
 * @version $Id: Pkcs8KeyOperator.java, v 0.1 2019-04-02 5:09 PM evan Exp $$
 */
public class Pkcs8KeyOperator {

  public Pkcs8KeyOperator() {
    //CryptoUtils.initMaxKeySize();
  }

  /**
   * Generate asymmetric key pair by algId
   *
   * @param type
   * @return Keypair key pair or null
   */
  public LedgerKeyPair generate(KeyTypes type) {
    LedgerKeyPair keypair = null;
    try {
      switch (type) {
        case KEY_ECCK1_PKCS8:
        case KEY_SM2_PKCS8:
        case KEY_ECCR1_PKCS8:
          keypair = generateEccKeyPair(type);
          break;
        case KEY_RSA2048_PKCS8:
          keypair = generateRSAKeyPair(2048);
          break;
        default:
          break;
      }
    } catch (InvalidAlgorithmParameterException e) {
      //throw new MychainSdkException(ErrorCode.OTHERS, ExceptionUtils.getStackTrace(e));
      throw new LedgerException(e);
    } catch (NoSuchAlgorithmException e) {
      //throw new MychainSdkException(ErrorCode.OTHERS, ExceptionUtils.getStackTrace(e));
      throw new LedgerException(e);
    } catch (NoSuchProviderException e) {
      //throw new MychainSdkException(ErrorCode.OTHERS, ExceptionUtils.getStackTrace(e));
      throw new LedgerException(e);
    }
    return keypair;
  }

  /**
   * @param privkeyEncoded
   * @return
   */
  public LedgerKeyPair loadFromPrivkey(byte[] privkeyEncoded) {
    if (ArrayUtils.isEmpty(privkeyEncoded)) {
      //throw new MychainSdkException(ErrorCode.SDK_INVALID_PARAMETER,
      //    "privkeyEncoded should not empty");
      throw new LedgerException("privkeyEncoded should not be empty");
    }
    if (privkeyEncoded.length <= 2) {
      //throw new MychainSdkException(ErrorCode.SDK_INVALID_PARAMETER,
      //    "privkeyEncoded is too short");
      throw new LedgerException("privkeyEncoded is too short");
    }

    KeyTypes type;
    byte[] prikey;
    if (privkeyEncoded.length == 32) {
      type = KeyTypes.KEY_ECCK1_PKCS8;
      prikey = privkeyEncoded;
    } else {
      type = KeyTypes.valueOf(privkeyEncoded);
      prikey = Arrays.copyOfRange(privkeyEncoded, 2, privkeyEncoded.length);
    }

    switch (type) {
      case KEY_ECCK1_PKCS8: {
        BigInteger d = new BigInteger(1, prikey);
        return eccPrivateKey2Keypair(type, "secp256k1", d);
      }
      case KEY_SM2_PKCS8: {
        BigInteger d = new BigInteger(1, prikey);
        return eccPrivateKey2Keypair(type, "sm2p256v1", d);
      }
      case KEY_RSA2048_PKCS8: {
        throw new UnsupportedOperationException("ras key is not supported yet");
      }
      default:
        break;
    }
    return null;
  }

  /**
   * Load asymmetric key pair from PKCS8 file.
   *
   * @param path     the passphrase of key file.
   * @param password the passphrase of key file. If password is null, the key pair is plaintext.
   * @return key pair or null
   */
  public LedgerKeyPair load(String path, String password) {
    InputStream inputStream = null;
    try {
      inputStream = new FileInputStream(path);
      return loadKey(inputStream, password);
    } catch (FileNotFoundException e) {
      //throw new MychainSdkException(ErrorCode.SDK_GET_FILE_INPUT_STREAM_FAILED, ExceptionUtils.getStackTrace(e));
      throw new LedgerException(e);
    } catch (Exception e) {
      //throw new MychainSdkException(ErrorCode.SDK_INVALID_PRIVATE_KEY, ExceptionUtils.getStackTrace(e));
      throw new LedgerException(e);
    } finally {
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (IOException e) {
          //throw new MychainSdkException(ErrorCode.OTHERS, ExceptionUtils.getStackTrace(e));
          throw new LedgerException(e);
        }
      }
    }
  }

  /**
   * Load asymmetric key pair from keyBytes.
   *
   * @param keyBytes the passphrase of key file.
   * @param password the passphrase of key file. If password is null, the key pair is plaintext.
   * @return IKeyPair key pair or null
   */
  public LedgerKeyPair load(byte[] keyBytes, String password) {
    InputStream inputStream = null;
    try {
      inputStream = new ByteArrayInputStream(keyBytes);
      return loadKey(inputStream, password);
    } catch (OperatorCreationException e) {
      //throw new MychainSdkException(ErrorCode.OTHERS, ExceptionUtils.getStackTrace(e));
      throw new LedgerException(e);
    } catch (IOException e) {
      //throw new MychainSdkException(ErrorCode.OTHERS, ExceptionUtils.getStackTrace(e));
      throw new LedgerException(e);
    } catch (PKCSException e) {
      //throw new MychainSdkException(ErrorCode.OTHERS, ExceptionUtils.getStackTrace(e));
      throw new LedgerException(e);
    } catch (InvalidKeyException e) {
      //throw new MychainSdkException(ErrorCode.OTHERS, ExceptionUtils.getStackTrace(e));
      throw new LedgerException(e);
    } finally {
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (IOException e) {
          //throw new MychainSdkException(ErrorCode.OTHERS, ExceptionUtils.getStackTrace(e));
          throw new LedgerException(e);
        }
      }
    }
  }

  /**
   * Load public key from PKCS8 file.
   *
   * @param path the file path of key.
   * @return IKeyPair key pair or null, only public key.
   */
  public LedgerKeyPair loadPubkey(String path) {
    InputStream inputStream = null;
    try {
      inputStream = new FileInputStream(path);
      return loadPubkey(inputStream);
    } catch (FileNotFoundException e) {
      //throw new MychainSdkException(ErrorCode.SDK_GET_FILE_INPUT_STREAM_FAILED, e.getMessage());
      throw new LedgerException(e);
    } catch (Exception e) {
      //throw new MychainSdkException(ErrorCode.SDK_INVALID_PUBLIC_KEY, e.getMessage());
      throw new LedgerException(e);
    } finally {
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (IOException e) {
          //throw new MychainSdkException(ErrorCode.OTHERS, ExceptionUtils.getStackTrace(e));
          throw new LedgerException(e);
        }
      }
    }
  }

  /**
   * Load public key from keyBytes. the keyBytes is PKCS8 format.
   *
   * @param pubkeyBytes
   * @return IKeyPair key pair or null, only public key.
   */
  public LedgerKeyPair loadPubkey(byte[] pubkeyBytes) {
    InputStream inputStream = null;
    try {
      inputStream = new ByteArrayInputStream(pubkeyBytes);
      return loadPubkey(inputStream);
    } catch (Exception e) {
      //throw new MychainSdkException(ErrorCode.SDK_INVALID_PUBLIC_KEY, e.getMessage());
      throw new LedgerException(e);
    } finally {
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (IOException e) {
          //throw new MychainSdkException(ErrorCode.OTHERS, ExceptionUtils.getStackTrace(e));
          throw new LedgerException(e);
        }
      }
    }
  }

  /**
   * @param pubkeyBytes encoded public key.
   * @return
   */
  public LedgerKeyPair loadFromPubkey(byte[] pubkeyBytes) {
    return new LedgerKeyPair(pubkeyBytes);
  }

  /**
   * Save asymmetric key pair to PKCS8 file.
   *
   * @param key      key pair
   * @param path     the file path
   * @param password the passphrase of key.If password is null, the key pair will by saved by none password.
   * @return boolean
   */
  public boolean save(LedgerKeyPair key, String path, String password) {
    try {
      switch (key.getType()) {
        case KEY_ECCK1_PKCS8:
        case KEY_SM2_PKCS8: {
          PrivateKey privateKey = getEcPrivateKey(key);
          if (privateKey == null) {
            break;
          }
          return saveFile(path, privateKey,
              password == null ? null : password.toCharArray());
        }
        case KEY_RSA2048_PKCS8: {
          PrivateKey privateKey = getRSAPrivateKey(key);
          if (privateKey == null) {
            break;
          }
          return saveFile(path, privateKey,
              password == null ? null : password.toCharArray());
        }
        default:
          break;
      }
    } catch (InvalidKeySpecException e) {
      //throw new MychainSdkException(ErrorCode.OTHERS, ExceptionUtils.getStackTrace(e));
      throw new LedgerException(e);
    } catch (NoSuchProviderException e) {
      //throw new MychainSdkException(ErrorCode.OTHERS, ExceptionUtils.getStackTrace(e));
      throw new LedgerException(e);
    } catch (NoSuchAlgorithmException e) {
      //throw new MychainSdkException(ErrorCode.OTHERS, ExceptionUtils.getStackTrace(e));
      throw new LedgerException(e);
    } catch (OperatorCreationException e) {
      //throw new MychainSdkException(ErrorCode.OTHERS, ExceptionUtils.getStackTrace(e));
      throw new LedgerException(e);
    } catch (IOException e) {
      //throw new MychainSdkException(ErrorCode.OTHERS, ExceptionUtils.getStackTrace(e));
      throw new LedgerException(e);
    }
    return false;
  }

  /***
   * TODO: add other alg
   * loadKey from inputStream
   * @param inputStream
   * @param password
   * @return Keypair
   * @throws IOException
   * @throws OperatorCreationException
   * @throws PKCSException
   */
  private LedgerKeyPair loadKey(InputStream inputStream, String password) throws IOException,
      OperatorCreationException,
      PKCSException,
      InvalidKeyException {
    PrivateKey privateKey = getPrivateKeyFromPKCS8(inputStream, password);
    if (privateKey instanceof BCECPrivateKey) {
      BCECPrivateKey bcecPrivateKey = (BCECPrivateKey)privateKey;
      String curveName = ((ECNamedCurveSpec)bcecPrivateKey.getParams()).getName();
      BigInteger d = bcecPrivateKey.getD();
      switch (curveName) {
        case "secp256k1":
          return eccPrivateKey2Keypair(KeyTypes.KEY_ECCK1_PKCS8, curveName, d);
        case "sm2p256v1":
          return eccPrivateKey2Keypair(KeyTypes.KEY_SM2_PKCS8, curveName, d);
        default:
          break;
      }
    } else if (privateKey instanceof RSAPrivateKey) {
      //RSAPrivateCrtKeyImpl rsaPrivateKey = (RSAPrivateCrtKeyImpl)privateKey;
      //return rsaPrivateKey2Keypair(rsaPrivateKey);
      throw new UnsupportedEncodingException("ras key is not supported yet");
    }
    return null;
  }

  /**
   * TODO: add other alg
   * loadPubkey from inputStream
   *
   * @param inputStream
   * @return Keypair
   */
  private LedgerKeyPair loadPubkey(InputStream inputStream) throws IOException {
    PublicKey publicKey = getPublicKeyFromPKCS8(inputStream);
    if (publicKey instanceof BCECPublicKey) {
      BCECPublicKey bcecPublicKey = (BCECPublicKey)publicKey;
      String curveName = ((ECNamedCurveSpec)bcecPublicKey.getParams()).getName();
      byte[] pubkeyId = bcecPublicKey.getQ().getEncoded(false);
      switch (curveName) {
        case "secp256k1": {
          byte[] typeBytes = KeyTypes.KEY_ECCK1_PKCS8.toBytes();
          byte[] pubkeyEncoded = new byte[2 + pubkeyId.length];
          System.arraycopy(typeBytes, 0, pubkeyEncoded, 0, 2);
          System.arraycopy(pubkeyId, 0, pubkeyEncoded, 2, pubkeyId.length);
          return new LedgerKeyPair(pubkeyEncoded);
        }
        case "sm2p256v1": {
          byte[] typeBytes = KeyTypes.KEY_SM2_PKCS8.toBytes();
          byte[] pubkeyEncoded = new byte[2 + pubkeyId.length];
          System.arraycopy(typeBytes, 0, pubkeyEncoded, 0, 2);
          System.arraycopy(pubkeyId, 0, pubkeyEncoded, 2, pubkeyId.length);
          return new LedgerKeyPair(pubkeyEncoded);
        }
        default:
          break;
      }
    } else if (publicKey instanceof RSAPublicKey) {
      RSAPublicKey rsaPublicKey = (RSAPublicKey)publicKey;
      byte[] pubkeyId = rsaPublicKey.getEncoded();

      byte[] typeBytes = KeyTypes.KEY_RSA2048_PKCS8.toBytes();
      byte[] pubkeyEncoded = new byte[2 + pubkeyId.length];
      System.arraycopy(typeBytes, 0, pubkeyEncoded, 0, 2);
      System.arraycopy(pubkeyId, 0, pubkeyEncoded, 2, pubkeyId.length);
      return new LedgerKeyPair(pubkeyEncoded);
    }
    return null;
  }

  private LedgerKeyPair generateEccKeyPair(KeyTypes type) throws InvalidAlgorithmParameterException,
      NoSuchAlgorithmException,
      NoSuchProviderException {
    switch (type) {
      case KEY_ECCK1_PKCS8: {
        KeyPair securityKeyPair = generateEccKeyPair("ECDSA", "secp256k1");
        BigInteger d = ((BCECPrivateKey)securityKeyPair.getPrivate()).getD();
        return eccPrivateKey2Keypair(type, "secp256k1", d);
      }
      case KEY_SM2_PKCS8: {
        KeyPair securityKeyPair = generateEccKeyPair("ECDSA", "sm2p256v1");
        BigInteger d = ((BCECPrivateKey)securityKeyPair.getPrivate()).getD();
        return eccPrivateKey2Keypair(type, "sm2p256v1", d);
      }
      case KEY_ECCR1_PKCS8:{
        KeyPair securityKeyPair=generateEccKeyPair("ECDSA","secp256r1");
        BigInteger d=((BCECPrivateKey)securityKeyPair.getPrivate()).getD();
        return eccPrivateKey2Keypair(type,"secp256r1",d);
      }
      default:
        break;
    }
    return null;
  }

  private KeyPair generateEccKeyPair(String algorithm, String curveName)
      throws NoSuchProviderException,
      NoSuchAlgorithmException,
      InvalidAlgorithmParameterException {
    Security.addProvider(new BouncyCastleProvider());
    ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec(curveName);
    KeyPairGenerator generator = KeyPairGenerator.getInstance(algorithm,
        BouncyCastleProvider.PROVIDER_NAME);
    generator.initialize(ecSpec, new SecureRandom());
    return generator.generateKeyPair();
  }

  private LedgerKeyPair generateRSAKeyPair(int keysize) throws NoSuchAlgorithmException {
    KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
    keyPairGen.initialize(keysize, new SecureRandom());

    KeyPair securityKeyPair = keyPairGen.generateKeyPair();

    byte[] pubkeyId = securityKeyPair.getPublic().getEncoded();
    byte[] privkeyId = securityKeyPair.getPrivate().getEncoded();

    byte[] typeBytes = KeyTypes.KEY_RSA2048_PKCS8.toBytes(); //may be 4096
    byte[] pubkeyEncoded = new byte[2 + pubkeyId.length];
    System.arraycopy(typeBytes, 0, pubkeyEncoded, 0, 2);
    System.arraycopy(pubkeyId, 0, pubkeyEncoded, 2, pubkeyId.length);

    byte[] privkeyEncoded = new byte[2 + privkeyId.length];
    System.arraycopy(typeBytes, 0, privkeyEncoded, 0, 2);
    System.arraycopy(privkeyId, 0, privkeyEncoded, 2, privkeyId.length);
    return new LedgerKeyPair(pubkeyEncoded, privkeyEncoded);
  }

  /**
   * Get private key from pkcs 8.
   *
   * @param privkeyInputStream the privkey input stream
   * @param password           the password
   * @return the private key from pkcs 8
   */
  private PrivateKey getPrivateKeyFromPKCS8(InputStream privkeyInputStream, String password)
      throws PKCSException,
      IOException,
      OperatorCreationException {
    Security.addProvider(new BouncyCastleProvider());

    PEMParser parser = new PEMParser(new InputStreamReader(privkeyInputStream));
    PKCS8EncryptedPrivateKeyInfo pair = (PKCS8EncryptedPrivateKeyInfo)parser.readObject();
    JceOpenSSLPKCS8DecryptorProviderBuilder jce = new JceOpenSSLPKCS8DecryptorProviderBuilder();
    jce.setProvider("BC");
    InputDecryptorProvider decProv = jce
        .build(password == null ? null : password.toCharArray());
    PrivateKeyInfo info = pair.decryptPrivateKeyInfo(decProv);
    JcaPEMKeyConverter pemKeyConverter = new JcaPEMKeyConverter();
    return pemKeyConverter.getPrivateKey(info);
  }

  private PublicKey getPublicKeyFromPKCS8(InputStream inputStream) throws IOException {
    Security.addProvider(new BouncyCastleProvider());
    PEMParser parser = new PEMParser(new InputStreamReader(inputStream));
    JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
    Object obj = parser.readObject();
    parser.close();
    return converter.getPublicKey((SubjectPublicKeyInfo)obj);
  }

  private PrivateKey getEcPrivateKey(LedgerKeyPair keypair) throws NoSuchProviderException,
      NoSuchAlgorithmException,
      InvalidKeySpecException {
    String algorithm;
    String curveName;
    switch (keypair.getType()) {
      case KEY_ECCK1_PKCS8: {
        algorithm = "ECDSA";
        curveName = "secp256k1";
        break;
      }
      case KEY_SM2_PKCS8: {
        algorithm = "ECDSA";
        curveName = "sm2p256v1";
        break;
      }
      default:
        return null;
    }

    Security.addProvider(new BouncyCastleProvider());
    ECNamedCurveParameterSpec parameterSpec = ECNamedCurveTable.getParameterSpec(curveName);
    ECNamedCurveSpec spec = new ECNamedCurveSpec(curveName, parameterSpec.getCurve(),
        parameterSpec.getG(), parameterSpec.getN(), parameterSpec.getH(),
        parameterSpec.getSeed());
    ECPrivateKeySpec keySpec = new ECPrivateKeySpec(new BigInteger(1, keypair.getPrivkeyId()),
        spec);
    KeyFactory keyFactory = KeyFactory.getInstance(algorithm,
        BouncyCastleProvider.PROVIDER_NAME);
    return keyFactory.generatePrivate(keySpec);
  }

  private PrivateKey getRSAPrivateKey(LedgerKeyPair keypair) throws NoSuchAlgorithmException,
      InvalidKeySpecException {
    PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keypair.getPrivkeyId());
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
    return privateK;
  }

  /**
   * Gen key pair.
   *
   * @param filePath   the file path
   * @param privateKey the private key
   * @param password   the password
   * @throws Exception the exception
   */
  private boolean saveFile(String filePath, PrivateKey privateKey, char[] password)
      throws IOException,
      OperatorCreationException {
    FileOutputStream fos2;
    Security.addProvider(new BouncyCastleProvider());
    JceOpenSSLPKCS8EncryptorBuilder encryptorBuilder = new JceOpenSSLPKCS8EncryptorBuilder(
        PKCS8Generator.PBE_SHA1_3DES);
    encryptorBuilder.setProvider(BouncyCastleProvider.PROVIDER_NAME);
    encryptorBuilder.setPasssword(password);
    OutputEncryptor encryptor = encryptorBuilder.build();

    JcaPKCS8Generator gen2 = new JcaPKCS8Generator(privateKey, encryptor);
    PemObject obj2 = gen2.generate();
    StringWriter sw2 = new StringWriter();
    try (JcaPEMWriter pw = new JcaPEMWriter(sw2)) {
      pw.writeObject(obj2);
    }
    String pkcs8Key2 = sw2.toString();

    fos2 = new FileOutputStream(filePath);
    fos2.write(pkcs8Key2.getBytes());
    fos2.flush();
    fos2.close();

    return true;
  }

  /**
   * @param KeyTypes
   * @param curveName
   * @param d           ecc or sm2 privatekey
   * @return
   */
  private LedgerKeyPair eccPrivateKey2Keypair(KeyTypes KeyTypes, String curveName, BigInteger d) {
    byte[] typeBytes = KeyTypes.toBytes();
    byte[] privkeyId = NumericUtils.toBytesPadded(d, 32);
    byte[] privkeyEncoded = new byte[34];
    System.arraycopy(typeBytes, 0, privkeyEncoded, 0, 2);
    System.arraycopy(privkeyId, 0, privkeyEncoded, 2, privkeyId.length);
    byte[] pubKeyId = publicPointFromPrivate(curveName, d).getEncoded(false);
    byte[] pubkeyEncoded = new byte[2 + pubKeyId.length];
    System.arraycopy(typeBytes, 0, pubkeyEncoded, 0, 2);
    System.arraycopy(pubKeyId, 0, pubkeyEncoded, 2, pubKeyId.length);
    return new LedgerKeyPair(pubkeyEncoded, privkeyEncoded);
  }

  public static ECPoint publicPointFromPrivate(String curveName, BigInteger privKey) {
    X9ECParameters CURVE_PARAMS = CustomNamedCurves.getByName(curveName);
    ECDomainParameters CURVE = new ECDomainParameters(CURVE_PARAMS.getCurve(),
        CURVE_PARAMS.getG(), CURVE_PARAMS.getN(), CURVE_PARAMS.getH());
    return publicPointFromPrivate(CURVE, privKey);
  }

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

}