package com.refect.bunalert.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.refect.bunalert.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class Filter1Fragment extends Fragment {

    @BindView(R.id.tv_bun_name) TextView tvBunName;

    private String bunName;

    public Filter1Fragment() {
        // Required empty public constructor
    }

    public static Filter1Fragment newInstance(String name) {
        Filter1Fragment fragment = new Filter1Fragment();
        Bundle args = new Bundle();
        args.putString("name", name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            bunName = getArguments().getString("name");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_filter_1, container, false);
        ButterKnife.bind(this, v);

        Typeface type = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bunnywunny.ttf");
        tvBunName.setTypeface(type);

        tvBunName.setText(bunName);

        return v;
    }

}
