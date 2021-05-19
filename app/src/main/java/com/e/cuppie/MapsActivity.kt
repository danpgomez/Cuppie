package com.e.cuppie

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.e.cuppie.detail.PlaceDetailsFragment
import com.e.cuppie.model.Place

class MapsActivity : AppCompatActivity() {
    private lateinit var detailsFragment: PlaceDetailsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
    }

    fun showDetailsFragment(place: Place) {
        detailsFragment = PlaceDetailsFragment(place)
        detailsFragment.show(supportFragmentManager, "Place Details Fragment")
    }
}
