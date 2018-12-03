package com.refect.bunalert.async;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.refect.bunalert.R;
import com.refect.bunalert.models.Bun;
import com.refect.bunalert.utils.GPSHelper;
import com.refect.bunalert.utils.ImageUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by austinn on 8/3/2017.
 */

public class SubmitBunInBackground extends AsyncTask<String, Void, Void> {

    private Context ctx;
    private String name;
    private String message;
    private double lat;
    private double log;

    private DatabaseReference mDatabase;
    private GPSHelper mGPSHelper;

    public SubmitBunInBackground(Context ctx, String name, String message, double lat, double log) {
        this.ctx = ctx;
        this.name = name;
        this.message = message;
        this.lat = lat;
        this.log = log;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        this.mGPSHelper = new GPSHelper(ctx);
    }

    @Override protected Void doInBackground(String... params) {
        uploadPhoto(params[0]);
        return null;
    }

    private void uploadPhoto(String path) {
        Log.d("NewPostActivity (uploadPhoto)", "uploadPhoto");
        Bitmap bitmap = null;

        try {
            bitmap = BitmapFactory.decodeFile(path);
        } catch (Exception e) {
            Log.e("NewPostActivity", "No bitmap selected: " + e.toString());
        }

        if (null != bitmap) {
            Bitmap resizedBitmap = ImageUtils.resizeBitmap(bitmap, 640);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            resizedBitmap.compress(Bitmap.CompressFormat.PNG, 99, baos);
            final byte[] data = baos.toByteArray();

            StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://bun-alert.appspot.com");
            StorageReference mountainsRef = storageRef.child(lat + "_" + log + "_" + System.currentTimeMillis() + ".png");

            UploadTask uploadTask = mountainsRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override public void onFailure(@NonNull Exception e) {
                    Log.d("SubmitBunInBackground (uploadPhoto)", "Image failed to upload: " + e.toString());
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d("SubmitBunInBackground (uploadPhoto)", taskSnapshot.getDownloadUrl().toString());
                    createDatabaseItem(taskSnapshot.getDownloadUrl().toString());
                }
            });
        } else {
            Log.e("NewPostActivity (uploadPhoto)", "Bitmap is null");
            createDatabaseItem(null);
        }
    }

    private void createDatabaseItem(String url) {
        final String key = mDatabase.push().getKey();

        ArrayList<Double> location = new ArrayList<>();
        location.add(lat);
        location.add(log);

        Bun bun = new Bun(name, message, location, mGPSHelper.getZipCode(lat, log), url);

        mDatabase.child("buns").child(key).setValue(bun).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override public void onSuccess(Void aVoid) {
                sendPush(key, (message.length() > 0) ? message : name);
            }
        });
    }

    private void sendPush(String key, String message) {
        RequestQueue queue = Volley.newRequestQueue(ctx);
        JSONObject jsonObject = new JSONObject();
        JSONObject data = new JSONObject();
        JSONObject content = new JSONObject();

        String zipCode = mGPSHelper.getZipCode(lat, log);
        Log.d("SubmitBunInBackground (sendPush)", zipCode + "");

        try {
            content.put("key", key);
            content.put("message", message);
            data.put("message", content);

            jsonObject.put("to", "/topics/" + zipCode);
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
                params.put("Authorization", "key=" + ctx.getString(R.string.fcm_key));
                params.put("Content-Type", "application/json");
                return params;
            }
        };

        queue.add(req);
    }
}
