package com.e.cuppie.model

import com.google.android.gms.maps.model.LatLng

data class Place(
    val place_id: String,
    val icon: String,
    val name: String,
    val geometry: Geometry,
    val rating: Double,
    val vicinity: String,
    val business_status: String,
    val opening_hours: OpeningHours?,
) {
    override fun equals(other: Any?): Boolean {
        if (other !is Place) {
            return false
        }
        return this.place_id == other.place_id
    }

    override fun hashCode(): Int {
        return this.place_id.hashCode()
    }
}

data class OpeningHours(
    val open_now: Boolean
)

data class Geometry(
    val location: GeometryLocation
)

data class GeometryLocation(
    val lat: Double,
    val lng: Double
) {
    val latLng: LatLng
        get() = LatLng(lat, lng)
}
