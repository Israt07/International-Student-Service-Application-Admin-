package com.company.issadminpanel.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.company.issadminpanel.R
import com.company.issadminpanel.databinding.FragmentSplashBinding
import com.company.issadminpanel.utils.SharedPref
import com.google.firebase.auth.FirebaseAuth

class SplashFragment : Fragment() {

    //Declaring variables
    private lateinit var binding: FragmentSplashBinding

    private val waitingTime = 2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        SharedPref.init(requireContext())

        SharedPref.write("USER_TYPE", "")

        //going to next page
        Handler(Looper.getMainLooper()).postDelayed({
            if (FirebaseAuth.getInstance().currentUser != null) {
                findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
            }else {
                findNavController().navigate(R.id.action_splashFragment_to_authFragment)
            }
        }, waitingTime * 1000L)

        return binding.root
    }
}