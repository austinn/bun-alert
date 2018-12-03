package com.refect.bunalert.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.refect.bunalert.R;

import butterknife.ButterKnife;


public class WelcomeFragment1 extends Fragment {

    public WelcomeFragment1() {
        // Required empty public constructor
    }

    public static WelcomeFragment1 newInstance() {
        WelcomeFragment1 fragment = new WelcomeFragment1();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_welcome_fragment1, container, false);
        ButterKnife.bind(this, v);

        return v;
    }

}
