package com.company.issadminpanel.view.usersActivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.company.issadminpanel.databinding.FragmentEditUserBinding
import com.company.issadminpanel.model.UserModel
import com.company.issadminpanel.utils.KeyboardManager
import com.company.issadminpanel.utils.LoadingDialog
import com.company.issadminpanel.utils.NetworkManager
import com.company.issadminpanel.utils.showErrorToast
import com.company.issadminpanel.utils.showSuccessToast
import com.company.issadminpanel.utils.showWarningToast
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class EditUserFragment : Fragment() {

    //Declaring variables
    private lateinit var binding: FragmentEditUserBinding

    private var userId = ""
    private var name = ""
    private var gender = ""
    private var matricNumber = ""
    private var email = ""
    private var profilePicUrl = ""
    private var userType = ""
    private var mobileNumber = ""
    private var faculty = ""
    private var course = ""
    private var dateOfBirth = ""
    private var country = ""
    private var passportNumber = ""

    private lateinit var userModel: UserModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditUserBinding.inflate(inflater, container, false)
        LoadingDialog.init(requireContext())

        userId = arguments?.getString("USER_ID").toString()
        name = arguments?.getString("NAME").toString()
        gender = arguments?.getString("GENDER").toString()
        matricNumber = arguments?.getString("MATRIC_NUMBER").toString()
        email = arguments?.getString("EMAIL").toString()
        profilePicUrl = arguments?.getString("PROFILE_PIC_URL").toString()
        userType = arguments?.getString("USER_TYPE").toString()
        mobileNumber = arguments?.getString("MOBILE_NUMBER").toString()
        faculty = arguments?.getString("FACULTY").toString()
        course = arguments?.getString("COURSE").toString()
        dateOfBirth = arguments?.getString("DATE_OF_BIRTH").toString()
        country = arguments?.getString("COUNTRY").toString()
        passportNumber = arguments?.getString("PASSPORT_NUMBER").toString()

        userModel = UserModel(userId, name, matricNumber, email, profilePicUrl, userType, mobileNumber, faculty, course, dateOfBirth, country, passportNumber)

        //set details
        binding.nameEdittext.setText(name)
        binding.mobileNumberEdittext.setText(mobileNumber)
        binding.matricNumberEdittext.setText(matricNumber)
        binding.facultyEdittext.setText(faculty)
        binding.courseEdittext.setText(course)

        //back button click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        //update button click event
        binding.updateButton.setOnClickListener { update(it) }

        return binding.root
    }

    private fun update(view: View) {
        if (binding.nameEdittext.text.toString().trim().isEmpty()) {
            binding.nameEdittext.error = "Enter Name"
            binding.nameEdittext.requestFocus()
            return
        }
        if (binding.mobileNumberEdittext.text.toString().trim().isEmpty()) {
            binding.mobileNumberEdittext.error = "Enter Mobile Number"
            binding.mobileNumberEdittext.requestFocus()
            return
        }
        if (binding.matricNumberEdittext.text.toString().trim().isEmpty()) {
            binding.matricNumberEdittext.error = "Enter Matric Number"
            binding.matricNumberEdittext.requestFocus()
            return
        }
        if (binding.facultyEdittext.text.toString().trim().isEmpty()) {
            binding.facultyEdittext.error = "Enter Faculty"
            binding.facultyEdittext.requestFocus()
            return
        }
        if (binding.courseEdittext.text.toString().trim().isEmpty()) {
            binding.courseEdittext.error = "Enter course"
            binding.courseEdittext.requestFocus()
            return
        }

        //set details
        userModel.name = binding.nameEdittext.text.toString().trim()
        userModel.mobile_number = binding.mobileNumberEdittext.text.toString().trim()
        userModel.matric_number = binding.matricNumberEdittext.text.toString().trim()
        userModel.faculty = binding.facultyEdittext.text.toString().trim()
        userModel.course = binding.courseEdittext.text.toString().trim()

        if (NetworkManager.isInternetAvailable(requireContext())) {
            LoadingDialog.setText("Updating...")
            LoadingDialog.show()

            Firebase.database.reference.child("users").child(userId)
                .setValue(userModel).addOnCompleteListener { task ->
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