package com.example.blockchainams_wifirtt;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.tx.response.PollingTransactionReceiptProcessor;
import org.web3j.tx.response.TransactionReceiptProcessor;
import org.web3j.utils.RevertReasonExtractor;

import java.math.BigInteger;

public class TransactionEntity {
    private static Web3j web3j;
    private static RawTransactionManager txManager;
    private static String contractAddress;
    private static String privateKey;

    public static void initEntity(Web3j _web3j, String _contractAddress, String _privateKey) {
        web3j = _web3j;
        contractAddress = _contractAddress;
        privateKey = _privateKey;

        Credentials credentials = Credentials.create(privateKey);
        TransactionEntity.txManager = new RawTransactionManager(TransactionEntity.web3j, credentials);
    }

    public static String getContractAddress() {return contractAddress;}

    public static TransactionReceipt sendTransaction(String encodedFunction) throws Exception {
        // send transaction
        String hash = txManager.sendTransaction(
                DefaultGasProvider.GAS_PRICE,
                DefaultGasProvider.GAS_LIMIT,
                contractAddress,
                encodedFunction,
                BigInteger.ZERO
        ).getTransactionHash();

        // wait for tx to be mined
        TransactionReceiptProcessor receiptProcessor = new PollingTransactionReceiptProcessor(
                web3j,
                TransactionManager.DEFAULT_POLLING_FREQUENCY,
                TransactionManager.DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH
        );
        return receiptProcessor.waitForTransactionReceipt(hash);
    }

    public static String getRevertReason(TransactionReceipt txReceipt, String encodeFunction) throws Exception {
        return RevertReasonExtractor.extractRevertReason(
                txReceipt, encodeFunction, web3j, true);
    }
}
