package com.wasfat.ui.activity

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.model.Place
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.wasfat.R
import com.wasfat.ui.base.BaseActivity
import com.wasfat.utils.AppUtils
import com.wasfat.utils.Constants
import com.wasfat.utils.FetchAddressIntentService
import com.wasfat.utils.LocationProvider
import kotlinx.android.synthetic.main.activity_map_location.*
import java.util.*

class LocationMapsActivity : BaseActivity(), OnMapReadyCallback,
    android.location.LocationListener {

    private var mLatitude = 0.0
    private var mLognitude = 0.0
    var mContext: Context? = null
    private var mLocation: Location? = null
    private var currentAddresss = ""
    var placeFields: List<Place.Field>? = null
    private var mMap: GoogleMap? = null
    private var coordinate: LatLng? = null

    var options = MarkerOptions()
    var bundle: Bundle? = null

    var GPS_REQUEST_CODE = 100
    var GPS_SETTING_REQUEST_CODE = 101
    var GPS_PERMISSION_ENABLE = false
    private val errorCallback = LocationProvider.ErrorCallback { }
    var hasPickUpLocationResult = false
    var locationProvider: LocationProvider? = null

    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_location)
        locationProvider = LocationProvider(this, this, errorCallback)
        mFusedLocationProviderClient = LocationServices
            .getFusedLocationProviderClient(currentActivity!!);
        initControls()
    }

    private fun initControls() {
        placeFields = Arrays.asList(Place.Field.ID, Place.Field.ADDRESS, Place.Field.LAT_LNG)
        mContext = this
        enableGPSAutoMatically()
    }


    private fun initMapFragment() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        getDeviceLocation()

    }

    private fun getDeviceLocation() {
        try {
            val locationResult: Task<Location> =
                mFusedLocationProviderClient!!.lastLocation
            locationResult.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val location: Location? = task.result
                    val currentLatLng = LatLng(
                        location!!.latitude,
                        location!!.longitude
                    )
                    val update: CameraUpdate = CameraUpdateFactory.newLatLngZoom(
                        currentLatLng,
                        15f
                    )
                    mMap!!.animateCamera(update)
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message!!)
        }
    }

    private fun enableGPSAutoMatically() {
        val googleApiClient: GoogleApiClient = GoogleApiClient.Builder(this)
            .addApi(LocationServices.API).build()
        googleApiClient.connect()

        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)

        val result = LocationServices.SettingsApi.checkLocationSettings(
            googleApiClient, builder.build()
        )
        result.setResultCallback { result ->
            val status = result.status
            when (status.statusCode) {
                LocationSettingsStatusCodes.SUCCESS -> {
                    Log.e("tag", "SUCCESS")
                    getCurrentLocation()
                }
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                    Log.e("tag", "RESOLUTION_REQUIRED")
                    try {
                        status.startResolutionForResult(
                            this@LocationMapsActivity,
                            GPS_REQUEST_CODE
                        )
                    } catch (e: IntentSender.SendIntentException) {
                    }
                }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    Log.e("tag", "SETTINGS_CHANGE_UNAVAILABLE")
                }

            }
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap;
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }


        mMap!!.isMyLocationEnabled = true
        // val style = MapStyleOptions.loadRawResourceStyle(this, com.google.android.gms.maps.R.raw.style_json)
        // mMap!!.setMapStyle(style);
        mMap!!.uiSettings.isMyLocationButtonEnabled = true
        mMap!!.setOnCameraIdleListener {
            Handler().postDelayed(Runnable {
                hasPickUpLocationResult = false;
            }, 1500)
            var pickUpLatLong = mMap!!.cameraPosition.target;
            var temp = Location(LocationManager.GPS_PROVIDER);
            temp.latitude = pickUpLatLong.latitude;
            temp.longitude = pickUpLatLong.longitude
            getAddress(pickUpLatLong)
        }
        setPickupMarker(coordinate, true)
    }


    private fun getCurrentLocation() {
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    // check if all permissions are granted
                    if (report.areAllPermissionsGranted()) {
                        initMapFragment()
                    }
                    // check for permanent denial of any permission
                    if (report.isAnyPermissionPermanentlyDenied) {
                        // show alert dialog navigating to Settings
                        showSettingsDialog()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                    //grantLocationPermisson()
                }
            }).withErrorListener {
                // UiHelper.showErrorMessage("Error occurred!", coordinatorLayout)
            }
            .onSameThread()
            .check()
    }


    private fun getAddress(latLng: LatLng) {
        mLatitude = latLng.latitude
        mLognitude = latLng.longitude

        Log.d("1234", "lat" + mLatitude + " long: " + mLognitude)

        val geocoder: Geocoder
        val locale = Locale(Constants.LANGUAGE)
        Locale.setDefault(locale)
        geocoder = Geocoder(this, locale)

        try {
            val addresses: List<Address> =
                geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

            if (!addresses.isNullOrEmpty()) {
                if (addresses[0].subAdminArea != null && addresses[0].subAdminArea.isNotEmpty())
                    currentAddresss = addresses[0].subAdminArea
                else if (addresses[0].locality != null && addresses[0].locality.isNotEmpty())
                    currentAddresss = addresses[0].locality
                else currentAddresss = addresses[0].featureName
                if (!currentAddresss.isNullOrEmpty())
                    txtAddress.text = addresses[0].getAddressLine(0)

                btnConfirmAddress.setOnClickListener {
                    var intent = Intent()
                    intent.putExtra("mLatitude", mLatitude.toString())
                    intent.putExtra("mLongtitude", mLognitude.toString())
                    intent.putExtra("addresses", addresses[0].getAddressLine(0))
                    intent.putExtra("locality", addresses[0].locality)
                    intent.putExtra("adminArea", addresses[0].adminArea)
                    intent.putExtra("countryName", addresses[0].countryName)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }


            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun setPickupMarker(coordinate: LatLng?, hasCallStartService: Boolean) {

        try {
            if (coordinate != null) {
                if (hasCallStartService) {
                    startIntentService(mLocation!!)
                    val yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 15f)
                    mMap!!.animateCamera(yourLocation)
                    options.position(coordinate)
                } else {
                    val height = 120
                    val width = 60
                    val bitmapdraw =
                        resources.getDrawable(R.drawable.ic_baseline_location_on_24) as BitmapDrawable
                    val b = bitmapdraw.bitmap
                    val smallMarker = Bitmap.createScaledBitmap(b, width, height, false)
                    val markerOptionsCurrentLocation = MarkerOptions().position(coordinate)
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                    mMap!!.addMarker(markerOptionsCurrentLocation)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    protected fun startIntentService(mLocation: Location) {
        try {
            val intent = Intent(this, FetchAddressIntentService::class.java)
            // intent.putExtra(AppUtils.LocationConstants.RECEIVER, mResultReceiver)
            intent.putExtra(AppUtils.LocationConstants.LOCATION_DATA_EXTRA, mLocation)
            this.startService(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    override fun onLocationChanged(location: Location) {

        val latitude = location.latitude
        val longitude = location.longitude

        mLocation = location
        coordinate = LatLng(latitude, longitude)

    }


    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
        TODO("Not yet implemented")
    }

    override fun onProviderEnabled(provider: String) {
        TODO("Not yet implemented")
    }

    override fun onProviderDisabled(provider: String) {
        TODO("Not yet implemented")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GPS_REQUEST_CODE) {
            Log.e("tag", "GPS_REQUEST_CODE" + data?.getStringExtra("result"))
            if (resultCode == Activity.RESULT_OK)
                getCurrentLocation()
        } else if (requestCode == GPS_SETTING_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                GPS_PERMISSION_ENABLE = true
                getCurrentLocation()
            }
        }
    }

    private fun showSettingsDialog() {
        var alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Need Permissions")
        alertDialog.setMessage("This app needs permission to use this feature. You can grant them in app settings.")
        alertDialog.setPositiveButton(
            "GOTO SETTINGS"
        ) { dialog, which ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivityForResult(intent, GPS_SETTING_REQUEST_CODE)
            dialog.dismiss()
        }
        alertDialog.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }
        val alertDialog1 = alertDialog.create()
        alertDialog1.show()
    }

}