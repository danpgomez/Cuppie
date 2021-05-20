package com.e.cuppie.model

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize

@Parcelize
data class Place(
    val place_id: String,
    val icon: String,
    val name: String,
    val geometry: Geometry,
    val rating: Double,
    val vicinity: String,
    val business_status: String,
    val opening_hours: OpeningHours?,
) : Parcelable {
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

@Parcelize
data class OpeningHours(
    val open_now: Boolean
) : Parcelable

@Parcelize
data class Geometry(
    val location: GeometryLocation
) : Parcelable

@Parcelize
data class GeometryLocation(
    val lat: Double,
    val lng: Double
) : Parcelable {
    val latLng: LatLng
        get() = LatLng(lat, lng)
}
