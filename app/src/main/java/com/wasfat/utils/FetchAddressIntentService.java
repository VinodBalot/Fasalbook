package com.wasfat.utils;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.wasfat.R;


import org.json.JSONArray;
import org.json.JSONObject;


/**
 * Asynchronously handles an intent using a worker thread. Receives a ResultReceiver object and a
 * location through an intent. Tries to fetch the address for the location using a Geocoder, and
 * sends the result to the ResultReceiver.
 */
public class FetchAddressIntentService extends IntentService {

    private static final String TAG = "FetchAddressIS";

    /**
     * The receiver where results are forwarded from this service.
     */
    protected ResultReceiver mReceiver;

    /**
     * This constructor is required, and calls the super IntentService(String)
     * constructor with the name for a worker thread.
     */
    public FetchAddressIntentService() {
        // Use the TAG to name the worker thread.
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String errorMessage = "";

        mReceiver = intent.getParcelableExtra(AppUtils.LocationConstants.RECEIVER);

        // Check if receiver was properly registered.
        if (mReceiver == null) {
            Log.wtf(TAG, "No receiver received. There is nowhere to send the results.");
            return;
        }
        // Get the location passed to this service through an extra.
        Location location = intent.getParcelableExtra(AppUtils.LocationConstants.LOCATION_DATA_EXTRA);

        // Make sure that the location data was really sent over through an extra. If it wasn't,
        // send an error error message and return.
        if (location == null) {
            //errorMessage = getString(R.string.no_location_provided);
            Log.wtf(TAG, errorMessage);
            deliverResultToReceiver(AppUtils.LocationConstants.FAILURE_RESULT, errorMessage, null);
            return;
        }

        // Errors could still arise from using the Geocoder (for example, if there is no
        // connectivity, or if the Geocoder is given illegal location data). Or, the Geocoder may
        // simply not have an address for a location. In all these cases, we communicate with the
        // receiver using a resultCode indicating failure. If an address is found, we use a
        // resultCode indicating success.

        // The Geocoder used in this sample. The Geocoder's responses are localized for the given
        // Locale, which represents a specific geographical or linguistic region. Locales are used
        // to alter the presentation of information such as numbers or dates to suit the conventions
        // in the region they describe.
       /* Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        // Address found using the Geocoder.
        List<Address> addresses = null;

        try {
            // Using getFromLocation() returns an array of Addresses for the area immediately
            // surrounding the given latitude and longitude. The results are a best guess and are
            // not guaranteed to be accurate.
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    // In this sample, we get just a single address.
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            errorMessage = getString(R.string.service_not_available);
            Log.e(TAG, errorMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = getString(R.string.invalid_lat_long_used);
            Log.e(TAG, errorMessage + ". " +
                    "Latitude = " + location.getLatitude() +
                    ", Longitude = " + location.getLongitude(), illegalArgumentException);
        }*/

        //String map="http://maps.googleapis.com/maps/api/geocode/json?latlng=26.855308,75.8147631&sensor=false";


        getAddressFromLatLng(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));



      /*  // Handle case where no address was found.
        if (addresses == null || addresses.size() == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = getString(R.string.no_address_found);
                Log.e(TAG, errorMessage);
            }
            deliverResultToReceiver(AppUtils.LocationConstants.FAILURE_RESULT, errorMessage, null);
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();

            for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));

            }
            deliverResultToReceiver(AppUtils.LocationConstants.SUCCESS_RESULT,
                    TextUtils.join(System.getProperty("line.separator"), addressFragments), address);
            //TextUtils.split(TextUtils.join(System.getProperty("line.separator"), addressFragments), System.getProperty("line.separator"));

        }*/
    }

    /* api to fetch lat and lon with address*/
    private void getAddressFromLatLng(final String lat, final String lng) {
        {
            Ion.with(getApplicationContext())
                    .load("https://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + lng + "&sensor=false&key=" + getResources().getString(R.string.google_maps_key))
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            try {
                                if (result != null) {
                                    JSONObject jsonObject = new JSONObject(result);
                                    if (jsonObject.getString("status").equalsIgnoreCase("ok")) {
                                        JSONArray jsonArray = jsonObject.getJSONArray("results");
                                        JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                                        String address = jsonObject1.getString("formatted_address");
                                        deliverResultToReceiver(AppUtils.LocationConstants.SUCCESS_RESULT, address, lat, lng);
                                    }

                              /*  Address address = addresses.get(0);
                                ArrayList<String> addressFragments = new ArrayList<String>();

                                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                                    addressFragments.add(address.getAddressLine(i));

                                }*/
                                /*deliverResultToReceiver(AppUtils.LocationConstants.SUCCESS_RESULT,
                                        TextUtils.join(System.getProperty("line.separator"), addressFragments), addres);*/
                                    //TextUtils.split(TextUtils.join(System.getProperty("line.separator"), addressFragments), System.getProperty("line.separator"));

                                }

                            } catch (Exception e1) {
                                Log.e("Error", e1.toString());
                            }
                        }
                    });
        }
    }

    /**
     * Sends a resultCode and message to the receiver.
     */
    private void deliverResultToReceiver(int resultCode, String message, Address address) {
        try {
            Bundle bundle = new Bundle();
            bundle.putString(AppUtils.LocationConstants.RESULT_DATA_KEY, message);
            bundle.putString(AppUtils.LocationConstants.LOCATION_DATA_ADDRESS1, address.getAddressLine(0));
            bundle.putString(AppUtils.LocationConstants.LOCATION_DATA_ADDRESS2, address.getAddressLine(1));
            bundle.putString(AppUtils.LocationConstants.LOCATION_DATA_AREA, address.getSubLocality());
            bundle.putString(AppUtils.LocationConstants.LOCATION_DATA_CITY, address.getLocality());
            bundle.putString(AppUtils.LocationConstants.LOCATION_DATA_STATE, address.getAdminArea());
            bundle.putString(AppUtils.LocationConstants.LOCATION_DATA_COUNTRY, address.getCountryName());
            bundle.putString(AppUtils.LocationConstants.LOCATION_DATA_POSTAL, address.getPostalCode());

            mReceiver.send(resultCode, bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deliverResultToReceiver(int resultCode, String address, String lat, String lng) {
        Bundle bundle = new Bundle();
        bundle.putString(AppUtils.LocationConstants.RESULT_DATA_KEY, address);
        bundle.putString(AppUtils.LocationConstants.LOCATION_DATA_LATITUDE, lat);
        bundle.putString(AppUtils.LocationConstants.LOCATION_DATA_LONGITUDE, lng);

        mReceiver.send(resultCode, bundle);
    }
}