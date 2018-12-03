package com.refect.bunalert.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.refect.bunalert.R;
import com.refect.bunalert.models.Bun;
import com.refect.bunalert.services.MyService;
import com.refect.bunalert.utils.Constants;
import com.refect.bunalert.utils.GPSHelper;
import com.refect.bunalert.utils.PrefUtils;
import com.refect.bunalert.utils.Utils;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Pointer;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

import static com.refect.bunalert.R.id.map;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.fab_options) FloatingActionButton fabOptions;

    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    public GPSHelper mGPSHelper;

    private DatabaseReference mDatabase;

    private String incomingKey;
    private ArrayList<String> zipQuery;
    private TourGuide mTourGuideHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Bun Alert!");

        if (null != getIntent()) {
            if (getIntent().hasExtra("key")) {
                incomingKey = getIntent().getStringExtra("key");
            }
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("com.refect.bunalert.newbun"));

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(map);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (PrefUtils.getSetting(Constants.PREFS_TOUR_GUDIE_NEW_BUN, Constants.EMPTY_STRING, this).equals(Constants.EMPTY_STRING)) {
            PrefUtils.storeSetting(Constants.PREFS_TOUR_GUDIE_NEW_BUN, "false", this);
            showTourGuide();
        }

        permissionCheck();
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String key = intent.getStringExtra("key");

            mDatabase.child("buns").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override public void onDataChange(DataSnapshot dataSnapshot) {
                    Bun bun = dataSnapshot.getValue(Bun.class);
                    LatLng sydney = new LatLng(bun.getLocation().get(0), bun.getLocation().get(1));
                    mMap.addMarker(new MarkerOptions()
                            .position(sydney)
                            .icon(BitmapDescriptorFactory.fromResource(Utils.getRandomBunnyIcon(getApplicationContext())))
                            .title(bun.getName())
                            .snippet(bun.getMessage()));

                    mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(15f));
                }

                @Override public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    };

    private void showTourGuide() {
        mTourGuideHandler = TourGuide.init(this).with(TourGuide.Technique.Click)
                .setPointer(new Pointer())
                .setToolTip(new ToolTip().setTitle("Welcome!").setDescription("Click here to report a new bun").setGravity(Gravity.TOP | Gravity.LEFT))
                .setOverlay(new Overlay())
                .playOn(fabOptions);
    }

    private void getBuns(String zipCode) {
        mDatabase.child("buns").orderByChild("zipCode").equalTo(zipCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot dataSnapshot) {
                boolean keyHasExpired = false;
                Long now = new Date().getTime();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Bun bun = snapshot.getValue(Bun.class);

                    if (bun.getExpiration() >= now) {
                        LatLng sydney = new LatLng(bun.getLocation().get(0), bun.getLocation().get(1));
                        mMap.addMarker(new MarkerOptions()
                                .position(sydney)
                                .icon(BitmapDescriptorFactory.fromResource(Utils.getRandomBunnyIcon(getApplicationContext())))
                                .title(bun.getName())
                                .snippet(bun.getMessage()));

                        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                        mMap.moveCamera(CameraUpdateFactory.zoomTo(15f));
                    } else {
                        //expired
                        if (snapshot.getKey().equals(incomingKey)) {
                            keyHasExpired = true;
                        }
                    }
                }

                if (null != incomingKey && keyHasExpired) {
                    Toast.makeText(getApplicationContext(), "Sorry, this bun is no longer in here :(", Toast.LENGTH_LONG).show();
                }
            }

            @Override public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);

        startService(new Intent(MapsActivity.this, MyService.class));

        Location location = mGPSHelper.getLocation();
        if (null == location) {
            Toast.makeText(getApplicationContext(), "Could not get your location. Please tap on the location button to try again", Toast.LENGTH_LONG).show();
            return;
        }

        mMap.addMarker(new MarkerOptions()
                .title("you")
                .position(new LatLng(location.getLatitude(), location.getLongitude())));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 14.0f));

        String zipCode = mGPSHelper.getZipCode(location.getLatitude(), location.getLongitude());
        if (null != zipCode) {
            if (!zipCode.equals(Constants.EMPTY_STRING)) {
                getBuns(zipCode);
            }
        }
    }

    private void permissionCheck() {
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck == PermissionChecker.PERMISSION_GRANTED) {
            mGPSHelper = new GPSHelper(this);
            mapFragment.getMapAsync(this);
        } else {
            requestPermission();
        }
    }

    public void requestPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                    requestPermissions(
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            Constants.MY_PERMISSIONS_ACCESS_FINE_LOCATION);
                } else {
                    requestPermissions(
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            Constants.MY_PERMISSIONS_ACCESS_FINE_LOCATION);
                }
            }
        }
    }

    @OnClick(R.id.fab_location)
    public void location() {
        Location location = mGPSHelper.getLocation();
        if (null == location) {
            Toast.makeText(getApplicationContext(), "Could not get your location. Do you have location services enabled?", Toast.LENGTH_LONG).show();
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 14.0f));
        }
    }

    @OnClick(R.id.fab_options)
    public void sendNotification() {
        if (null != mTourGuideHandler) {
            mTourGuideHandler.cleanUp();
        }

        Intent intent = new Intent(this, NewPostActivity.class);
        intent.putExtra("latitude", mGPSHelper.getLatitude());
        intent.putExtra("longitude", mGPSHelper.getLongitude());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_list_view:
                startActivity(new Intent(MapsActivity.this, BunListActivity.class));
                return true;
            case R.id.menu_settings:
                startActivity(new Intent(MapsActivity.this, SettingsActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constants.MY_PERMISSIONS_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mGPSHelper = new GPSHelper(this);
                    mapFragment.getMapAsync(this);
                } else {
                    Toast.makeText(getApplicationContext(), "You must enable location in order to use Bun Alert!", Toast.LENGTH_LONG).show();
                    finish();
                }
                return;
            }
        }
    }
}
