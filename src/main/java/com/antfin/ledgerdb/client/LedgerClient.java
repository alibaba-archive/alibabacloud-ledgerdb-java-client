package com.antfin.ledgerdb.client;

import com.antfin.ledgerdb.client.common.AppendTransactionResponse;
import com.antfin.ledgerdb.client.common.GetTransactionResponse;
import com.antfin.ledgerdb.client.common.KVGetResponse;
import com.antfin.ledgerdb.client.common.ListTransactionsResponse;
import com.antfin.ledgerdb.client.exception.LedgerException;
import com.antfin.ledgerdb.client.proto.Tx;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

public class LedgerClient implements Closeable {

    private String ledgerUri = null;

    private LedgerClientOptions clientOptions = null;

    private LedgerDBLightClient ledgerDBLightClient = null;

    public LedgerClient(String ledgerUri, LedgerClientOptions options) {
        URI uri = URI.create(ledgerUri);
        if (!uri.getScheme().equals("ledger")) {
            throw new LedgerException();
        }
        String host = uri.getHost();
        int port = uri.getPort() > 0 ? uri.getPort() : 80;

        this.ledgerUri = ledgerUri;
        this.clientOptions = options;
        ledgerDBLightClient = new LedgerDBLightClient(host, port);
    }

    public void kvSet(byte[] key, byte[] value) {
        OperationControl operationControl = new OperationControl();
        ledgerDBLightClient.kvSet(ledgerUri, key, value, operationControl);
    }

    public byte[] kvGet(byte[] key) {
        OperationControl operationControl = new OperationControl();
        KVGetResponse response = ledgerDBLightClient.kvGet(ledgerUri, key, operationControl);
        return response.getPayload().getValue().toByteArray();
    }

    public void kvDel(byte[] key) {
        OperationControl operationControl = new OperationControl();
        ledgerDBLightClient.kvDel(ledgerUri, key, operationControl);
    }

    public long appendTxJournal(byte[] data, String[] clues) {
        OperationControl operationControl = new OperationControl();
        operationControl.setClues(Arrays.asList(clues));
        AppendTransactionResponse response = ledgerDBLightClient.appendTransaction(ledgerUri, data, operationControl);
        return response.getTotalSequence();
    }

    public long appendTx(byte[] data, String[] clues) {
        OperationControl operationControl = new OperationControl();
        operationControl.setClues(Arrays.asList(clues));
        AppendTransactionResponse response = ledgerDBLightClient.appendTransaction(ledgerUri, data, operationControl);
        return response.getTotalSequence();
    }

    public Tx getTx(long sequence) {
        OperationControl operationControl = new OperationControl();
        GetTransactionResponse response = ledgerDBLightClient.getTransaction(ledgerUri, sequence, operationControl);
        return response.getPayload().getTx();
    }

    public List<Tx> listTxs(String clue, long startSequence, int limit) {
        OperationControl operationControl = new OperationControl();
        ListTransactionsResponse response = ledgerDBLightClient.listTransactions(ledgerUri, clue, startSequence, limit, operationControl);
        return response.getTxList();
    }

    public List<Tx> listTxs(String clue, long startSequence, int limit, boolean reverse) {
        OperationControl operationControl = new OperationControl();
        ListTransactionsResponse response =
            ledgerDBLightClient.listTransactions(ledgerUri, clue, startSequence, limit, reverse, operationControl);
        return response.getTxList();
    }

    @Override
    public void close() throws IOException {
        ledgerDBLightClient.close();
    }
}
