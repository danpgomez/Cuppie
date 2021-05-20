package com.e.cuppie.favorites

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.e.cuppie.databinding.FavoritesFragmentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class FavoritesFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: FavoritesViewModel
    private lateinit var binding: FavoritesFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FavoritesFragmentBinding.inflate(inflater, container, false)
        auth = Firebase.auth
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            buttonLogIn.setOnClickListener {
                val email = binding.editTextEmailAddress.text.toString()
                val password = binding.editTextPassword.text.toString()
                signIn(email, password)
            }

            buttonSignUp.setOnClickListener {
                val email = binding.editTextEmailAddress.text.toString()
                val password = binding.editTextPassword.text.toString()
                createAccount(email, password)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            displayFavorites(currentUser)
            hideAuthUIElements()
        }
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(LOG_TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(LOG_TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        context, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }
            }
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(LOG_TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(LOG_TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        context, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        hideAuthUIElements()
        displayFavorites(user)
    }

    private fun hideAuthUIElements() {
        with(binding) {
            buttonLogIn.visibility = View.GONE
            buttonSignUp.visibility = View.GONE
            editTextEmailAddress.visibility = View.GONE
            editTextPassword.visibility = View.GONE
        }
    }

    private fun displayFavorites(user: FirebaseUser?) {
        with(binding) {
            textViewUserGreeting.text =
                "Hello ${user?.email.toString()} 👋! Here are your favorite places:"
            textViewUserGreeting.visibility = View.VISIBLE
            textViewFavorites0.visibility = View.VISIBLE
            textViewFavorites1.visibility = View.VISIBLE
            textViewFavorites2.visibility = View.VISIBLE
            textViewFavorites3.visibility = View.VISIBLE
        }
        user.let {
            val name = it?.displayName.toString()
            val email = it?.email
            val id = it?.uid
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(FavoritesViewModel::class.java)
        // TODO: Use the ViewModel
    }

    companion object {
        fun newInstance() = FavoritesFragment()
        private val LOG_TAG = this::class.java.simpleName
    }

}
