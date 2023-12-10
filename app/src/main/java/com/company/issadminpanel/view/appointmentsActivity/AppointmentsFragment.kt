package com.company.issadminpanel.view.appointmentsActivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.company.issadminpanel.R
import com.company.issadminpanel.databinding.FragmentAppointmentsBinding

class AppointmentsFragment : Fragment() {

    //Declaring variables
    private lateinit var binding: FragmentAppointmentsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAppointmentsBinding.inflate(inflater, container, false)

        //back button click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        //manage appointments button click event
        binding.manageAppointmentsButton.setOnClickListener { findNavController().navigate(R.id.action_appointmentsFragment_to_manageAppointmentsFragment) }

        //view appointments button click event
        binding.viewAppointmentsButton.setOnClickListener { findNavController().navigate(R.id.action_appointmentsFragment_to_viewAppointmentsFragment) }

        return binding.root
    }
}