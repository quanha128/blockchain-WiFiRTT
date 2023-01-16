//package com.example.blockchainams_wifirtt;
//
//import android.Manifest;
//import android.annotation.SuppressLint;
//import android.app.Service;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.pm.PackageManager;
//import android.net.wifi.ScanResult;
//import android.net.wifi.WifiManager;
//import android.net.wifi.rtt.RangingRequest;
//import android.net.wifi.rtt.RangingResult;
//import android.net.wifi.rtt.RangingResultCallback;
//import android.net.wifi.rtt.WifiRttManager;
//import android.os.Bundle;
//import android.os.Handler;
//import android.util.Log;
//import android.view.View;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.core.app.ActivityCompat;
//
//import com.lemmingapex.trilateration.NonLinearLeastSquaresSolver;
//import com.lemmingapex.trilateration.TrilaterationFunction;
//
//import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
//import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
//import org.apache.commons.math3.linear.RealMatrix;
//import org.apache.commons.math3.linear.RealVector;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class LocationRangingService {
//    private static final String TAG = "LocationRangingService";
//
//    private boolean mLocationPermissionApproved = false;
//
//    private WifiManager mWifiManager;
//    private WifiRttManager mWifiRttManager;
//    private WifiScanReceiver mWifiScanReceiver;
//
//    List<ScanResult> mAccessPointsSupporting80211mc;
//
//    private String mMAC;
//    private int mNumberOfSuccessfulRangeRequests;
//    private int mNumberOfRangeRequests;
//    private int mMillisecondsDelayBeforeNewRangingRequest = 100;
//    private int mSampleSize = 10;
//
//    final Handler mRangeRequestDelayHandler = new Handler();
//    private RttRangingResultCallback mRttRangingResultCallback;
//    double [] accessPointDistances;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        mAccessPointsSupporting80211mc = new ArrayList<>();
//
//        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
//        mWifiRttManager = (WifiRttManager) getSystemService(Context.WIFI_RTT_RANGING_SERVICE);
//        mWifiScanReceiver = new WifiScanReceiver();
//        mRttRangingResultCallback = new RttRangingResultCallback();
//
//        mNumberOfSuccessfulRangeRequests = 0;
//        mRttRangingResultCallback = new RttRangingResultCallback();
//    }
//
//    @Override
//    protected void onResume() {
//        Log.d(TAG, "onResume()");
//        super.onResume();
//
//        mLocationPermissionApproved =
//                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                        == PackageManager.PERMISSION_GRANTED;
//
//        registerReceiver(
//                mWifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
//    }
//
//    @Override
//    protected void onPause() {
//        Log.d(TAG, "onPause()");
//        super.onPause();
//        unregisterReceiver(mWifiScanReceiver);
//    }
//
//    public void onClickFindDistancesToAccessPoints(View view) {
//        if (mLocationPermissionApproved) {
//            Log.d(TAG, "Retrieving access points ...");
//            mWifiManager.startScan();
//
//        } else {
//            ActivityCompat.requestPermissions(this, new String[]{
//                    Manifest.permission.ACCESS_FINE_LOCATION,
//                    Manifest.permission.ACCESS_COARSE_LOCATION,}, 1);
//        }
//    }
//
//    public void showToast(final String toast) {
//        runOnUiThread(() -> Toast.makeText(this, toast, Toast.LENGTH_SHORT).show());
//    }
//
//    public void test(View view) {
//        startRangingRequest();
//    }
//
////    public double[] meanNMatrix(double[][] matrices) {
////        double [] ans = new double[] {0, 0, 0};
////        for (int i = 0; i < matrices.length; i++) {
////            ans[0] += matrices[i][0];
////            ans[1] += matrices[i][1];
////            ans[2] += matrices[i][2];
////        }
////
////        ans[0] /= matrices.length;
////        ans[1] /= matrices.length;
////        ans[2] /= matrices.length;
////
////        return ans;
////    }
//
//    public void getCentroidTrilateration(double[][] positions, double[] distances) {
//        try {
////            double[][] positions = new double[][]{{5.0, -6.0, 4.0}, {13.0, -15.0, -4.0}, {21.0, -3.0, 4.0}};
////            double[] distances = new double[]{8.06, 13.97, 23.32};
//
//            NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(
//                    new TrilaterationFunction(positions, distances),
//                    new LevenbergMarquardtOptimizer());
//
//            LeastSquaresOptimizer.Optimum optimum = solver.solve();
//
//            // the answer
//            double[] centroid = optimum.getPoint().toArray();
//
//            //print answer
//            for (double i: centroid) {
//                Log.e("test", Double.toString(i));
//            }
//            // error and geometry information; may throw SingularMatrixException depending the threshold argument provided
//            RealVector standardDeviation = optimum.getSigma(0);
//            RealMatrix covarianceMatrix = optimum.getCovariances(0);
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e("test", e.getMessage());
//        }
//    }
//
//    private void startRangingRequest() {
//        // Permission for fine location should already be granted via MainActivity (you can't get
//        // to this class unless you already have permission. If they get to this class, then disable
//        // fine location permission, we kick them back to main activity.
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            finish();
//        }
//
//        mNumberOfRangeRequests++;
//
//        RangingRequest rangingRequest =
//                new RangingRequest.Builder().addAccessPoints(mAccessPointsSupporting80211mc).build();
//
//        mWifiRttManager.startRanging(
//                rangingRequest, getApplication().getMainExecutor(), mRttRangingResultCallback);
//    }
//
//    private class RttRangingResultCallback extends RangingResultCallback {
//
//        private void queueNextRangingRequest() {
//            if (mNumberOfSuccessfulRangeRequests <
//                    mSampleSize * mAccessPointsSupporting80211mc.size()
//            ) {
//                mRangeRequestDelayHandler.postDelayed(
//                        new Runnable() {
//                            @Override
//                            public void run() {
//                                startRangingRequest();
//                            }
//                        },
//                        mMillisecondsDelayBeforeNewRangingRequest);
//            } else {
//                mNumberOfSuccessfulRangeRequests = 0;
//
//                // calculate mean distances
//                accessPointDistances[0] /= mSampleSize;
//                accessPointDistances[1] /= mSampleSize;
//                accessPointDistances[2] /= mSampleSize;
//                Log.e(TAG, accessPointDistances[0] + "\n" + accessPointDistances[1] + "\n" + accessPointDistances[2]);
////                double[][] accessPointLocations = { {0,0,0}, {0,0,0}, {0,0,0} };
////                getCentroidTrilateration(accessPointLocations, accessPointDistances);
//            }
//        }
//
//        @Override
//        public void onRangingFailure(int code) {
//            Log.d(TAG, "onRangingFailure() code: " + code);
//            queueNextRangingRequest();
//        }
//
//        @Override
//        public void onRangingResults(@NonNull List<RangingResult> list) {
//            Log.d(TAG, "onRangingResults(): " + list);
//
//            // Because we are only requesting RangingResult for one access point (not multiple
//            // access points), this will only ever be one. (Use loops when requesting RangingResults
//            // for multiple access points.)
//            if (!list.isEmpty()) {
//                for (int i = 0; i < list.size(); i++) {
//                    RangingResult rangingResult = list.get(i);
////                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
////                        Log.e(TAG, "responderLocation: "+ rangingResult.getUnverifiedResponderLocation());
////                    }
//                    if (rangingResult.getStatus() == RangingResult.STATUS_SUCCESS) {
//
//                        mNumberOfSuccessfulRangeRequests++;
//                        accessPointDistances[i] += rangingResult.getDistanceMm();
//
//                    } else if (rangingResult.getStatus()
//                            == RangingResult.STATUS_RESPONDER_DOES_NOT_SUPPORT_IEEE80211MC) {
//                        Log.d(TAG, "RangingResult failed (AP doesn't support IEEE80211 MC.");
//
//                    } else {
//                        Log.d(TAG, "RangingResult failed.");
//                    }
//                }
//            }
//            queueNextRangingRequest();
//        }
//    }
//
//    private class WifiScanReceiver extends BroadcastReceiver {
//
//        private List<ScanResult> find80211mcSupportedAccessPoints(
//                @NonNull List<ScanResult> originalList) {
//            List<ScanResult> newList = new ArrayList<>();
//
//            for (ScanResult scanResult : originalList) {
//
//                if (scanResult.is80211mcResponder()) {
//                    newList.add(scanResult);
//                }
//
//                if (newList.size() >= RangingRequest.getMaxPeers()) {
//                    break;
//                }
//            }
//            return newList;
//        }
//
//        // This is checked via mLocationPermissionApproved boolean
//        @SuppressLint("MissingPermission")
//        public void onReceive(Context context, Intent intent) {
//
//            List<ScanResult> scanResults = mWifiManager.getScanResults();
//
//            if (scanResults != null) {
//
//                if (mLocationPermissionApproved) {
//                    mAccessPointsSupporting80211mc = find80211mcSupportedAccessPoints(scanResults);
//
//                    int numRttAPs = mAccessPointsSupporting80211mc.size();
//                    Log.d(TAG,
//                            scanResults.size()
//                                    + " APs discovered, "
//                                    + numRttAPs
//                                    + " RTT capable.");
//
//                    showToast(numRttAPs + " RTT capable APs found");
////                    accessPointLocations = new double[numRttAPs][3];
//                    accessPointDistances = new double[mSampleSize];
//
//                } else {
//                    // TODO (jewalker): Add Snackbar regarding permissions
//                    Log.d(TAG, "Permissions not allowed.");
//                }
//            }
//        }
//    }
//}
