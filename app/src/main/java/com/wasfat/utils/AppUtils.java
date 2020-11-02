package com.wasfat.utils;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;


public class AppUtils {

    public class LocationConstants {
        public static final int SUCCESS_RESULT = 0;

        public static final int FAILURE_RESULT = 1;

        public static final String PACKAGE_NAME = "com.wasfat";

        public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";

        public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";

        public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";


        public static final String LOCATION_DATA_LATITUDE = PACKAGE_NAME + ".LOCATION_DATA_LATITUDE";

        public static final String LOCATION_DATA_LONGITUDE = PACKAGE_NAME + ".LOCATION_DATA_LONGITUDE";


        public static final String LOCATION_DATA_ADDRESS1 = PACKAGE_NAME + ".LOCATION_DATA_ADDRESS1";
        public static final String LOCATION_DATA_ADDRESS2 = PACKAGE_NAME + ".LOCATION_DATA_ADDRESS2";
        public static final String LOCATION_DATA_AREA = PACKAGE_NAME + ".LOCATION_DATA_AREA";
        public static final String LOCATION_DATA_CITY = PACKAGE_NAME + ".LOCATION_DATA_CITY";
        public static final String LOCATION_DATA_STATE = PACKAGE_NAME + ".LOCATION_DATA_STATE";
        public static final String LOCATION_DATA_COUNTRY = PACKAGE_NAME + ".LOCATION_DATA_COUNTRY";
        public static final String LOCATION_DATA_POSTAL = PACKAGE_NAME + ".LOCATION_DATA_POSTAL";

    }


    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }
}