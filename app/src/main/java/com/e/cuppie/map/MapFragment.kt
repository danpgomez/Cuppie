package com.e.cuppie.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.e.cuppie.BuildConfig
import com.e.cuppie.R
import com.e.cuppie.api.NearbyPlacesResponse
import com.e.cuppie.api.PlacesService
import com.e.cuppie.model.Place
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapFragment : Fragment() {
    private var places: List<Place>? = null
    private var currentLocation: Location? = null
    private lateinit var placesService: PlacesService
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var markers: MutableList<Marker> = emptyList<Marker>().toMutableList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_map, container, false)
        mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment

        requestLocationPermissionIfNeeded()
        placesService = PlacesService.create()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        setUpMap()
        setHasOptionsMenu(true)

        return rootView
    }

    // Overflow menu setup
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.overflow_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(
            item,
            requireView().findNavController()
        ) or (super.onOptionsItemSelected(item))
    }

    // Map configuration
    private fun setUpMap() {
        mapFragment.getMapAsync { map ->
            map.enableLocationIfAllowed()
            getCurrentLocation { location ->
                val position = CameraPosition.fromLatLngZoom(location.latLng, ZOOM_LEVEL)
                map.moveCamera(CameraUpdateFactory.newCameraPosition(position))
                getNearbyCafes(location, map)
                addClickListenersToMarkers(map)
            }
        }
    }

    private fun getNearbyCafes(location: Location, map: GoogleMap) {
        placesService.getNearbyPlaces(
            apiKey = BuildConfig.API_KEY,
            location = "${location.latitude}, ${location.longitude}",
            radiusInMeters = 2000,
            placeType = getString(R.string.cafe_place_type)
        ).enqueue(
            object : Callback<NearbyPlacesResponse> {

                override fun onFailure(call: Call<NearbyPlacesResponse>, t: Throwable) {
                    Log.e(LOG_TAG, getString(R.string.error_failed_to_get_places), t)
                }

                override fun onResponse(
                    call: Call<NearbyPlacesResponse>,
                    response: Response<NearbyPlacesResponse>
                ) {
                    if (!response.isSuccessful) {
                        Log.e(LOG_TAG, getString(R.string.error_failed_to_get_places))
                    }

                    val nearbyPlaces = response.body()?.results ?: emptyList()
                    places = nearbyPlaces
                    addPlacesToMap(map)
                }
            }
        )
    }

    private fun addPlacesToMap(googleMap: GoogleMap) {
        if (currentLocation == null) Log.e(LOG_TAG, getString(R.string.location_not_determined))

        places?.let { list ->
            for (place in list) {
                createPlaceMarker(googleMap, place)
            }
        }
    }

    private fun createPlaceMarker(
        map: GoogleMap,
        place: Place
    ) {
        val marker = map.addMarker(
            MarkerOptions()
                .position(place.geometry.location.latLng)
                .title(place.name)
        )
        marker?.tag = place
        marker?.let { markers.add(it) }
    }

    private fun addClickListenersToMarkers(googleMap: GoogleMap) {
        googleMap.setOnMarkerClickListener {
            val directions =
                MapFragmentDirections.actionMapFragmentToPlaceDetailsFragment(it.tag as Place)
            view?.findNavController()?.navigate(directions)
            return@setOnMarkerClickListener true
        }
    }

    override fun onResume() {
        super.onResume()
        setUpMap()
    }

    // Request Permissions
    private fun hasFineLocationPermission(): Boolean =
        context?.let {
            ContextCompat.checkSelfPermission(
                it,
                FINE_LOCATION
            )
        } == PackageManager.PERMISSION_GRANTED

    private fun requestLocationPermissionIfNeeded() {
        if (!hasFineLocationPermission()) {
            activity?.let {
                ActivityCompat.requestPermissions(
                    it,
                    arrayOf(FINE_LOCATION),
                    LOCATION_REQUEST_CODE
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        val permissionIsGranted = (
                requestCode == LOCATION_REQUEST_CODE && grantResults.isNotEmpty()
                        && (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                )

        if (permissionIsGranted) {
            setUpMap()
        }
    }

    @SuppressLint("MissingPermission")
    private fun GoogleMap.enableLocationIfAllowed() {
        if (hasFineLocationPermission()) {
            isMyLocationEnabled = true
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation(onSuccess: (Location) -> Unit) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            currentLocation = location
            onSuccess(location)
        }.addOnFailureListener {
            Log.e(LOG_TAG, getString(R.string.location_not_determined))
        }
    }

    companion object {
        private val LOG_TAG = this::class.java.simpleName
        private const val ZOOM_LEVEL = 13F
        private const val FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
        private const val LOCATION_REQUEST_CODE = 1
    }
}

val Location.latLng: LatLng
    get() = LatLng(this.latitude, this.longitude)
