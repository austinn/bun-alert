package com.refect.bunalert.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.refect.bunalert.R;
import com.refect.bunalert.utils.Constants;
import com.refect.bunalert.utils.PrefUtils;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);

        if (checkLocationServicesEnabled()) {
            if (PrefUtils.getSetting(Constants.PREFS_FIRST_TIME, Constants.EMPTY_STRING, this).equals(Constants.EMPTY_STRING)) {
                PrefUtils.storeSetting(Constants.PREFS_FIRST_TIME, "false", this);
            } else {
                go();
            }
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setCancelable(false);
            dialog.setMessage("Location services is turned off. Please enable location to continue using this app");
            dialog.setPositiveButton("Enable Location", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);

                    finish();
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Toast.makeText(getApplicationContext(), "You must enable location services to use this app", Toast.LENGTH_LONG).show();
                    finish();
                }
            });
            dialog.show();
        }
    }

    private boolean checkLocationServicesEnabled() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {

        }

        return gps_enabled;
    }

    @OnClick(R.id.btn_show_me_the_buns)
    public void go() {
        startActivity(new Intent(this, MapsActivity.class));
        finish();
    }
}
