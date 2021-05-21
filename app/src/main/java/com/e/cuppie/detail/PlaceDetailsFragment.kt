package com.e.cuppie.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.e.cuppie.databinding.PlaceDetailsBinding

class PlaceDetailsFragment : Fragment() {
    private val place by lazy {
        PlaceDetailsFragmentArgs.fromBundle(requireArguments()).place
    }
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
            openNowText.text =
                if (place.opening_hours.toString() == "OpeningHours(open_now=true)") "Open Now" else "Closed"
            businessStatusText.text =
                if (place.business_status == "OPERATIONAL") "Operational" else "Permanently Closed "
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }
}
