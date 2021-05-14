package com.e.cuppie.model

import com.google.android.gms.maps.model.LatLng

data class Place(
    val id: String,
    val icon: String,
    val name: String,
    val latLng: LatLng,
) {
    override fun equals(other: Any?): Boolean {
       if (other !is Place) {
           return false
       }
        return this.id == other.id
    }

    override fun hashCode(): Int {
        return this.id.hashCode()
    }
}
