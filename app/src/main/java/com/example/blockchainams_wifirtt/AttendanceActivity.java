package com.example.blockchainams_wifirtt;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.rtt.RangingRequest;
import android.net.wifi.rtt.RangingResult;
import android.net.wifi.rtt.RangingResultCallback;
import android.net.wifi.rtt.WifiRttManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;
import com.lemmingapex.trilateration.NonLinearLeastSquaresSolver;
import com.lemmingapex.trilateration.TrilaterationFunction;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.util.EntityUtils;

public class AttendanceActivity extends AppCompatActivity {
    private final String base = BaseUrl.VALUE;
    private final String baseUrl = base + "5000/";
    private final String ganacheUrl = base + "8545/";
    final String MESSAGE_FAILED = "Invalid";
    final String TAG = "AttendanceActivity";
    private Web3j web3j;
    String selectedCourse;
    int selectedCourseId;
    double [] currentCoordinate = new double[]{0,0};

    private boolean mLocationPermissionApproved = false;

    private WifiManager mWifiManager;
    private WifiRttManager mWifiRttManager;
    private WifiScanReceiver mWifiScanReceiver;

    List<ScanResult> mAccessPointsSupporting80211mc;

    private String mMAC;
    private int mNumberOfSuccessfulRangeRequests;
    private int mNumberOfRangeRequests;
    private final int mMillisecondsDelayBeforeNewRangingRequest = 100;
    private final int mSampleSize = 10;

    final Handler mRangeRequestDelayHandler = new Handler();
    private RttRangingResultCallback mRttRangingResultCallback;
    double [] accessPointDistances;
    private HashMap<Integer,Integer> mapCourseId;
    HashMap<String, Integer> accessPointDistancesMap;
    int numRttAPs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance_activity);

        mapCourseId = new HashMap<>();
        ArrayList<Integer> courseIdList = new ArrayList<>();
        ArrayList<String> courseNameList = new ArrayList<>();
        for (List list: StudentIdentity.getCourseInfo()) {
            int courseId = (int)(double)list.get(0);
            String courseName = list.get(1).toString();
            courseIdList.add(courseId);
            courseNameList.add(courseName);
        }

        for (int i = 0; i< courseIdList.size(); i++) {
            mapCourseId.put(i, courseIdList.get(i));
        }

        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item,
                courseNameList);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new SpinnerActivity());

        mAccessPointsSupporting80211mc = new ArrayList<>();
        accessPointDistancesMap = new HashMap<>();

        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        mWifiRttManager = (WifiRttManager) getSystemService(Context.WIFI_RTT_RANGING_SERVICE);
        mWifiScanReceiver = new WifiScanReceiver();
        mRttRangingResultCallback = new RttRangingResultCallback();

        mNumberOfSuccessfulRangeRequests = 0;
        mRttRangingResultCallback = new RttRangingResultCallback();

        if (mLocationPermissionApproved) {
            Log.d(TAG, "Retrieving access points ...");
            mWifiManager.startScan();

        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,}, 1);
        }
        Log.e("", numRttAPs + "");
        if (numRttAPs >= 3)
            startRangingRequest();
        else showToast("Not enough APs");
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume()");
        super.onResume();

        mLocationPermissionApproved =
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;

        registerReceiver(
                mWifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause()");
        super.onPause();
        unregisterReceiver(mWifiScanReceiver);
    }

    public void onClickFindDistancesToAccessPoints(View view) {
        if (mLocationPermissionApproved) {
            Log.d(TAG, "Retrieving access points ...");
            mWifiManager.startScan();

        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,}, 1);
        }
    }

    public void doRangingRequest(View view) {
        startRangingRequest();
    }

    public void getCentroidTrilateration(double[][] positions, double[] distances) {
        try {

            NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(
                    new TrilaterationFunction(positions, distances),
                    new LevenbergMarquardtOptimizer());

            LeastSquaresOptimizer.Optimum optimum = solver.solve();

            // the answer
            currentCoordinate = optimum.getPoint().toArray();

            //print answer
            for (double i: currentCoordinate) {
                Log.e("test", "centroid:" + Double.toString(i));
            }
            // error and geometry information; may throw SingularMatrixException depending the threshold argument provided
            RealVector standardDeviation = optimum.getSigma(0);
            RealMatrix covarianceMatrix = optimum.getCovariances(0);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("test", e.getMessage());
        }
    }

    private void startRangingRequest() {
        // Permission for fine location should already be granted via MainActivity (you can't get
        // to this class unless you already have permission. If they get to this class, then disable
        // fine location permission, we kick them back to main activity.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            finish();
        }

        mNumberOfRangeRequests++;

        RangingRequest rangingRequest =
                new RangingRequest.Builder()
                        .addAccessPoints(mAccessPointsSupporting80211mc).build();

        mWifiRttManager.startRanging(
                rangingRequest, getApplication()
                        .getMainExecutor(), mRttRangingResultCallback);
    }

    private class RttRangingResultCallback extends RangingResultCallback {

        private void queueNextRangingRequest() {
            if (mNumberOfSuccessfulRangeRequests <
                    mSampleSize * mAccessPointsSupporting80211mc.size()
            ) {
                mRangeRequestDelayHandler.postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                startRangingRequest();
                            }
                        },
                        mMillisecondsDelayBeforeNewRangingRequest);
            } else {

                // calculate mean distances
                double [][] accessPointLocations = new double[numRttAPs][2];
                double [] accessPointDistances = new double[numRttAPs];
                if (numRttAPs == 3) {
                    for (Map.Entry<String, Integer> entry: accessPointDistancesMap.entrySet()) {
                        if (AccessPointLocation.AP1.getMac().equals(entry.getKey())) {
                            accessPointLocations[0] = AccessPointLocation.AP1.getCoordinate();
                            accessPointDistances[0] = (double) accessPointDistancesMap.get(entry.getKey());
                        } else if (AccessPointLocation.AP2.getMac().equals(entry.getKey())) {
                            accessPointLocations[1] = AccessPointLocation.AP2.getCoordinate();
                            accessPointDistances[1] = (double) accessPointDistancesMap.get(entry.getKey());
                        } else if (AccessPointLocation.AP3.getMac().equals(entry.getKey())) {
                            accessPointLocations[2] = AccessPointLocation.AP3.getCoordinate();
                            accessPointDistances[2] = (double) accessPointDistancesMap.get(entry.getKey());
                        }
                    }
                } else {
                    for (Map.Entry<String, Integer> entry: accessPointDistancesMap.entrySet()) {
                        if (AccessPointLocation.AP1.getMac().equals(entry.getKey())) {
                            accessPointLocations[0] = AccessPointLocation.AP1.getCoordinate();
                            accessPointDistances[0] = (double) accessPointDistancesMap.get(entry.getKey());
                        } else if (AccessPointLocation.AP2.getMac().equals(entry.getKey())) {
                            accessPointLocations[1] = AccessPointLocation.AP2.getCoordinate();
                            accessPointDistances[1] = (double) accessPointDistancesMap.get(entry.getKey());
                        } else if (AccessPointLocation.AP3.getMac().equals(entry.getKey())) {
                            accessPointLocations[2] = AccessPointLocation.AP3.getCoordinate();
                            accessPointDistances[2] = (double) accessPointDistancesMap.get(entry.getKey());
                        } else if (AccessPointLocation.AP4.getMac().equals(entry.getKey())) {
                            accessPointLocations[3] = AccessPointLocation.AP4.getCoordinate();
                            accessPointDistances[3] = (double) accessPointDistancesMap.get(entry.getKey());
                        }
                    }
                }
                for (int i = 0; i<numRttAPs; i++) {
                    accessPointDistances[i] /= (double) mNumberOfSuccessfulRangeRequests / mAccessPointsSupporting80211mc.size();
                    Log.e("checkResAfterMean", "" + accessPointDistances[i] + " " + mNumberOfSuccessfulRangeRequests / mAccessPointsSupporting80211mc.size());
                }
                getCentroidTrilateration(accessPointLocations, accessPointDistances);
                mNumberOfSuccessfulRangeRequests = 0;
                accessPointDistancesMap.replaceAll((k, v) -> 0);
            }
        }

        @Override
        public void onRangingFailure(int code) {
            Log.d(TAG, "onRangingFailure() code: " + code);
            queueNextRangingRequest();
        }

        @Override
        public void onRangingResults(@NonNull List<RangingResult> list) {
            Log.w(TAG, "onRangingResults(): " + list);

            // Because we are only requesting RangingResult for one access point (not multiple
            // access points), this will only ever be one. (Use loops when requesting RangingResults
            // for multiple access points.)
            if (!list.isEmpty()) {
                for (int i = 0; i < list.size(); i++) {
                    RangingResult rangingResult = list.get(i);
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                        Log.e(TAG, "responderLocation: "+ rangingResult.getUnverifiedResponderLocation());
//                    }
                    if (rangingResult.getStatus() == RangingResult.STATUS_SUCCESS) {

                        mNumberOfSuccessfulRangeRequests++;
//                        Log.e("result", "num of successful requests: "+mNumberOfSuccessfulRangeRequests);
//                        Log.e("", rangingResult.getMacAddress().toString());
                        accessPointDistancesMap.put(rangingResult.getMacAddress().toString(), accessPointDistancesMap.get(rangingResult.getMacAddress().toString()) + rangingResult.getDistanceMm());

                    } else if (rangingResult.getStatus()
                            == RangingResult.STATUS_RESPONDER_DOES_NOT_SUPPORT_IEEE80211MC) {
                        Log.d(TAG, "RangingResult failed (AP doesn't support IEEE80211 MC.");

                    } else {
                        Log.d(TAG, "RangingResult failed.");
                    }
                }
            }
            queueNextRangingRequest();
        }
    }

    private class WifiScanReceiver extends BroadcastReceiver {

        private List<ScanResult> find80211mcSupportedAccessPoints(
                @NonNull List<ScanResult> originalList) {
            List<ScanResult> newList = new ArrayList<>();

            for (ScanResult scanResult : originalList) {

                if (scanResult.is80211mcResponder()) {
                    newList.add(scanResult);
                }

                if (newList.size() >= RangingRequest.getMaxPeers()) {
                    break;
                }
            }
            return newList;
        }

        // This is checked via mLocationPermissionApproved boolean
        @SuppressLint("MissingPermission")
        public void onReceive(Context context, Intent intent) {

            List<ScanResult> scanResults = mWifiManager.getScanResults();

            if (scanResults != null) {

                if (mLocationPermissionApproved) {
                    mAccessPointsSupporting80211mc = find80211mcSupportedAccessPoints(scanResults);

                    numRttAPs = mAccessPointsSupporting80211mc.size();
                    Log.d(TAG,
                            scanResults.size()
                                    + " APs discovered, "
                                    + numRttAPs
                                    + " RTT capable.");

                    showToast(numRttAPs + " RTT capable APs found");
                    for (ScanResult x: mAccessPointsSupporting80211mc) {
                        Log.e("", x.BSSID);
                        accessPointDistancesMap.put(x.BSSID, 0);
                    }

                } else {
                    // TODO (jewalker): Add Snackbar regarding permissions
                    Log.d(TAG, "Permissions not allowed.");
                }
            }
        }
    }

    public HttpResponse checkDate(Registration registration) throws Exception {
        Gson gson = new Gson();
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(baseUrl + "check_date");
        StringEntity postingString = new StringEntity(gson.toJson(registration));
        post.setEntity(postingString);
        post.setHeader("Content-type", "application/json");
        return httpClient.execute(post);
    }

    public HttpResponse checkFlag(Registration registration) throws Exception {
        Gson gson = new Gson();
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(baseUrl + "check_flag");
        StringEntity postingString = new StringEntity(gson.toJson(registration));
        post.setEntity(postingString);
        post.setHeader("Content-type", "application/json");
        return httpClient.execute(post);
    }

    class Coordinates {
        int courseId;
        double[] bounds;
        Coordinates (int courseId, double[] bounds) {
            this.bounds = bounds;
            this.courseId =courseId;
        }
    }

    public HttpResponse checkCoordinate(Coordinates coordinates) throws Exception {
        Gson gson = new Gson();
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(baseUrl + "check_coordinate");
        StringEntity postingString = new StringEntity(gson.toJson(coordinates));
        post.setEntity(postingString);
        post.setHeader("Content-type", "application/json");
        return httpClient.execute(post);
    }

    public void doAttendance(View view) throws Exception {
        Thread thread = new Thread(() -> {
            try {
                // check if within schedule range
                if (checkDate(new Registration(StudentIdentity.getStudentId(), selectedCourseId)).getStatusLine().getStatusCode() != 200) {
                    Log.e("doAttendance", "Not within class schedule");
                    showToast("Not within class schedule");
                    return;
                }
                // check if within class coordinates
//                if (checkCoordinate(new Coordinates(selectedCourseId, currentCoordinate)).getStatusLine().getStatusCode() != 200) {
//                    Log.e("doAttendance", "Not within class coordinates");
//                    showToast("Not within class coordinates");
//                    return;
//                }
                //check if already do att today
//                if (checkFlag(new Registration(StudentIdentity.getStudentId(), selectedCourseId)).getStatusLine().getStatusCode() != 200){
//                    Log.e("doAttendance", "Already did attendance");
//                    showToast("Already did attendance");
//                    return;
//                }
                // initiate txEntity
                String contractAddress = getContractAddress();
                web3j = Web3j.build(new HttpService(ganacheUrl));
                String pk = StudentIdentity.getPrivateKey();
                TransactionEntity.initEntity(web3j, contractAddress, pk);

                // define function
                Function function = new Function(
                        Attendance.FUNC_DOATTENDANCE,
                        Arrays.asList(
                                new Uint256(
                                        BigInteger.valueOf(StudentIdentity.getStudentId())),
                                new Uint256(
                                        BigInteger.valueOf(selectedCourseId) )),
                        Collections.emptyList());

                // encode and send tx
                String encodeFunction = FunctionEncoder.encode(function);
                TransactionReceipt txReceipt = TransactionEntity.sendTransaction(encodeFunction);
                String reason = TransactionEntity.getRevertReason(txReceipt, encodeFunction);
                if (!reason.equals("N/A")) Log.e(TAG, reason);
                else {
                    showToast("Attendance recorded on blockchain server");
                    logEvents(Attendance.ATTENDANCERECORDEVENT_EVENT, txReceipt.getTransactionHash());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }


    private String getContractAddress() {
        Util runnable = new Util();
        Thread thread = new Thread(runnable);
        try {
            thread.start();
            thread.join();
//            Log.e(TAG, runnable.getOutput());
            return runnable.getOutput();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return MESSAGE_FAILED;
    }

    private class Util implements Runnable {
        private volatile String output;
        public String getOutput() {return output;}

        @Override
        public void run() {
            try {
                HttpClient httpClient = HttpClientBuilder.create().build();
                HttpGet get = new HttpGet(baseUrl + "contract_address");
                get.setHeader("Content-type", "application/json");
                HttpResponse response = httpClient.execute(get);

                output = EntityUtils.toString(response.getEntity());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class SpinnerActivity extends Activity implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {
            // An item was selected. You can retrieve the selected item using
            // parent.getItemAtPosition(pos)
            selectedCourse = parent.getItemAtPosition(pos).toString();
            selectedCourseId = mapCourseId.get(parent.getSelectedItemPosition());
            Log.e(TAG, "User selected " + selectedCourse + " with id: " + selectedCourseId);
        }

        public void onNothingSelected(AdapterView<?> parent) {
            // Another interface callback
        }
    }

    public HttpResponse sendLog(Record record) throws Exception {
        Gson gson = new Gson();
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(baseUrl + "record");
        StringEntity postingString = new StringEntity(gson.toJson(record));
        post.setEntity(postingString);
        post.setHeader("Content-type", "application/json");
        return httpClient.execute(post);
    }

    class Registration {
        public int studentId;
        public int courseId;
        public Registration(int studentId, int courseId) {
            this.courseId = courseId;
            this.studentId = studentId;
        }
    }

    public HttpResponse updateAttendanceFlag(Registration registration) throws Exception {
        Gson gson = new Gson();
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(baseUrl + "update_flag");
        StringEntity postingString = new StringEntity(gson.toJson(registration));
        post.setEntity(postingString);
        post.setHeader("Content-type", "application/json");
        return httpClient.execute(post);
    }

    public void logEvents(Event event, String transactionHash) {
        try {
            //encode
            final String EVENT_HASH = EventEncoder.encode(event);
            //Filter
            EthFilter filter = new EthFilter(DefaultBlockParameterName.LATEST, DefaultBlockParameterName.LATEST, TransactionEntity.getContractAddress());
            //Pull all events
            web3j.ethLogFlowable(filter).subscribe(
                    log -> {
                        final List<String> topics = log.getTopics();
                        if (topics == null || topics.size() == 0 || !topics.get(0).equals(EVENT_HASH)) {
                            Log.e("eventCreation", "You have no'in OMEGALUL");
                            return;
                        }

                        List<Type> args = FunctionReturnDecoder.decode(log.getData(), event.getParameters());

                        Log.e("stdCreationEvent", "studentId: " + args.get(0).getValue().toString());
                        Log.e("stdCreationEvent", "attVal: " + args.get(1).getValue().toString());
                        Log.e("stdCreationEvent", "blockTime: " + args.get(2).getValue().toString());
                        Log.e("stdCreationEvent", "courseId: " + args.get(3).getValue().toString());

                        // send log to server
                        Record record = new Record(
                                ((BigInteger) args.get(0).getValue()).intValue(),
                                ((BigInteger) args.get(1).getValue()).intValue(),
                                ((BigInteger) args.get(2).getValue()).intValue(),
                                ((BigInteger) args.get(3).getValue()).intValue(),
                                transactionHash
                        );
                        HttpResponse response = sendLog(record);
                        if (response.getStatusLine().getStatusCode() != 200) {
                            Log.e("", "Failed to log on server");
                            showToast("Failed to log on server");
                        } else {
                            showToast("Successfully logged record on server");
                        }

                        //TODO: update attendance flag
                        Registration registration = new Registration(
                                ((BigInteger) args.get(0).getValue()).intValue(),
                                ((BigInteger) args.get(3).getValue()).intValue()
                        );
                        HttpResponse response1 = updateAttendanceFlag(registration);
                        if (response1.getStatusLine().getStatusCode() != 200) {
                            Log.e("", "Failed to update attendance flag");
                            showToast("Failed to update attendance flag");
                        } else {
                            showToast("Successfully updated attendance flag");
                        }
                    }, throwable -> {
                        Log.e("eventCreation", throwable.getMessage());
                        throwable.printStackTrace();
                    }).dispose();
        } catch (Exception e) {
            showToast(e.getMessage());
            e.printStackTrace();
        }
    }

    public void showToast(final String toast) {
        runOnUiThread(() -> Toast.makeText(this, toast, Toast.LENGTH_SHORT).show());
    }
}
