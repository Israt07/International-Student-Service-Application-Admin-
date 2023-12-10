package com.company.issadminpanel.view.faqActivity.visaActivity

import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.company.issadminpanel.R
import com.company.issadminpanel.adapter.VisaRenewAdapter
import com.company.issadminpanel.databinding.DialogViewVisaRenewFormBinding
import com.company.issadminpanel.databinding.FragmentVisaRenewBinding
import com.company.issadminpanel.interfaces.VisaRenewItemClickListener
import com.company.issadminpanel.model.VisaRenewModel
import com.company.issadminpanel.repository.VisaRenewRepository
import com.company.issadminpanel.view_model.VisaRenewViewModel

class VisaRenewFragment : Fragment(), VisaRenewItemClickListener {

    //Declaring variables
    private lateinit var binding: FragmentVisaRenewBinding

    private lateinit var repository: VisaRenewRepository
    private lateinit var viewModel: VisaRenewViewModel

    private var visaList = ArrayList<VisaRenewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentVisaRenewBinding.inflate(inflater, container, false)

        repository = VisaRenewRepository()
        viewModel = ViewModelProvider(this, VisaRenewViewModelFactory(repository))[VisaRenewViewModel::class.java]

        //request for data
        viewModel.requestVisaList()

        observerList()

        //back button click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        //set recyclerview adapter
        binding.formsRecyclerview.adapter = VisaRenewAdapter(visaList,  this)

        return binding.root
    }

    private fun observerList() {
        viewModel.visaListLiveData.observe(viewLifecycleOwner) {
            visaList.clear()
            if (it != null) {
                visaList.addAll(it)

                binding.formsRecyclerview.adapter?.notifyDataSetChanged()

                binding.noFormAvailableTextview.visibility = View.GONE
                binding.formsRecyclerview.visibility = View.VISIBLE
            } else {
                binding.formsRecyclerview.visibility = View.GONE
                binding.noFormAvailableTextview.visibility = View.VISIBLE
            }
            binding.progressbar.visibility = View.GONE
        }
    }

    override fun onItemClick(currentItem: VisaRenewModel) {
        val builder = AlertDialog.Builder(requireContext())
        val dialogBinding = DialogViewVisaRenewFormBinding.inflate(layoutInflater)

        builder.setView(dialogBinding.root)
        builder.setCancelable(true)

        val alertDialog = builder.create()

        //make transparent to default dialog
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(0))

        //set details
        dialogBinding.typeOfPassportTextview.text = currentItem.type_of_passport
        dialogBinding.fullNameTextview.text = currentItem.full_name
        dialogBinding.genderTextview.text = currentItem.gender
        dialogBinding.nationalityTextview.text = currentItem.nationality
        dialogBinding.passportNumberTextview.text = currentItem.passport_number
        dialogBinding.passportExpiryDateTextview.text = currentItem.passport_expiry_date
        dialogBinding.applicationDateTextview.text = currentItem.application_date

        //view student info button click event
        dialogBinding.viewStudentInfoButton.setOnClickListener {
            val bundle = bundleOf(
                "USER_ID" to currentItem.user_id
            )
            findNavController().navigate(R.id.action_visaRenewFragment_to_userDetailsFragment, bundle)
            alertDialog.dismiss()
        }

        alertDialog.show()
    }
}



class VisaRenewViewModelFactory(private val repository: VisaRenewRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = VisaRenewViewModel(repository) as T
}