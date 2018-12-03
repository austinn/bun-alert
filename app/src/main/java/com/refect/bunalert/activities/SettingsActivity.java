package com.refect.bunalert.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.refect.bunalert.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.tv_current_zip) TextView tvCurrentZip;
    @BindView(R.id.btn_clear_zips) Button btnClearZips;

    private ArrayList<String> zips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getAllTopics();
    }

    @OnClick(R.id.btn_test)
    public void test() {
        startActivity(new Intent(this, TestActivity.class));
    }

    @OnClick(R.id.btn_clear_zips)
    public void clear() {
        if (null != zips) {
            for (String zip : zips) {
                FirebaseMessaging.getInstance().unsubscribeFromTopic(zip);
            }
            tvCurrentZip.setText("");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void getAllTopics() {
        zips = new ArrayList<>();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://iid.googleapis.com/iid/info/" + FirebaseInstanceId.getInstance().getToken() + "?details=true";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            JSONObject topics = json.getJSONObject("rel").getJSONObject("topics");
                            Iterator<String> iter = topics.keys();
                            btnClearZips.setEnabled(true);
                            while (iter.hasNext()) {
                                String key = iter.next();
                                zips.add(key);
                                tvCurrentZip.append(key + "\n");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("SettingsActivity", "Error: " + error);
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "key=" + getString(R.string.fcm_key));
                params.put("Content-Type", "application/json");
                return params;
            }
        };
        queue.add(stringRequest);
    }
}
