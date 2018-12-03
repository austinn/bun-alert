package com.refect.bunalert.adapters;

/**
 * Created by anelson on 1/7/16.
 */

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.refect.bunalert.R;
import com.refect.bunalert.interfaces.OnRecyclerViewItemClickListener;
import com.refect.bunalert.models.Bun;
import com.refect.bunalert.utils.Constants;
import com.refect.bunalert.utils.Utils;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BunAdapter extends RecyclerView.Adapter<BunAdapter.ViewHolder> implements View.OnClickListener {

    private List<Bun> models;
    private static Activity mActivity;

    //animation
    private boolean animateItems = true;
    private static final int ANIMATED_ITEMS_COUNT = 10;
    private int lastAnimatedPosition = -1;
    private OnRecyclerViewItemClickListener<Bun> itemClickListener;

    private double lat;
    private double log;

    public BunAdapter(Activity activity, double lat, double log) {
        this.models = new ArrayList<>();
        this.mActivity = activity;
        this.lat = lat;
        this.log = log;
    }

    public void setModels(ArrayList<Bun> buns) {
        this.models = buns;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row_item_bun, viewGroup, false);
        v.setOnClickListener(this);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int pos) {
        final Bun model = models.get(pos);
        viewHolder.itemView.setTag(model);

        viewHolder.tvPetName.setText(model.getName());
        viewHolder.tvPetDetails.setText(model.getMessage());

        double km = Utils.distance(lat, log, model.getLocation().get(0), model.getLocation().get(1));
        String distance = String.format("%.2f", km / Constants.KM);
        viewHolder.tvPetDistance.setText(distance + " miles away");

        PrettyTime p = new PrettyTime();
        viewHolder.tvPetLastUpdated.setText(p.format(Utils.addMinutesToDate(-5, new Date(model.getExpiration()))));

        if (null == model.getImageUrl()) {
            Picasso.with(mActivity).load(Utils.getRandomBunnyIcon(mActivity))
                    .into(viewHolder.ivPetImage);
        } else {
            Picasso.with(mActivity).load(model.getImageUrl())
                    .placeholder(Utils.getRandomBunnyIcon(mActivity))
                    .into(viewHolder.ivPetImage);
        }
    }

    @Override
    public int getItemCount() {
        return models == null ? 0 : models.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_pet_image) ImageView ivPetImage;
        @BindView(R.id.tv_pet_name) TextView tvPetName;
        @BindView(R.id.tv_pet_details) TextView tvPetDetails;
        @BindView(R.id.tv_pet_distance) TextView tvPetDistance;
        @BindView(R.id.tv_pet_last_updated) TextView tvPetLastUpdated;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public Bun getItemAt(int pos) {
        return models.get(pos);
    }

    public void add(Bun item) {
        models.add(0, item);
        notifyItemInserted(0);
    }

    public void remove(Bun item) {
        int position = models.indexOf(item);
        models.remove(position);
        notifyItemRemoved(position);
    }

    public void clear() {
        models.clear();
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener<Bun> listener) {
        this.itemClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (itemClickListener != null) {
            Bun model = (Bun) v.getTag();
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
        int column_index_data, column_index_folder_name;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        String absolutePathOfImage = null;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        cursor = activity.getContentResolver().query(uri, projection, null,
                null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);
            listOfAllImages.add(absolutePathOfImage);
        }
        return listOfAllImages;
    }

}
