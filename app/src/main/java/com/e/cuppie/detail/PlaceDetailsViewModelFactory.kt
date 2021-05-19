package com.e.cuppie.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.e.cuppie.model.Place

class PlaceDetailsViewModelFactory(private val place: Place) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        check(modelClass == PlaceDetailsViewModel::class.java)
        return PlaceDetailsViewModel(place) as T
    }
}
