package com.company.issadminpanel.view.faqActivity.visaActivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.company.issadminpanel.R
import com.company.issadminpanel.databinding.FragmentVisaBinding

class VisaFragment : Fragment() {

    //Declaring variables
    private lateinit var binding: FragmentVisaBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentVisaBinding.inflate(inflater, container, false)

        //back button click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        //visa information button click event
        binding.visaInformationButton.setOnClickListener { goToFaqQuestionAnswerPage("Visa Information") }

        //renew form button click event
        binding.renewFormButton.setOnClickListener { findNavController().navigate(R.id.action_visaFragment_to_visaRenewFragment) }

        return binding.root
    }

    private fun goToFaqQuestionAnswerPage(title: String?) {
        val bundle = bundleOf(
            "TITLE" to title
        )
        findNavController().navigate(R.id.action_visaFragment_to_faqQuestionAnswerFragment, bundle)
    }
}