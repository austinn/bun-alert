package com.refect.bunalert.activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.refect.bunalert.R;
import com.refect.bunalert.adapters.PagerAdapter;
import com.refect.bunalert.fragments.BlankFragment;
import com.refect.bunalert.fragments.Filter1Fragment;
import com.refect.bunalert.fragments.Filter2Fragment;
import com.refect.bunalert.utils.Constants;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShareActivity extends AppCompatActivity {

    @BindView(R.id.iv_bun_image) ImageView ivBunImage;

    @BindView(R.id.pager_filters) ViewPager pagerFilters;
    private PagerAdapter pagerAdapter;

    private String bunName;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        ButterKnife.bind(this);

        if (null != getIntent()) {
            bunName = getIntent().getStringExtra("name");
            imageUrl = getIntent().getStringExtra("imageUrl");
        }

        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        pagerFilters.setAdapter(pagerAdapter);

        pagerAdapter.add(BlankFragment.newInstance(), "");
        pagerAdapter.add(Filter1Fragment.newInstance(bunName), "");
        pagerAdapter.add(Filter2Fragment.newInstance(bunName), "");

        if (!imageUrl.equals(Constants.EMPTY_STRING)) {
            Picasso.with(this).load(imageUrl)
                    .into(ivBunImage);
        }
    }

//        View v1 = getWindow().getDecorView().getRootView();
//        v1.setDrawingCacheEnabled(true);
//        Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
//        v1.setDrawingCacheEnabled(false);
//
//        Intent i = new Intent(Intent.ACTION_SEND);
//        i.setType("image/*");
//        i.putExtra(Intent.EXTRA_STREAM, getImageUri(getApplicationContext(), bitmap));
//        try {
//            startActivity(Intent.createChooser(i, "Share with..."));
//        } catch (android.content.ActivityNotFoundException ex) {
//            ex.printStackTrace();
//        }

}
