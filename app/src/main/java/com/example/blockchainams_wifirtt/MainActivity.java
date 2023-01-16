package com.example.blockchainams_wifirtt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.util.ArrayList;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.util.EntityUtils;
import jnr.ffi.annotations.In;

public class MainActivity extends AppCompatActivity {

    private String baseUrl = BaseUrl.VALUE + "5000/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void requestLogin(View view) throws Exception {
        Thread thread = new Thread(() -> {
            try {
                HttpResponse response = login();

                if (response.getStatusLine().getStatusCode() != 200) {
                    showToast("Failed to login");
                    return;
                } else {
                    showToast("Succeed");
                }

                JSONArray body = new JSONArray(EntityUtils.toString(response.getEntity()));
                ArrayList<Integer> id_list = new Gson().fromJson(body.toString(), new TypeToken<ArrayList<Integer>>(){}.getType());
                int id = id_list.get(0);

                StudentIdentity.initEntity(id);
                for (String course: StudentIdentity.getCourseList()) {
                    Log.e("", course);
                }
                Intent i = new Intent(MainActivity.this, AttendanceActivity.class);
                startActivity(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    private HttpResponse login() throws Exception {
        EditText _username = findViewById(R.id.username);
        EditText _password = findViewById(R.id.password);
        String username = _username.getText().toString();
        String password = _password.getText().toString();
        LoginEntity loginEntity = new LoginEntity(username, password);
        Gson gson = new Gson();
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(baseUrl + "login");
        StringEntity postingString = new StringEntity(gson.toJson(loginEntity));
        post.setEntity(postingString);
        post.setHeader("Content-type", "application/json");
        HttpResponse response = httpClient.execute(post);
        return response;
    }

    public void showToast(final String toast) {
        runOnUiThread(() -> Toast.makeText(this, toast, Toast.LENGTH_SHORT).show());
    }
}