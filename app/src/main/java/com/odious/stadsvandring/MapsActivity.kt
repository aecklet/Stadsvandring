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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


/*    //Displays map of current place
class MapsActivityCurrentPlace : AppCompatActivity(), OnMapReadyCallback {
    private var map: GoogleMap? = null
    private var cameraPosition: CameraPosition? = null

    private lateinit var placesClient: PlacesClient

} */

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

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
                if (ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, Manifest.permission.ACCESS_COARSE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    // Permission Denied
                } else {
                    ActivityCompat.requestPermissions(context, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), MY_PERMISSIONS_REQUEST)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        LocationHelper().startListeningUserLocation(this, object : LocationHelper.MyLocationListener {
            override fun onLocationChanged(location: Location) {
                val longitude = location.longitude
                val latitude = location.longitude
                Log.d("Location", "" + location.latitude + "," + location.longitude)
            }
        })

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
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

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(22.05, -33.1)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
}