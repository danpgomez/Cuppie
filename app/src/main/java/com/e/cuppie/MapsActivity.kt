package com.e.cuppie

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private val mapFragment: SupportMapFragment
        get() = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        requestLocationPermissionIfNeeded()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        mapFragment.getMapAsync {
            with(it) {
                enableLocationIfAllowed()
                getCurrentLocation { location ->
                    val position = CameraPosition.fromLatLngZoom(location.latLng, ZOOM_LEVEL)
                    it.moveCamera(CameraUpdateFactory.newCameraPosition(position))
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_REQUEST_CODE && grantResults.isNotEmpty()
            && (grantResults[0] == PackageManager.PERMISSION_GRANTED)
        ) {
            mapFragment.getMapAsync {
                it.enableLocationIfAllowed()
                getCurrentLocation { location ->
                    val position = CameraPosition.fromLatLngZoom(location.latLng, ZOOM_LEVEL)
                    it.moveCamera(CameraUpdateFactory.newCameraPosition(position))
                }
            }
        }
    }

    private fun requestLocationPermissionIfNeeded() {
        if (!isLocationPermissionGranted()) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(FINE_LOCATION),
                LOCATION_REQUEST_CODE
            )
        }
    }

    private fun isLocationPermissionGranted(): Boolean =
        ContextCompat.checkSelfPermission(
            baseContext,
            FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    @SuppressLint("MissingPermission")
    private fun GoogleMap.enableLocationIfAllowed() {
        if (isLocationPermissionGranted()) {
            isMyLocationEnabled = true
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation(onSuccess: (Location) -> Unit) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            currentLocation = location
            onSuccess(location)
        }.addOnFailureListener {
            Log.e(LOG_TAG, "Could not get location")
        }
    }


    // TODO: this function does not seem to be getting called at all
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        Log.i(LOG_TAG, "🍣 onMapReady called")
        // Add a marker in Sydney and move the camera
        val sydney = LatLng(40.7175550, -74.2332241)
        map.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, ZOOM_LEVEL))
    }

    companion object {
        private val LOG_TAG = MapsActivity::class.java.simpleName

        private const val ZOOM_LEVEL = 13F
        private const val FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION

        private const val LOCATION_REQUEST_CODE = 1
    }
}

val Location.latLng: LatLng
    get() = LatLng(this.latitude, this.longitude)
