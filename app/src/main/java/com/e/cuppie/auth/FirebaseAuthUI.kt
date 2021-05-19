package com.e.cuppie.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.e.cuppie.MapsActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth

class FirebaseAuthUI : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createSignInIntent()
    }

    private fun createSignInIntent() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.EmailBuilder().build()
        )

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser
                val intent = Intent(this, MapsActivity::class.java)

                startActivity(intent)
                finish()
//                Navigation.findNavController(this, R.id.fragmentContainerView).navigate(
//                    FirebaseAuthUIDirections.actionFirebaseAuthUIToMapsActivity(user)
//                )
            } else {
                val error = response?.error?.errorCode
                Toast.makeText(
                    this,
                    "⚠️ Unable to log in. Please contact Support. Error: $error",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun signOut() {
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                Toast.makeText(
                    this,
                    "Log out successful",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    companion object {

        private const val RC_SIGN_IN = 123
    }
}
