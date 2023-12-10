package com.company.issadminpanel.view.paymentsActivity

import android.annotation.SuppressLint
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
import com.company.issadminpanel.adapter.PaymentsAdapter
import com.company.issadminpanel.databinding.FragmentPaymentsBinding
import com.company.issadminpanel.interfaces.PaymentItemClickListener
import com.company.issadminpanel.model.PaymentModel
import com.company.issadminpanel.repository.PaymentsRepository
import com.company.issadminpanel.view_model.PaymentsViewModel
import com.google.gson.Gson

class PaymentsFragment : Fragment(), PaymentItemClickListener {

    private lateinit var binding: FragmentPaymentsBinding

    private var userId = ""

    private lateinit var repository: PaymentsRepository
    private lateinit var viewModel: PaymentsViewModel

    private var paymentList = ArrayList<PaymentModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPaymentsBinding.inflate(inflater, container, false)

        userId = arguments?.getString("DATA", "").toString()

        repository = PaymentsRepository()
        viewModel = ViewModelProvider(this, PaymentsViewModelFactory(repository))[PaymentsViewModel::class.java]

        //request for data
        viewModel.requestPaymentDetails(userId)

        observerList()

        //back button click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        //set recyclerview adapter
        binding.paymentListRecyclerview.adapter = PaymentsAdapter(paymentList, this)

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun observerList() {
        viewModel.paymentDetailsLiveData.observe(viewLifecycleOwner) { paymentModels ->
            paymentList.clear()
            paymentModels?.let { models ->
                paymentList.addAll(models)
                binding.paymentListRecyclerview.adapter?.notifyDataSetChanged()

                binding.totalTextview.text = "Total: ${models.filter { it.debit_rm != null }.sumByDouble { it.debit_rm!!.toDouble() }.toInt()}"

                binding.dateTextview.text = "Date: ${models[0].credit_date}"
            }

            binding.progressbar.visibility = View.GONE
        }
    }

    override fun onItemClick(currentItem: PaymentModel) {
        val bundle = bundleOf(
            "DATA" to Gson().toJson(currentItem)
        )
        findNavController().navigate(R.id.action_paymentsFragment_to_editPaymentFragment, bundle)
    }
}




class PaymentsViewModelFactory(private val repository: PaymentsRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = PaymentsViewModel(repository) as T
}