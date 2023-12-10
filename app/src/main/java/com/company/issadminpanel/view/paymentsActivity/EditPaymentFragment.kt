package com.company.issadminpanel.view.paymentsActivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.company.issadminpanel.databinding.FragmentEditPaymentBinding
import com.company.issadminpanel.model.PaymentModel
import com.company.issadminpanel.utils.KeyboardManager
import com.company.issadminpanel.utils.LoadingDialog
import com.company.issadminpanel.utils.NetworkManager
import com.company.issadminpanel.utils.showErrorToast
import com.company.issadminpanel.utils.showSuccessToast
import com.company.issadminpanel.utils.showWarningToast
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson

class EditPaymentFragment : Fragment() {

    private lateinit var binding: FragmentEditPaymentBinding

    private lateinit var payment: PaymentModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditPaymentBinding.inflate(inflater, container, false)
        LoadingDialog.init(requireContext())

        payment = Gson().fromJson(arguments?.getString("DATA"), PaymentModel::class.java)

        binding.debitEdittext.setText(payment.debit_rm)
        binding.creditEdittext.setText(payment.credit_rm)
        binding.statusEdittext.setText(payment.status)

        binding.updateButton.setOnClickListener { updatePayment(it) }

        return binding.root
    }

    private fun updatePayment(view: View) {
        if (binding.debitEdittext.text.toString().trim().isEmpty()) {
            binding.debitEdittext.error = "Enter Debit"
            binding.debitEdittext.requestFocus()
            return
        }
        if (binding.creditEdittext.text.toString().trim().isEmpty()) {
            binding.creditEdittext.error = "Enter Credit"
            binding.creditEdittext.requestFocus()
            return
        }
        if (binding.statusEdittext.text.toString().trim().isEmpty()) {
            binding.statusEdittext.error = "Enter Status"
            binding.statusEdittext.requestFocus()
            return
        }

        payment.debit_rm = binding.debitEdittext.text.toString().trim()
        payment.credit_rm = binding.creditEdittext.text.toString().trim()
        payment.status = binding.statusEdittext.text.toString().trim()

        if (NetworkManager.isInternetAvailable(requireContext())) {
            LoadingDialog.setText("Updating...")
            LoadingDialog.show()

            Firebase.database.reference.child("payments").child(payment.user_id.toString()).child(payment.id.toString())
                .setValue(payment).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        KeyboardManager.hideKeyBoard(requireContext(), view)
                        requireContext().showSuccessToast("Update successful")
                        findNavController().popBackStack()
                    } else {
                        requireContext().showErrorToast("Something wrong.")
                    }
                    LoadingDialog.dismiss()
                }
        } else {
            requireContext().showWarningToast("No internet available")
        }
    }
}