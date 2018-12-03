package com.refect.bunalert.utils;

import android.Manifest;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

import com.refect.bunalert.R;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by austinn on 8/2/2017.
 */

public class Utils {

    private static int screenWidth = 0;
    private static int screenHeight = 0;

    public final static long SECOND_MILLIS = 1000;
    public final static long MINUTE_MILLIS = SECOND_MILLIS * 60;
    public final static long HOUR_MILLIS = MINUTE_MILLIS * 60;
    public final static long DAY_MILLIS = HOUR_MILLIS * 24;

    public static Date addMinutesToDate(int minutes, Date beforeTime) {
        final long ONE_MINUTE_IN_MILLIS = 60000;//millisecs

        long curTimeInMs = beforeTime.getTime();
        Date afterAddingMins = new Date(curTimeInMs + (minutes * ONE_MINUTE_IN_MILLIS));
        return afterAddingMins;
    }

    public static int getScreenHeight(Context c) {
        if (screenHeight == 0) {
            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenHeight = size.y;
        }

        return screenHeight;
    }

    public static int getScreenWidth(Context c) {
        if (screenWidth == 0) {
            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenWidth = size.x;
        }

        return screenWidth;
    }

    public static Map<String, Integer> permissionsMap() {
        Map<String, Integer> map = new HashMap<>();
        map.put(Manifest.permission.ACCESS_FINE_LOCATION, Constants.MY_PERMISSIONS_ACCESS_FINE_LOCATION);
        map.put(Manifest.permission.CAMERA, Constants.MY_PERMISSIONS_CAMERA);
        map.put(Manifest.permission.READ_EXTERNAL_STORAGE, Constants.MY_PERMISSIONS_READ_EXTERNAL_STORAGE);
        return map;
    }

    public static String getRandomBunnyName(Context ctx) {
        Random rand = new Random();
        String[] names = ctx.getResources().getStringArray(R.array.names);
        return names[rand.nextInt(names.length - 1)];
    }

    public static int getRandomBunnyIcon(Context ctx) {
        int[] icons = {R.drawable.bunny1_icon, R.drawable.bunny2_icon, R.drawable.bunny3_icon, R.drawable.bunny4_icon};
        Random rand = new Random();
        return icons[rand.nextInt(icons.length - 1)];
    }

    public static double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}
