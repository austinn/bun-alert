package com.refect.bunalert.interfaces;

/**
 * Created by anelson on 1/7/16.
 */
import android.view.View;

public interface OnRecyclerViewItemClickListener<T> {
    public void onItemClick(View view, T model);
}
