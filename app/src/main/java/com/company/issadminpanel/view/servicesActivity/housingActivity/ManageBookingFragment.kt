package com.company.issadminpanel.view.servicesActivity.housingActivity

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
import com.company.issadminpanel.adapter.ManageBookingAdapter
import com.company.issadminpanel.databinding.DialogManageBookingBinding
import com.company.issadminpanel.databinding.FragmentManageBookingBinding
import com.company.issadminpanel.interfaces.ManageBookingItemClickListener
import com.company.issadminpanel.model.HousingBookingModel
import com.company.issadminpanel.repository.ManageBookingRepository
import com.company.issadminpanel.utils.LoadingDialog
import com.company.issadminpanel.utils.NetworkManager
import com.company.issadminpanel.utils.showErrorToast
import com.company.issadminpanel.utils.showSuccessToast
import com.company.issadminpanel.utils.showWarningToast
import com.company.issadminpanel.view_model.ManageBookingViewModel
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ManageBookingFragment : Fragment(), ManageBookingItemClickListener {

    //Declaring variables
    private lateinit var binding: FragmentManageBookingBinding

    private lateinit var repository: ManageBookingRepository
    private lateinit var viewModel: ManageBookingViewModel

    private var bookingList = ArrayList<HousingBookingModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentManageBookingBinding.inflate(inflater, container, false)
        LoadingDialog.init(requireContext())

        repository = ManageBookingRepository()
        viewModel = ViewModelProvider(this, ManageBookingViewModelFactory(repository))[ManageBookingViewModel::class.java]

        //request for data
        viewModel.requestBookingList()

        observerList()

        //back button click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        //set recyclerview adapter
        binding.bookingRecyclerview.adapter = ManageBookingAdapter(bookingList,  this)

        return binding.root
    }

    private fun observerList() {
        viewModel.bookingListLiveData.observe(viewLifecycleOwner) {
            bookingList.clear()
            if (it.isNullOrEmpty()) {
                binding.bookingAvailableLayout.visibility = View.GONE
                binding.noBookingAvailableTextview.visibility = View.VISIBLE
            } else {
                bookingList.addAll(it)

                binding.bookingRecyclerview.adapter?.notifyDataSetChanged()

                binding.noBookingAvailableTextview.visibility = View.GONE
                binding.bookingAvailableLayout.visibility = View.VISIBLE
            }
            binding.progressbar.visibility = View.GONE
        }
    }

    override fun onViewButtonClick(currentItem: HousingBookingModel) {
        val builder = AlertDialog.Builder(requireContext())
        val dialogBinding = DialogManageBookingBinding.inflate(layoutInflater)

        builder.setView(dialogBinding.root)
        builder.setCancelable(true)

        val alertDialog = builder.create()

        //make transparent to default dialog
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(0))

        //set details
        dialogBinding.studentNameTextview.text = currentItem.name
        dialogBinding.genderTextview.text = currentItem.gender
        dialogBinding.raceTextview.text = currentItem.name
        dialogBinding.matricNumberTextview.text = currentItem.matric_number
        dialogBinding.emailTextview.text = currentItem.email
        dialogBinding.facultyTextview.text = currentItem.faculty
        dialogBinding.courseTextview.text = currentItem.course
        dialogBinding.yearTextview.text = currentItem.year
        dialogBinding.housingTypeTextview.text = currentItem.housing_type
        dialogBinding.durationTextview.text = currentItem.duration_of_stay
        dialogBinding.paymentStatusTextview.text = currentItem.payment_status
        dialogBinding.bookingStatusTextview.text = currentItem.booking_status
        dialogBinding.noteTextview.text = currentItem.note

        if (currentItem.booking_status == "pending") {
            dialogBinding.bottomSection.visibility = View.VISIBLE
        } else {
            dialogBinding.bottomSection.visibility = View.GONE
        }

        if (currentItem.booking_status != "pending") {
            dialogBinding.noteTextSection.visibility = View.VISIBLE
        } else {
            dialogBinding.noteTextSection.visibility = View.GONE
        }

        //approve button click event
        dialogBinding.approveButton.setOnClickListener { onActionButtonClick(currentItem, "approved", alertDialog, dialogBinding) }

        //reject button click event
        dialogBinding.rejectButton.setOnClickListener { onActionButtonClick(currentItem, "rejected", alertDialog, dialogBinding) }

        alertDialog.show()
    }

    private fun onActionButtonClick(bookingItem: HousingBookingModel, status: String, alertDialog: AlertDialog, dialogBinding: DialogManageBookingBinding) {
        dialogBinding.actionSection.visibility = View.GONE
        dialogBinding.noteSection.visibility = View.VISIBLE

        dialogBinding.submitButton.setOnClickListener {
            if (dialogBinding.noteEdittext.text.toString().trim().isEmpty()) {
                requireContext().showWarningToast("Enter note")
                return@setOnClickListener
            }
            bookingItem.booking_status = status
            bookingItem.note = dialogBinding.noteEdittext.text.toString().trim()

            if (NetworkManager.isInternetAvailable(requireContext())) {
                LoadingDialog.setText("Submitting...")
                LoadingDialog.show()

                Firebase.database.reference.child("services").child("housing").child("bookings").child(bookingItem.booking_id.toString())
                    .setValue(bookingItem).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            requireContext().showSuccessToast("Successful")
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
    }
}



class ManageBookingViewModelFactory(private val repository: ManageBookingRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = ManageBookingViewModel(repository) as T
}