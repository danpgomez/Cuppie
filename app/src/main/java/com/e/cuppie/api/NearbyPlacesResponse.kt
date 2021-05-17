package com.e.cuppie.api

import com.e.cuppie.model.Place
import com.google.gson.annotations.SerializedName

data class NearbyPlacesResponse(
    @SerializedName("results") val results: List<Place>
)
