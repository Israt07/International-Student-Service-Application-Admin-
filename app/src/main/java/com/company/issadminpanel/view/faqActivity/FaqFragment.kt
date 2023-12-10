package com.company.issadminpanel.view.faqActivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.company.issadminpanel.R
import com.company.issadminpanel.databinding.FragmentFaqBinding

class FaqFragment : Fragment() {

    //Declaring variables
    private lateinit var binding: FragmentFaqBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFaqBinding.inflate(inflater, container, false)

        //back button click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        //visa button click event
        binding.visaButton.setOnClickListener { findNavController().navigate(R.id.action_faqFragment_to_visaFragment) }

        //financial info button click event
        binding.financialInfoButton.setOnClickListener { goToFaqQuestionAnswerPage("Financial Info") }

        //insurance info button click event
        binding.insuranceInfoButton.setOnClickListener { goToFaqQuestionAnswerPage("Insurance Info") }

        //handbook button click event
        binding.handbookButton.setOnClickListener { findNavController().navigate(R.id.action_faqFragment_to_handbookFragment) }

        return binding.root
    }

    private fun goToFaqQuestionAnswerPage(title: String?) {
        val bundle = bundleOf(
            "TITLE" to title
        )
        findNavController().navigate(R.id.action_faqFragment_to_faqQuestionAnswerFragment, bundle)
    }
}