package com.e.cuppie.permissions

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class PermissionManager(private val fragment: Fragment) {

    fun getLocationPermissionFromUser() {
        checkIfPermissionIsGranted()
        requestPermission()
    }

    private val permissionIsGranted = ContextCompat.checkSelfPermission(
        fragment.requireContext(),
        FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED


    private fun checkIfPermissionIsGranted(): Boolean {
        if (permissionIsGranted) {
            return true
        } else {
            showPermissionRationale()
        }
        // this return may always return false which is not what I want
        return false
    }

    private fun requestPermission() {
        fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            fragment.activity?.let { activity ->
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(FINE_LOCATION),
                    LOCATION_REQUEST_CODE
                )
            }
        }
    }

    private fun showPermissionRationale() {
        AlertDialog.Builder(fragment.requireContext())
                // TODO: Extract string resources
            .setTitle("Permission Required")
            .setMessage("Cuppie needs to access your location to display nearby coffee shops.")
            .setCancelable(false)
            .setPositiveButton("Ok") { _,_ ->
                requestPermission()
            }
            .show()
    }

    companion object {
        private const val FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
        private const val LOCATION_REQUEST_CODE = 1
    }
}
