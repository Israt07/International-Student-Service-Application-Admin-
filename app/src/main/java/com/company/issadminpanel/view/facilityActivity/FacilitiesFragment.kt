package com.company.issadminpanel.view.facilityActivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.company.issadminpanel.R
import com.company.issadminpanel.databinding.FragmentFacilitiesBinding

class FacilitiesFragment : Fragment() {

    //Declaring variables
    private lateinit var binding: FragmentFacilitiesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFacilitiesBinding.inflate(inflater, container, false)

        //back button click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        //mosque button click event
        binding.mosqueButton.setOnClickListener { goToFacilityDetailsPage("Mosque") }

        //lab button click event
        binding.labButton.setOnClickListener { goToFacilityDetailsPage("Lab") }

        //library button click event
        binding.libraryButton.setOnClickListener { goToFacilityDetailsPage("Library") }

        //canteen button click event
        binding.canteenButton.setOnClickListener { goToFacilityDetailsPage("Canteen") }

        //health care button click event
        binding.healthCareButton.setOnClickListener { goToFacilityDetailsPage("Health Care") }

        //emergency contact button click event
        binding.emergencyContactButton.setOnClickListener { goToFacilityDetailsPage("Emergency Contact") }

        //community button click event
        binding.communityButton.setOnClickListener { findNavController().navigate(R.id.action_facilitiesFragment_to_communityFragment) }

        return binding.root
    }

    private fun goToFacilityDetailsPage(title: String?) {
        val bundle = bundleOf(
            "TITLE" to title
        )
        findNavController().navigate(R.id.action_facilitiesFragment_to_facilityDetailsFragment, bundle)
    }
}