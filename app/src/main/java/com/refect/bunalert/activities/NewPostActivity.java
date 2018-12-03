package com.refect.bunalert.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.commonsware.cwac.cam2.CameraActivity;
import com.commonsware.cwac.cam2.Facing;
import com.commonsware.cwac.cam2.FlashMode;
import com.commonsware.cwac.cam2.ZoomStyle;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.refect.bunalert.R;
import com.refect.bunalert.adapters.ImagesAdapter;
import com.refect.bunalert.async.SubmitBunInBackground;
import com.refect.bunalert.dialog.GalleryDialog;
import com.refect.bunalert.interfaces.OnRecyclerViewItemClickListener;
import com.refect.bunalert.utils.Constants;
import com.refect.bunalert.utils.Utils;
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class NewPostActivity extends AppCompatActivity {

    private static final FlashMode[] FLASH_MODES = {
            FlashMode.ALWAYS,
            FlashMode.AUTO
    };

    private static final int REQUEST_PORTRAIT_RFC = 1337;
    private static final int REQUEST_PORTRAIT_FFC = REQUEST_PORTRAIT_RFC + 1;

    private File testRoot;
    private File selectedFile;

    @BindView(R.id.input_layout_bun_name) TextInputLayout inputLayoutBunName;
    @BindView(R.id.input_bun_name) EditText inputBunName;
    @BindView(R.id.input_bun_text) EditText inputBunText;
    @BindView(R.id.iv_picture) ImageView ivPicture;
    @BindView(R.id.iv_clear_picture) ImageView ivClearPicture;

    @BindView(R.id.rv_recent_pictures) RecyclerView rvRecentPictures;
    private ImagesAdapter imageAdapter;

    private DatabaseReference mDatabase;

    private double lat;
    private double log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        ButterKnife.bind(this);

        if (null != getIntent()) {
            lat = getIntent().getDoubleExtra("latitude", 0);
            log = getIntent().getDoubleExtra("longitude", 0);
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();
        testRoot = new File(this.getExternalFilesDir(null), "test");
        inputLayoutBunName.setHint(Utils.getRandomBunnyName(this));
        ivPicture.setImageResource(Utils.getRandomBunnyIcon(this));

        permissionCheck(Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    @OnTextChanged(R.id.input_bun_name)
    public void nameChanged(CharSequence text) {
        if (text.length() > 0) {
            inputLayoutBunName.setHint(text);
        } else {
            inputLayoutBunName.setHint(Utils.getRandomBunnyName(this));
        }
    }

    private void setupRecyclerView() {
        imageAdapter = new ImagesAdapter(this, R.layout.list_row_item_recent_picture);
        rvRecentPictures.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvRecentPictures.setAdapter(imageAdapter);

        imageAdapter.setOnItemClickListener(new OnRecyclerViewItemClickListener<String>() {
            @Override public void onItemClick(View view, String model) {
                setSelectedFile(new File(model));
            }
        });
    }

    @OnClick(R.id.iv_dismiss_dialog)
    public void dismiss() {
        finish();
    }

    @OnClick(R.id.iv_clear_picture)
    public void clear() {
        selectedFile = null;
        ivPicture.setImageResource(Utils.getRandomBunnyIcon(this));
        ivClearPicture.setVisibility(View.GONE);
    }

    @OnClick(R.id.card_submit)
    public void submit() {
        String name = inputLayoutBunName.getHint().toString();
        String message = inputBunText.getText().toString();
        String path = (null == selectedFile) ? null : selectedFile.getAbsolutePath();
        new SubmitBunInBackground(this, name, message, lat, log).execute(path);

        finish();
    }

    @OnClick(R.id.card_gallery)
    public void gallery() {
        permissionCheckGallery(Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    @OnClick(R.id.card_camera)
    public void camera() {
        permissionCheck(Manifest.permission.CAMERA);
    }

    public void setSelectedFile(File file) {
        this.selectedFile = file;
        ivClearPicture.setVisibility(View.VISIBLE);
        Picasso.with(getApplicationContext()).load(selectedFile)
                .into(ivPicture);
    }

    public void takePicture() {
        Intent i = new CameraActivity.IntentBuilder(this)
                .skipConfirm()
                .facing(Facing.BACK)
                .facingExactMatch()
                .to(new File(testRoot, "portrait-rear.jpg"))
                .updateMediaStore()
                .debug()
                .debugSavePreviewFrame()
                .flashModes(FLASH_MODES)
                .zoomStyle(ZoomStyle.SEEKBAR)
                .build();
        startActivityForResult(i, REQUEST_PORTRAIT_RFC);
    }

    private void permissionCheckGallery(String permission) {
        int permissionCheck = ContextCompat.checkSelfPermission(this, permission);

        if (permissionCheck == PermissionChecker.PERMISSION_GRANTED) {
            if (permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                DialogFragment newFragment = GalleryDialog.newInstance();
                newFragment.show(getSupportFragmentManager(), "dialog_gallery");
            }
        } else {
            requestPermission(permission);
        }
    }

    private void permissionCheck(String permission) {
        int permissionCheck = ContextCompat.checkSelfPermission(this, permission);

        if (permissionCheck == PermissionChecker.PERMISSION_GRANTED) {
            if (permission.equals(Manifest.permission.CAMERA)) {
                takePicture();
            } else if (permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                setupRecyclerView();
            }
        } else {
            requestPermission(permission);
        }
    }

    public void requestPermission(String permission) {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{permission}, Utils.permissionsMap().get(permission));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constants.MY_PERMISSIONS_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePicture();
                } else {
                    Toast.makeText(this, "You must enable camera permissions in order to use post a picture", Toast.LENGTH_LONG).show();
                }
            }
            case Constants.MY_PERMISSIONS_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setupRecyclerView();
                } else {
                    Toast.makeText(this, "You must enable read storage to view gallery", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_PORTRAIT_RFC:
                    setSelectedFile(new File(testRoot, "portrait-rear.jpg"));
                    break;
            }
        }
    }
}
