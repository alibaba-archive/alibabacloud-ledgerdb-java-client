package com.antfin.ledgerdb.sdk.crypto;

//import com.alipay.mychain.sdk.crypto.keyoperator.Pkcs8KeyOperator;
//import com.alipay.mychain.sdk.crypto.keypair.KeyTypeEnum;
//import com.alipay.mychain.sdk.crypto.keypair.Keypair;

import com.antfin.ledgerdb.sdk.crypto.key.KeyTypes;
import com.antfin.ledgerdb.sdk.crypto.key.LedgerKeyPair;
import com.antfin.ledgerdb.sdk.crypto.key.Pkcs8KeyOperator;
import com.antfin.ledgerdb.sdk.crypto.key.Pkcs8KeyOperator;
import com.antfin.ledgerdb.sdk.crypto.key.KeyTypes;
import com.antfin.ledgerdb.sdk.crypto.key.LedgerKeyPair;

public class KeyPairUtils {

  private static final Pkcs8KeyOperator keyOperator = new Pkcs8KeyOperator();


  public static KeyPair createKeyPair(LedgerKeyPair keypair) {
    if (keypair.getType() == KeyTypes.KEY_SM2_PKCS8) {
      return new SM2KeyPair(keypair);
    } else {
      return new ECCK1KeyPair(keypair);
    }
  }

  // (TODO) need to consider thread safety
  public static KeyPair createSM2KeyPair() {
    LedgerKeyPair keypair = keyOperator.generate(KeyTypes.KEY_SM2_PKCS8);
    return new SM2KeyPair(keypair);
  }


  // (TODO) need to consider thread safety
  public static KeyPair createECCK1KeyPair() {
    LedgerKeyPair keypair = keyOperator.generate(KeyTypes.KEY_ECCK1_PKCS8);
    return new ECCK1KeyPair(keypair);
  }
}
