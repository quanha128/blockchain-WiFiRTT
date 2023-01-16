package com.example.blockchainams_wifirtt;

public enum AccessPointLocation {
    AP1 (new double[] {11400 + 2000 + 300, 5350-800},    "cc:f4:11:29:6f:47"),
    AP2 (new double[] {1100              , 5150}    ,    "30:fd:38:da:70:a4"),
    AP3 (new double[] {11400 + 2000 + 300, 10900 - 800}, "30:fd:38:da:86:af"),
    AP4 (new double[] {1100              , 10900 - 200}, "30:fd:38:da:86:db");

    private final double[] coordinate;
    private final String mac;
    AccessPointLocation(double[] coordinate, String mac) {
        this.coordinate = coordinate;
        this.mac = mac;
    };

    public double[] getCoordinate() {
        return this.coordinate;
    }

    public String getMac() {
        return this.mac;
    }
}
