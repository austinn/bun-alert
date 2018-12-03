package com.refect.bunalert.activities;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.refect.bunalert.R;
import com.refect.bunalert.adapters.BunAdapter;
import com.refect.bunalert.interfaces.OnRecyclerViewItemClickListener;
import com.refect.bunalert.models.Bun;
import com.refect.bunalert.utils.Constants;
import com.refect.bunalert.utils.GPSHelper;
import com.refect.bunalert.utils.Utils;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BunListActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.rv_buns) RecyclerView rvBuns;
    @BindView(R.id.card_no_buns) CardView cardNoBuns;
    private BunAdapter bunAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Bun Alert!");

        GPSHelper gpsHelper = new GPSHelper(this);
        Location location = gpsHelper.getLocation();

        bunAdapter = new BunAdapter(this, gpsHelper.getLatitude(), gpsHelper.getLongitude());
        rvBuns.setLayoutManager(new LinearLayoutManager(this));
        rvBuns.setItemAnimator(new DefaultItemAnimator());
        rvBuns.setAdapter(bunAdapter);

        bunAdapter.setOnItemClickListener(new OnRecyclerViewItemClickListener<Bun>() {
            @Override public void onItemClick(View view, Bun model) {
//                Intent intent = new Intent(BunListActivity.this, ShareActivity.class);
//                intent.putExtra("name", model.getName());
//                intent.putExtra("imageUrl", (null != model.getImageUrl()) ? model.getImageUrl() : Constants.EMPTY_STRING);
//                startActivity(intent);
            }
        });

        String zipCode = gpsHelper.getZipCode(location.getLatitude(), location.getLongitude());
        if (null != zipCode) {
            if (!zipCode.equals(Constants.EMPTY_STRING)) {
                getBuns(zipCode);
            }
        }
    }

    private void getBuns(String zipCode) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("buns").orderByChild("zipCode").equalTo(zipCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot dataSnapshot) {
                Long now = new Date().getTime();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Bun bun = snapshot.getValue(Bun.class);

                    PrettyTime p = new PrettyTime();
                    if ((p.format(Utils.addMinutesToDate(-5, new Date(bun.getExpiration())))).contains("minute")) {
                        cardNoBuns.setVisibility(View.GONE);
                        bunAdapter.add(bun);
                    }
                }
            }

            @Override public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_map_view:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
