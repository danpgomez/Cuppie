package com.e.cuppie.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.e.cuppie.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PlaceDetailsFragment : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.place_details, container, false)
    }
}
