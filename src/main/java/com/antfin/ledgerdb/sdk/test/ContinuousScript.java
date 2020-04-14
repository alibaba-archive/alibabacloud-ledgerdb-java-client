package com.antfin.ledgerdb.sdk.test;

import com.antfin.ledgerdb.sdk.MyLedgerLightClient;
import com.antfin.ledgerdb.sdk.OperationControl;
import com.antfin.ledgerdb.sdk.common.CreateLedgerResponse;
import com.antfin.ledgerdb.sdk.proto.LedgerMeta;
import com.google.protobuf.ByteString;
import io.netty.handler.ssl.SslContext;
import org.bouncycastle.util.encoders.Hex;

import javax.net.ssl.SSLException;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ContinuousScript {

  /**
   * @param args first is the path of crt, second is the test mode, thrid is the data file for test
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
    MyLedgerLightClient client = getClient(args);
    if ("A".equals(args[1])) {
      appendTransactions(args, client);
    } else if ("AG".equals(args[1])) {
      appendThenGetTransactions(args, client);
    } else if ("AE".equals(args[1])) {
      appendThenVerifyTransactions(args, client);
    }
  }

  public static void appendTransactions(
      String[] args,
      MyLedgerLightClient client) {

  }

  public static void appendThenGetTransactions(
      String[] args,
      MyLedgerLightClient client) {

  }

  public static void appendThenVerifyTransactions(
      String[] args,
      MyLedgerLightClient client) {

  }

  public static MyLedgerLightClient getClient(String[] args) throws Exception {
    File clientCrtfile =
        new File(args[0]);
    SslContext sslContext = MyLedgerLightClient.buildSslContext(clientCrtfile);
    MyLedgerLightClient client =
        new MyLedgerLightClient("yize.inc.alipay.net", 10077, sslContext);
    return client;
  }
}
