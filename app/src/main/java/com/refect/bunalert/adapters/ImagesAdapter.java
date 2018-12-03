package com.refect.bunalert.adapters;

/**
 * Created by anelson on 1/7/16.
 */

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.refect.bunalert.R;
import com.refect.bunalert.interfaces.OnRecyclerViewItemClickListener;
import com.refect.bunalert.utils.Utils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> implements View.OnClickListener {

    private List<String> models;
    private static Activity mActivity;

    //animation
    private boolean animateItems = true;
    private static final int ANIMATED_ITEMS_COUNT = 10;
    private int lastAnimatedPosition = -1;
    private OnRecyclerViewItemClickListener<String> itemClickListener;
    private int layout;

    public ImagesAdapter(Activity activity, int layout) {
        this.models = new ArrayList<>();
        this.mActivity = activity;
        this.layout = layout;
        this.models = getAllShownImagesPath(mActivity);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(layout, viewGroup, false);
        v.setOnClickListener(this);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int pos) {
        final String model = models.get(pos);
        viewHolder.itemView.setTag(model);

        if (layout == R.layout.list_row_item_recent_picture_full) {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(Utils.getScreenWidth(mActivity) / 2, Utils.getScreenWidth(mActivity) / 2);
            viewHolder.ivRecentPicture.setLayoutParams(lp);
        }

        File f = new File(model);

        try {
            Picasso.with(mActivity).load(f)
                    .placeholder(R.drawable.bunny1)
                    .into(viewHolder.ivRecentPicture);
        } catch (Exception e) {
            Log.e("ImagesAdapter (onBindViewHolder)", "Error loading bitmap: " + e.toString());
        }
    }

    @Override
    public int getItemCount() {
        return models == null ? 0 : models.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_recent_picture) ImageView ivRecentPicture;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public String getItemAt(int pos) {
        return models.get(pos);
    }


    public void remove(String item) {
        int position = models.indexOf(item);
        models.remove(position);
        notifyItemRemoved(position);
    }

    public void clear() {
        models.clear();
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener<String> listener) {
        this.itemClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (itemClickListener != null) {
            String model = (String) v.getTag();
            itemClickListener.onItemClick(v, model);
        }
    }

    /**
     * Getting All Images Path.
     *
     * @param activity the activity
     * @return ArrayList with images Path
     */
    private ArrayList<String> getAllShownImagesPath(Activity activity) {
        Uri uri;
        Cursor cursor;
        int column_index_data;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        String absolutePathOfImage = null;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        cursor = activity.getContentResolver().query(uri, projection, null,
                null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);
            listOfAllImages.add(absolutePathOfImage);
        }
        return listOfAllImages;
    }

}
