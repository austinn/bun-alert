package com.refect.bunalert.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;

/**
 * Created by austinn on 8/3/2017.
 */

public class ImageUtils {

    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    /**
     * Converts a resource to a bitmap
     *
     * @param resource
     * @return
     */
    public static Bitmap resource2Bitmap(Context ctx, int resource) {
        return BitmapFactory.decodeResource(ctx.getResources(), resource);
    }

    public static byte[] resource2ByteArray(Context ctx, int resource) {
        return bitmap2ByteArray(resource2Bitmap(ctx, resource));
    }

    /**
     * Converts a bitmap into a byte array
     *
     * @param bmp
     * @return
     */
    public static byte[] bitmap2ByteArray(Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 99, stream);
        return stream.toByteArray();
    }

    /**
     * Converts a byte array into a bitmap
     *
     * @param data
     * @return
     */
    public static Bitmap byteArray2Bitmap(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    public static Bitmap resizeBitmap(Bitmap bitmap, int maxSize) {
        int outWidth;
        int outHeight;
        int inWidth = bitmap.getWidth();
        int inHeight = bitmap.getHeight();
        if (inWidth > inHeight) {
            outWidth = maxSize;
            outHeight = (inHeight * maxSize) / inWidth;
        } else {
            outHeight = maxSize;
            outWidth = (inWidth * maxSize) / inHeight;
        }

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, outWidth, outHeight, false);
        return resizedBitmap;
    }
}
