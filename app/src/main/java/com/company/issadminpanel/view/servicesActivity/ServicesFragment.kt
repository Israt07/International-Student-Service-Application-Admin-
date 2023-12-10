package com.company.issadminpanel.view.servicesActivity

import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.company.issadminpanel.R
import com.company.issadminpanel.databinding.DialogUpdateTransportationBinding
import com.company.issadminpanel.databinding.FragmentServicesBinding
import com.company.issadminpanel.utils.KeyboardManager
import com.company.issadminpanel.utils.LoadingDialog
import com.company.issadminpanel.utils.NetworkManager
import com.company.issadminpanel.utils.showErrorToast
import com.company.issadminpanel.utils.showSuccessToast
import com.company.issadminpanel.utils.showWarningToast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ServicesFragment : Fragment() {

    //Declaring variables
    private lateinit var binding: FragmentServicesBinding

    private var transportationLink = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentServicesBinding.inflate(inflater, container, false)
        LoadingDialog.init(requireContext())

        //request for data
        getDataFromFirebase()

        //back button click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        //housing button click event
        binding.housingButton.setOnClickListener { findNavController().navigate(R.id.action_servicesFragment_to_housingFragment) }

        //transportation button click event
        binding.transportationButton.setOnClickListener { openTransportationDialog() }

        return binding.root
    }

    private fun getDataFromFirebase() {
        Firebase.database.reference.child("services").child("transportation").addValueEventListener(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    transportationLink = snapshot.child("transportationImageLink").value as String
                }
                binding.progressbar.visibility = View.GONE
                binding.mainLayout.visibility = View.VISIBLE
            }

            override fun onCancelled(error: DatabaseError) {
                binding.progressbar.visibility = View.GONE
                binding.mainLayout.visibility = View.VISIBLE
            }

        })
    }

    private fun openTransportationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val dialogBinding = DialogUpdateTransportationBinding.inflate(layoutInflater)

        builder.setView(dialogBinding.root)
        builder.setCancelable(true)

        val alertDialog = builder.create()

        //make transparent to default dialog
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(0))

        dialogBinding.transportationFileLinkEdittext.setText(transportationLink)

        //update transportation button click event
        dialogBinding.updateTransportationButton.setOnClickListener {
            if (dialogBinding.transportationFileLinkEdittext.text.toString().trim().isEmpty()) {
                dialogBinding.transportationFileLinkEdittext.error = "Enter file link"
                dialogBinding.transportationFileLinkEdittext.requestFocus()
                return@setOnClickListener
            }

            if (NetworkManager.isInternetAvailable(requireContext())) {
                LoadingDialog.setText("Updating...")
                LoadingDialog.show()
                KeyboardManager.hideKeyBoard(requireContext(), it)

                Firebase.database.reference.child("services").child("transportation").child("transportationImageLink")
                    .setValue(dialogBinding.transportationFileLinkEdittext.text.toString().trim()).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            requireContext().showSuccessToast("Update successful")
                            alertDialog.dismiss()
                        } else {
                            requireContext().showErrorToast("Something wrong.")
                        }
                        LoadingDialog.dismiss()
                    }
            } else {
                requireContext().showWarningToast("No internet available")
            }
        }

        alertDialog.show()
    }
}