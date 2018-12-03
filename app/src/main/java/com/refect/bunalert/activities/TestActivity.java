package com.refect.bunalert.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.refect.bunalert.R;
import com.refect.bunalert.models.Bun;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TestActivity extends AppCompatActivity {

    @BindView(R.id.input_zip) EditText inputZip;
    @BindView(R.id.input_lat) EditText inputLat;
    @BindView(R.id.input_log) EditText inputLog;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);

        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    private void createDatabaseItem(String url) {
        final String key = mDatabase.push().getKey();

        ArrayList<Double> location = new ArrayList<>();
        location.add((double) Float.parseFloat(inputLat.getText().toString()));
        location.add((double) Float.parseFloat(inputLog.getText().toString()));

        Bun bun = new Bun("Test bun", "", location, inputZip.getText().toString(), null);

        mDatabase.child("buns").child(key).setValue(bun).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override public void onSuccess(Void aVoid) {
                sendPush(key, "Buns");
            }
        });
    }

    private void sendPush(String key, String message) {
        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject jsonObject = new JSONObject();
        JSONObject data = new JSONObject();
        JSONObject content = new JSONObject();

        try {
            content.put("key", key);
            content.put("message", message);
            data.put("message", content);

            jsonObject.put("to", "/topics/" + inputZip.getText().toString());
            jsonObject.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest req = new JsonObjectRequest(
                "http://fcm.googleapis.com/fcm/send",
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override public void onResponse(JSONObject response) {
                        //Toast.makeText(getApplicationContext(), response + "", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override public void onErrorResponse(VolleyError error) {
                        Log.e("SubmitBunInBackground (sendPush)", "error");
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "key=" + getApplicationContext().getString(R.string.fcm_key));
                params.put("Content-Type", "application/json");
                return params;
            }
        };

        queue.add(req);
    }

    @OnClick(R.id.btn_send)
    public void send() {
        createDatabaseItem("");
    }
}
