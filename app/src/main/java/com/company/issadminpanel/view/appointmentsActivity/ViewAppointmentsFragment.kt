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
import com.company.issadminpanel.adapter.ViewAppointmentAdapter
import com.company.issadminpanel.databinding.DialogViewAppointmentBinding
import com.company.issadminpanel.databinding.FragmentViewAppointmentsBinding
import com.company.issadminpanel.interfaces.ViewAppointmentItemClickListener
import com.company.issadminpanel.model.AppointmentModel
import com.company.issadminpanel.repository.ViewAppointmentRepository
import com.company.issadminpanel.utils.SharedPref
import com.company.issadminpanel.view_model.ViewAppointmentViewModel

class ViewAppointmentsFragment : Fragment(), ViewAppointmentItemClickListener {

    //Declaring variables
    private lateinit var binding: FragmentViewAppointmentsBinding

    private lateinit var repository: ViewAppointmentRepository
    private lateinit var viewModel: ViewAppointmentViewModel

    private var appointmentsList = ArrayList<AppointmentModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentViewAppointmentsBinding.inflate(inflater, container, false)
        SharedPref.init(requireContext())

        repository = ViewAppointmentRepository()
        viewModel = ViewModelProvider(this, ViewAppointmentViewModelFactory(repository))[ViewAppointmentViewModel::class.java]

        //request for data
        viewModel.requestAppointmentsList(SharedPref.read("USER_NAME", "").toString())

        observerList()

        //back button click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        //set recyclerview adapter
        binding.appointmentsRecyclerview.adapter = ViewAppointmentAdapter(appointmentsList,  this@ViewAppointmentsFragment)

        return binding.root
    }

    private fun observerList() {
        viewModel.appointmentListLiveData.observe(viewLifecycleOwner) {
            appointmentsList.clear()
            if (it.isNullOrEmpty()) {
                binding.appointmentsAvailableLayout.visibility = View.GONE
                binding.noAppointmentAvailableTextview.visibility = View.VISIBLE
            } else {
                appointmentsList.addAll(it)

                binding.appointmentsRecyclerview.adapter?.notifyDataSetChanged()

                binding.noAppointmentAvailableTextview.visibility = View.GONE
                binding.appointmentsAvailableLayout.visibility = View.VISIBLE
            }
            binding.progressbar.visibility = View.GONE
        }
    }

    override fun onViewButtonClick(currentAppointment: AppointmentModel) {
        val builder = AlertDialog.Builder(requireContext())
        val dialogBinding = DialogViewAppointmentBinding.inflate(layoutInflater)

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

        alertDialog.show()
    }
}



class ViewAppointmentViewModelFactory(private val repository: ViewAppointmentRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = ViewAppointmentViewModel(repository) as T
}