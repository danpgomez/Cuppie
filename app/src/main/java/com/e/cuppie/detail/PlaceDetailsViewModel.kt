package com.e.cuppie.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.e.cuppie.model.Geometry
import com.e.cuppie.model.GeometryLocation
import com.e.cuppie.model.OpeningHours
import com.e.cuppie.model.Place

class PlaceDetailsViewModel(place: Place) : ViewModel() {
    private var placeData = MutableLiveData<Place>()
    val place: LiveData<Place> = placeData

    init {
        placeData.value = Place(
            place_id = "123",
            name = "DPG's Coffee Shop",
            geometry = Geometry(GeometryLocation(32.98, 27.788)),
            icon = "Test",
            rating = 3.0,
            vicinity = "8 Clarkson St, New York, NJ 10014",
            business_status = "OPEN NOW",
            opening_hours = OpeningHours(open_now = true)
        )
    }
}
