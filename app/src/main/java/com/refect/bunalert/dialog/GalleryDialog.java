package com.refect.bunalert.dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.refect.bunalert.R;
import com.refect.bunalert.activities.NewPostActivity;
import com.refect.bunalert.adapters.ImagesAdapter;
import com.refect.bunalert.interfaces.OnRecyclerViewItemClickListener;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Austin Nelson on 1/19/2017.
 */

public class GalleryDialog extends DialogFragment {

    @BindView(R.id.rv_gallery) RecyclerView rvGallery;
    private ImagesAdapter imagesAdapter;

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public static GalleryDialog newInstance() {
        GalleryDialog f = new GalleryDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        f.setArguments(args);

        return f;
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Pick a style based on the num.
        int style = DialogFragment.STYLE_NO_TITLE;
        int theme = android.R.style.Theme_Material_NoActionBar_Fullscreen;

        setStyle(style, theme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_gallery, container, false);
        ButterKnife.bind(this, v);

        imagesAdapter = new ImagesAdapter(getActivity(), R.layout.list_row_item_recent_picture_full);
        rvGallery.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        rvGallery.setAdapter(imagesAdapter);

        imagesAdapter.setOnItemClickListener(new OnRecyclerViewItemClickListener<String>() {
            @Override public void onItemClick(View view, String model) {
                ((NewPostActivity) getActivity()).setSelectedFile(new File(model));
                getDialog().dismiss();
            }
        });

        return v;
    }
}
