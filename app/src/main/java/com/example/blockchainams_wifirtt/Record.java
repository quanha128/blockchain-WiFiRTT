package com.example.blockchainams_wifirtt;

public class Record {
    int id;
    int attVal;
    int courseId;
    int blockTime;
    String txHash;

    public Record(int id, int attVal, int blockTime, int courseId, String txHash) {
        this.id = id;
        this.attVal = attVal;
        this.courseId = courseId;
        this.blockTime = blockTime;
        this.txHash = txHash;
    }
}
