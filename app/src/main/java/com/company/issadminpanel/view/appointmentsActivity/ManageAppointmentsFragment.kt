package com.company.issadminpanel.view.appointmentsActivity

import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.company.issadminpanel.adapter.ManageAppointmentAdapter
import com.company.issadminpanel.databinding.DialogManageAppointmentBinding
import com.company.issadminpanel.databinding.FragmentManageAppointmentsBinding
import com.company.issadminpanel.interfaces.ManageAppointmentItemClickListener
import com.company.issadminpanel.model.AppointmentModel
import com.company.issadminpanel.repository.ManageAppointmentRepository
import com.company.issadminpanel.utils.LoadingDialog
import com.company.issadminpanel.utils.NetworkManager
import com.company.issadminpanel.utils.SharedPref
import com.company.issadminpanel.utils.showErrorToast
import com.company.issadminpanel.utils.showSuccessToast
import com.company.issadminpanel.utils.showWarningToast
import com.company.issadminpanel.view_model.ManageAppointmentViewModel
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ManageAppointmentsFragment : Fragment(), ManageAppointmentItemClickListener {

    //Declaring variables
    private lateinit var binding: FragmentManageAppointmentsBinding

    private lateinit var repository: ManageAppointmentRepository
    private lateinit var viewModel: ManageAppointmentViewModel

    private var pendingAppointmentsList = ArrayList<AppointmentModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentManageAppointmentsBinding.inflate(inflater, container, false)
        SharedPref.init(requireContext())
        LoadingDialog.init(requireContext())

        repository = ManageAppointmentRepository()
        viewModel = ViewModelProvider(this, ManageAppointmentViewModelFactory(repository))[ManageAppointmentViewModel::class.java]

        //request for data
        viewModel.requestPendingAppointmentsList(SharedPref.read("USER_NAME", "").toString())

        observerList()

        //back button click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        //set recyclerview adapter
        binding.appointmentsRecyclerview.adapter = ManageAppointmentAdapter(pendingAppointmentsList,  this@ManageAppointmentsFragment)

        return binding.root
    }

    private fun observerList() {
        viewModel.pendingAppointmentListLiveData.observe(viewLifecycleOwner) {
            pendingAppointmentsList.clear()
            if (it.isNullOrEmpty()) {
                binding.appointmentsAvailableLayout.visibility = View.GONE
                binding.noAppointmentAvailableTextview.visibility = View.VISIBLE
            } else {
                pendingAppointmentsList.addAll(it)

                binding.appointmentsRecyclerview.adapter?.notifyDataSetChanged()

                binding.noAppointmentAvailableTextview.visibility = View.GONE
                binding.appointmentsAvailableLayout.visibility = View.VISIBLE

            }
            binding.progressbar.visibility = View.GONE
        }
    }

    override fun onViewButtonClick(currentAppointment: AppointmentModel) {
        val builder = AlertDialog.Builder(requireContext())
        val dialogBinding = DialogManageAppointmentBinding.inflate(layoutInflater)

        builder.setView(dialogBinding.root)
        builder.setCancelable(true)

        val alertDialog = builder.create()

        //make transparent to default dialog
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(0))

        //set details
        dialogBinding.studentNameTextview.text = currentAppointment.name
        dialogBinding.matricNumberTextview.text = currentAppointment.matric_number
        dialogBinding.emailTextview.text = currentAppointment.email
        dialogBinding.facultyTextview.text = currentAppointment.faculty
        dialogBinding.dateTextview.text = currentAppointment.date
        dialogBinding.timeTextview.text = currentAppointment.time

        //approve button click event
        dialogBinding.approveButton.setOnClickListener {
            currentAppointment.appointment_status = "approved"
            if (NetworkManager.isInternetAvailable(requireContext())) {
                LoadingDialog.setText("Approving...")
                LoadingDialog.show()

                Firebase.database.reference.child("appointments").child(currentAppointment.appointment_id.toString())
                    .setValue(currentAppointment).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            requireContext().showSuccessToast("Approved")
                        } else {
                            requireContext().showErrorToast("Something wrong.")
                        }
                        alertDialog.dismiss()
                        LoadingDialog.dismiss()
                    }
            } else {
                requireContext().showWarningToast("No internet available")
            }
        }

        //reject text click event
        dialogBinding.rejectTextview.setOnClickListener {
            dialogBinding.actionSection.visibility = View.GONE
            dialogBinding.rejectSection.visibility = View.VISIBLE
        }

        //reject button click event
        dialogBinding.rejectButton.setOnClickListener {
            if (dialogBinding.rejectionReasonEdittext.text.toString().trim().isEmpty()) {
                requireContext().showWarningToast("Enter Rejection reason")
                return@setOnClickListener
            }
            currentAppointment.appointment_status = "rejected"
            currentAppointment.rejection_reason = dialogBinding.rejectionReasonEdittext.text.toString().trim()

            if (NetworkManager.isInternetAvailable(requireContext())) {
                LoadingDialog.setText("Rejecting...")
                LoadingDialog.show()

                Firebase.database.reference.child("appointments").child(currentAppointment.appointment_id.toString())
                    .setValue(currentAppointment).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            requireContext().showSuccessToast("Rejected")
                        } else {
                            requireContext().showErrorToast("Something wrong.")
                        }
                        alertDialog.dismiss()
                        LoadingDialog.dismiss()
                    }
            } else {
                requireContext().showWarningToast("No internet available")
            }
        }

        alertDialog.show()
    }
}



class ManageAppointmentViewModelFactory(private val repository: ManageAppointmentRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = ManageAppointmentViewModel(repository) as T
}