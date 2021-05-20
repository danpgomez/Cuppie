package com.e.cuppie.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.e.cuppie.BuildConfig
import com.e.cuppie.MapsActivity
import com.e.cuppie.R
import com.e.cuppie.api.NearbyPlacesResponse
import com.e.cuppie.api.PlacesService
import com.e.cuppie.model.Place
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var mapFragment: SupportMapFragment
    private var mapReady = false
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: Location? = null
    private lateinit var placesService: PlacesService
    private var places: List<Place>? = null
    private var markers: MutableList<Marker> = emptyList<Marker>().toMutableList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_map, container, false)

        requestLocationPermissionIfNeeded()
        placesService = PlacesService.create()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        requestLocationPermissionIfNeeded()
        mapFragment.getMapAsync {
            with(it) {
                enableLocationIfAllowed()
                getCurrentLocation { location ->
                    val position = CameraPosition.fromLatLngZoom(location.latLng, ZOOM_LEVEL)
                    it.moveCamera(CameraUpdateFactory.newCameraPosition(position))
                    getNearbyPlaces(location, this)
                }
                mMap = this
                mapReady = true
                addClickListenersToMarkers(this)
            }
        }
        return rootView
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_REQUEST_CODE && grantResults.isNotEmpty()
            && (grantResults[0] == PackageManager.PERMISSION_GRANTED)
        ) {
            mapFragment =
                childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
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
            activity?.let {
                ActivityCompat.requestPermissions(
                    it,
                    arrayOf(FINE_LOCATION),
                    LOCATION_REQUEST_CODE
                )
            }
        }
    }

    private fun isLocationPermissionGranted(): Boolean =
        context?.let {
            ContextCompat.checkSelfPermission(
                it,
                FINE_LOCATION
            )
        } == PackageManager.PERMISSION_GRANTED

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

    private fun getNearbyPlaces(location: Location, map: GoogleMap) {
        placesService.nearbyPlaces(
            apiKey = BuildConfig.API_KEY,
            location = "${location.latitude}, ${location.longitude}",
            radiusInMeters = 2000,
            placeType = "cafe"
        ).enqueue(
            object : Callback<NearbyPlacesResponse> {
                override fun onFailure(call: Call<NearbyPlacesResponse>, t: Throwable) {
                    Log.e(LOG_TAG, "Failed to get nearby places", t)
                }

                override fun onResponse(
                    call: Call<NearbyPlacesResponse>,
                    response: Response<NearbyPlacesResponse>
                ) {
                    if (!response.isSuccessful) {
                        Log.e(LOG_TAG, "Failed to get nearby places")
                    }
                    val nearbyPlaces = response.body()?.results ?: emptyList()
                    places = nearbyPlaces
                    addPlacesToMap(map)
                }
            }
        )
    }

    private fun addPlacesToMap(googleMap: GoogleMap) {
        if (currentLocation == null) {
            Log.e(LOG_TAG, "Location has not been determined yet")
        }

        val placesList = places
        placesList?.let { list ->
            for (place in list) {
                googleMap.let { map ->
                    val marker = map.addMarker(
                        MarkerOptions()
                            .position(place.geometry.location.latLng)
                            .title(place.name)
                    )
                    marker?.tag = place
                    marker?.let { markers.add(it) }
                }
            }
        }
    }

    private fun addClickListenersToMarkers(googleMap: GoogleMap) {
        googleMap.setOnMarkerClickListener {
            MapsActivity().showDetailsFragment(it.tag as Place)
            return@setOnMarkerClickListener true
        }
    }

    companion object {
        private val LOG_TAG = MapsActivity::class.java.simpleName
        private const val ZOOM_LEVEL = 13F
        private const val FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
        private const val LOCATION_REQUEST_CODE = 1
    }

    override fun onMapReady(p0: GoogleMap) {
        Log.i(LOG_TAG, "map is ready")
    }
}

val Location.latLng: LatLng
    get() = LatLng(this.latitude, this.longitude)