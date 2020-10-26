package com.odious.stadsvandring

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    private var loc = Location(LocationManager.NETWORK_PROVIDER)

    fun setCoordinates(location: Location) {
        loc = location
    }

    class LocationHelper {

        val LOCATION_REFRESH_TIME = 3000
        val LOCATION_REFRESH_DISTANCE = 300
        val MY_PERMISSIONS_REQUEST = 100

        var myLocationListener : MyLocationListener? = null

        interface MyLocationListener {
            infix fun onLocationChanged(location : Location)
        }

        fun startListeningUserLocation(context: Context, myListener: MyLocationListener) {
            myLocationListener = myListener

            val mLocationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager

            val mLocationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    myLocationListener!!onLocationChanged(location)
                }

                override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
            }
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_COARSE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {

                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    LOCATION_REFRESH_TIME.toLong(),
                    LOCATION_REFRESH_DISTANCE.toFloat(),
                    mLocationListener)
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(context as
                            Activity, Manifest.permission.ACCESS_COARSE_LOCATION) ||
                            ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    // Permission Denied
                } else {
                    ActivityCompat.requestPermissions(context,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION),
                            MY_PERMISSIONS_REQUEST)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        Log.d("loc", "$loc")
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        LocationHelper().startListeningUserLocation(this, object : LocationHelper.MyLocationListener {
            override fun onLocationChanged(location: Location) {

                val temp = Location(LocationManager.GPS_PROVIDER)
                temp.latitude = location.latitude
                temp.longitude = location.longitude
                Log.d("temp", "$temp")
                setCoordinates(temp)
                Log.d("locAfterTemp", "$loc")

                val markerCordinates = LatLng(location.latitude, location.longitude)
                mMap.addMarker(MarkerOptions().position(markerCordinates).title("Your Current Location"))
                mMap.moveCamera(CameraUpdateFactory.newLatLng(markerCordinates))

            }
        })
    }
    


    /*
    private fun makeApiCall(location: Location) {
        Log.d("Api", "Api Call Start With $location")
        Log.d("makeApiCall", "${location.latitude} ${location.longitude}")
        val request =
            Request
                .Builder()
                .url("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=${location.latitude}," +
                        "${location.longitude}&radius=1500&type=restaurant&key=AIzaSyAKm0fHb6CQqAxb6bk4UcHN0kDhMhLDmFg")
                .build()

        val response = OkHttpClient().newCall(request).execute().body!!.string()
        val jsonObject = JSONObject(response)
    }
     */
}
