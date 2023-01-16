package com.example.blockchainams_wifirtt;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.util.EntityUtils;

public class StudentIdentity {
    private static String baseUrl = BaseUrl.VALUE;
    private static int studentId;
    private static String privateKey;
    private static List<String> courseList;
    private static List<List<Object>> courseInfo;
//    private static HashMap<Integer, String> courseMap;
//    private static HashMap<Integer, String> courseSchedule;

    public static void initEntity(int id) throws Exception {
        studentId = id;
        courseList = getRegisteredCourses();
        privateKey = requestPk();
        courseInfo = requestCourseInfo();
    }

    public static String getSchedule(int id) throws Exception {
        for (List x: courseInfo) {
            if ((int)(double)x.get(0) == id) {
                return x.get(2).toString();
            }
        }
        return "FAILED";
    }

    public static List<List<Object>> getCourseInfo() { return courseInfo; }

//    public static HashMap<Integer, String> getCourseSchedule() { return courseSchedule; }

    public static int getStudentId() {
        return studentId;
    }

    public static String getPrivateKey() {return privateKey;}

    public static List<String> getCourseList() {
        return courseList;
    }

    public static List<String> getRegisteredCourses() throws Exception {
        // send request
        Gson gson = new Gson();
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(baseUrl + "5000/course_registration");
        StringEntity postingString = new StringEntity(gson.toJson(StudentIdentity.studentId));
        post.setEntity(postingString);
        post.setHeader("Content-type", "application/json");
        HttpResponse response = httpClient.execute(post);

        // convert response to list
        JSONArray list = new JSONArray(EntityUtils.toString(response.getEntity()));
        return new Gson().fromJson(list.toString(), new TypeToken<ArrayList<String>>(){}.getType());
    }

    public static List<List<Object>> requestCourseInfo() throws Exception {
        // send request
        Gson gson = new Gson();
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(baseUrl + "5000/course_info");
        StringEntity postingString = new StringEntity(gson.toJson(StudentIdentity.studentId));
        post.setEntity(postingString);
        post.setHeader("Content-type", "application/json");
        HttpResponse response = httpClient.execute(post);

        // convert response to list
        JSONArray list = new JSONArray(EntityUtils.toString(response.getEntity()));
        List<List<Object>> cc = new Gson().fromJson(list.toString(), new TypeToken<ArrayList<List<Object>>>(){}.getType());
        return cc;
    }

    public static String requestPk() throws Exception {
        // send request
        Gson gson = new Gson();
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(baseUrl + "5000/privatekey");
        StringEntity postingString = new StringEntity(gson.toJson(StudentIdentity.studentId));
        post.setEntity(postingString);
        post.setHeader("Content-type", "application/json");
        HttpResponse response = httpClient.execute(post);

        String cc = EntityUtils.toString(response.getEntity());
        return cc;
    }
}
