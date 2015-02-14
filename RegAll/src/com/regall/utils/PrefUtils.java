package com.regall.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

/**
 * Created by Alex on 11.02.2015.
 */
public class PrefUtils {

    private static final String PREFS_FILE_NAME = "regall_prefs";
    private static final String PREFS_KEY_LAT = "lat";
    private static final String PREFS_KEY_LON = "lon";

    private static final float DEFAULT_LATITUDE = 44.6054393f;
    private static final float DEFAULT_LONGITUDE = 33.5161317f;

    public static void saveDefaultLocation(Context context, Location location) {
        if (location == null) {
            return;
        }
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE).edit();
        editor.putFloat(PREFS_KEY_LAT, (float) location.getLatitude());
        editor.putFloat(PREFS_KEY_LON, (float) location.getLongitude());
        editor.commit();
    }

    public static Location getDefaultLocation(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        Location location = new Location("me");
        location.setLatitude(prefs.getFloat(PREFS_KEY_LAT, DEFAULT_LATITUDE));
        location.setLongitude(prefs.getFloat(PREFS_KEY_LON, DEFAULT_LONGITUDE));
        return location;
    }

}
