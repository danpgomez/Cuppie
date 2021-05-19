package com.e.cuppie.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.e.cuppie.databinding.PlaceDetailsBinding
import com.e.cuppie.model.Place
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PlaceDetailsFragment(private val place: Place) : BottomSheetDialogFragment() {
    private lateinit var placeDetailsViewModel: PlaceDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = PlaceDetailsBinding.inflate(inflater, container, false)

        binding.apply {
            placeName.text = place.name
            ratingText.text = place.rating.toString()
            vicinityText.text = place.vicinity
            openNowText.text = place.business_status
            businessStatusText.text = place.opening_hours?.toString() ?: "unknown"
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }
}
