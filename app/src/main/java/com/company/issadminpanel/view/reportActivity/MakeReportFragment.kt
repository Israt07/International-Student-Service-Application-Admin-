package com.company.issadminpanel.view.reportActivity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.company.issadminpanel.databinding.FragmentMakeReportBinding
import com.company.issadminpanel.model.DoctorReportModel
import com.company.issadminpanel.model.UserModel
import com.company.issadminpanel.utils.KeyboardManager
import com.company.issadminpanel.utils.LoadingDialog
import com.company.issadminpanel.utils.NetworkManager
import com.company.issadminpanel.utils.SharedPref
import com.company.issadminpanel.utils.showErrorToast
import com.company.issadminpanel.utils.showSuccessToast
import com.company.issadminpanel.utils.showWarningToast
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MakeReportFragment : Fragment() {

    private lateinit var binding: FragmentMakeReportBinding

    private lateinit var user: UserModel
    private lateinit var studentAge: String
    private lateinit var currentDate: String

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMakeReportBinding.inflate(inflater, container, false)
        LoadingDialog.init(requireContext())
        SharedPref.init(requireContext())

        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        user = Gson().fromJson(arguments?.getString("DATA"), UserModel::class.java)

        studentAge = calculateAge(user.date_of_birth).toString()

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        currentDate = dateFormat.format(Date())

        binding.nameTextview.text = "Name: ${user.name}"
        binding.ageTextview.text = "Age: ${studentAge}"
        binding.genderTextview.text = "Gender: ${user.gender}"
        binding.contactInformationTextview.text = "Contact Information: ${user.mobile_number}"
        binding.dateOfReportTextview.text = "Date of Report: $currentDate"

        binding.makeReportButton.setOnClickListener { makeReport(it) }

        return binding.root
    }

    private fun calculateAge(dobString: String?): Int {
        return try {
            val format = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
            val dob = Calendar.getInstance()
            dob.time = format.parse(dobString)
            val today = Calendar.getInstance()
            val years = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
            if (dob.get(Calendar.DAY_OF_YEAR) > today.get(Calendar.DAY_OF_YEAR)) {
                years - 1
            } else {
                years
            }
        } catch (e: Exception) {
            0
        }
    }

    private fun makeReport(view: View) {
        if (NetworkManager.isInternetAvailable(requireContext())) {
            LoadingDialog.setText("Processing...")
            LoadingDialog.show()

            val reportId = Firebase.database.reference.child("doctors_reports").push().key.toString()

            val reportModel = DoctorReportModel(reportId, user.name, user.user_id, studentAge, user.gender, user.mobile_number, currentDate, binding.medicalHistoryEdittext.text.toString().trim(), binding.currentSymptomsEdittext.text.toString().trim(), binding.medicationNameEdittext.text.toString().trim(), binding.dosageEdittext.text.toString().trim(), binding.frequencyEdittext.text.toString().trim(), binding.testNameEdittext.text.toString().trim(), binding.resultsEdittext.text.toString().trim(), binding.diagnosisEdittext.text.toString().trim(), binding.treatmentEdittext.text.toString().trim(), binding.doctorsNoteEdittext.text.toString().trim(), SharedPref.read("USER_NAME", ""), SharedPref.read("USER_ID", "").toString())

            Firebase.database.reference.child("doctors_reports").child(reportId)
                .setValue(reportModel).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        KeyboardManager.hideKeyBoard(requireContext(), view)
                        requireContext().showSuccessToast("Report submitted")
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