package com.company.issadminpanel.view.authActivity

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.company.issadminpanel.R
import com.company.issadminpanel.databinding.FragmentRegisterBinding
import com.company.issadminpanel.model.UserModel
import com.company.issadminpanel.utils.LoadingDialog
import com.company.issadminpanel.utils.NetworkManager
import com.company.issadminpanel.utils.SharedPref
import com.company.issadminpanel.utils.showErrorToast
import com.company.issadminpanel.utils.showSuccessToast
import com.company.issadminpanel.utils.showWarningToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class RegisterFragment : Fragment() {

    //Declaring variables
    private lateinit var binding: FragmentRegisterBinding

    private lateinit var userTypeList: Array<String>
    private var selectedUserType = ""

    private lateinit var facultyList: Array<String>
    private var selectedFaculty: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        LoadingDialog.init(requireContext())
        SharedPref.init(requireContext())

        //back button click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        //set details
        userTypeList = arrayOf("ADMIN", "LECTURER", "DOCTOR")

        val userTypeItemAdapter = ArrayAdapter(requireContext(), R.layout.single_dropdown_item, userTypeList)
        binding.userTypeSelectAutoCompleteTextview.setAdapter(userTypeItemAdapter)

        binding.userTypeSelectAutoCompleteTextview.setOnClickListener {
            binding.userTypeSelectAutoCompleteTextview.setText("")
        }

        facultyList = arrayOf("FKE", "FKEKK", "FKP", "FPTT", "FTKEE", "FTMK")

        val facultyItemAdapter = ArrayAdapter(requireContext(), R.layout.single_dropdown_item, facultyList)
        binding.facultySelectAutoCompleteTextview.setAdapter(facultyItemAdapter)

        binding.facultySelectAutoCompleteTextview.setOnClickListener {
            binding.facultySelectAutoCompleteTextview.setText("")
        }

        binding.facultySelectAutoCompleteTextview.doOnTextChanged { text, _, _, _ ->
            selectedFaculty = text.toString()
        }

        binding.countryCodePicker.registerCarrierNumberEditText(binding.mobileNumberEdittext)

        binding.userTypeSelectAutoCompleteTextview.doOnTextChanged { text, _, _, _ ->
            binding.facultySelectAutoCompleteTextview.setText("")
            binding.bioEdittext.setText("")
            binding.specialistInEdittext.setText("")
            selectedUserType = text.toString()
            updateView(selectedUserType)
        }

        //Register button click event
        binding.registerButton.setOnClickListener { registerUser() }

        //login text click event
        binding.loginButton.setOnClickListener { findNavController().navigate(R.id.action_registerFragment_to_loginFragment) }

        return binding.root
    }

    private fun registerUser() {
        if (binding.emailEdittext.text.toString().trim().isEmpty()) {
            binding.emailEdittext.error = "Enter your email"
            binding.emailEdittext.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(binding.emailEdittext.text.toString().trim()).matches()) {
            binding.emailEdittext.error = "Enter valid email"
            binding.emailEdittext.requestFocus()
            return
        }
        if (binding.nameEdittext.text.toString().trim().isEmpty()) {
            binding.nameEdittext.error = "Enter your name"
            binding.nameEdittext.requestFocus()
            return
        }
        if (binding.matricNumberEdittext.text.toString().trim().isEmpty()) {
            binding.matricNumberEdittext.error = "Enter staff id"
            binding.matricNumberEdittext.requestFocus()
            return
        }
        if (binding.mobileNumberEdittext.text.toString().trim().isEmpty()) {
            binding.mobileNumberEdittext.error = "Enter mobile number"
            binding.mobileNumberEdittext.requestFocus()
            return
        }
        if (selectedUserType.isEmpty()) {
            requireContext().showWarningToast("Select user type")
            return
        }
        if (!userTypeList.contains(selectedUserType)) {
            requireContext().showWarningToast("Select valid user type")
            return
        }
        if (binding.passwordEdittext.text.toString().isEmpty()) {
            binding.passwordEdittext.error = "Enter password"
            binding.passwordEdittext.requestFocus()
            return
        }
        if (binding.passwordEdittext.text.toString().trim().length < 6) {
            binding.passwordEdittext.error = "Must be 6 character"
            binding.passwordEdittext.requestFocus()
            return
        }
        if (binding.passwordEdittext.text.toString().trim().lowercase() == binding.nameEdittext.text.toString().trim().lowercase()) {
            binding.passwordEdittext.error = "Must be different from name"
            binding.passwordEdittext.requestFocus()
            return
        }
        if (!Regex(".*[A-Z].*").containsMatchIn(binding.passwordEdittext.text.toString().trim())) {
            binding.passwordEdittext.error = "Password should have at least 1 uppercase letter"
            binding.passwordEdittext.requestFocus()
            return
        }
        if (!Regex(".*[a-z].*").containsMatchIn(binding.passwordEdittext.text.toString().trim())) {
            binding.passwordEdittext.error = "Password should have at least 1 lowercase letter"
            binding.passwordEdittext.requestFocus()
            return
        }
        if (!Regex(".*[0-9].*").containsMatchIn(binding.passwordEdittext.text.toString().trim())) {
            binding.passwordEdittext.error = "Password should have at least 1 number"
            binding.passwordEdittext.requestFocus()
            return
        }
        if (!Regex(".*[@#\$%^&+=].*").containsMatchIn(binding.passwordEdittext.text.toString().trim())) {
            binding.passwordEdittext.error = "Password should have at least 1 special character"
            binding.passwordEdittext.requestFocus()
            return
        }

        if (selectedUserType == "LECTURER") {
            if (binding.bioEdittext.text.toString().trim().isEmpty()) {
                binding.bioEdittext.error = "Enter bio"
                binding.bioEdittext.requestFocus()
                return
            }

            if (selectedFaculty.isNullOrEmpty()) {
                requireContext().showWarningToast("Select your faculty")
                return
            }
            if (!facultyList.contains(selectedFaculty)) {
                requireContext().showWarningToast("Select valid faculty")
                return
            }
        }

        if (selectedUserType == "DOCTOR") {
            if (binding.bioEdittext.text.toString().trim().isEmpty()) {
                binding.bioEdittext.error = "Enter experience"
                binding.bioEdittext.requestFocus()
                return
            }

            if (binding.specialistInEdittext.text.toString().trim().isEmpty()) {
                binding.specialistInEdittext.error = "Enter details"
                binding.specialistInEdittext.requestFocus()
                return
            }
        }

        if (NetworkManager.isInternetAvailable(requireContext())) {
            LoadingDialog.setText("Creating account...")
            LoadingDialog.show()

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(binding.emailEdittext.text.toString().trim(), binding.passwordEdittext.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = FirebaseAuth.getInstance().currentUser?.uid

                        val userModel = UserModel(userId, binding.nameEdittext.text.toString().trim(), null, binding.matricNumberEdittext.text.toString().trim(), binding.emailEdittext.text.toString().trim(), null, selectedUserType, binding.countryCodePicker.fullNumberWithPlus, selectedFaculty, null, null, binding.countryNamePicker.selectedCountryName, null, binding.bioEdittext.text.toString().trim(), binding.specialistInEdittext.text.toString().trim())

                        //adding data to firebase
                        Firebase.database.reference.child("users").child(userId!!)
                            .setValue(userModel).addOnCompleteListener { task2 ->
                                if (task2.isSuccessful) {
                                    requireContext().showSuccessToast("Successful")
                                    LoadingDialog.dismiss()
                                    FirebaseAuth.getInstance().signOut()
                                    findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                                } else {
                                    requireContext().showErrorToast(task2.exception?.localizedMessage.toString())
                                    LoadingDialog.dismiss()
                                }
                            }.addOnFailureListener{ e ->
                                requireContext().showErrorToast(e.localizedMessage!!.toString())
                                LoadingDialog.dismiss()
                            }
                    } else {
                        if (task.exception is FirebaseAuthUserCollisionException) {
                            binding.emailEdittext.error = "Email already registered"
                            binding.emailEdittext.requestFocus()
                        } else {
                            requireContext().showErrorToast(task.exception?.localizedMessage.toString())
                        }

                        LoadingDialog.dismiss()
                    }
                }
        } else {
            requireContext().showWarningToast("No internet available")
        }
    }

    private fun updateView(userType: String){
        binding.facultyCardView.visibility = View.GONE
        binding.specialistInCardView.visibility = View.GONE
        binding.bioCardView.visibility = View.GONE

        when(userType) {
            "LECTURER" -> {
                binding.facultyCardView.visibility = View.VISIBLE
                binding.bioCardView.visibility = View.VISIBLE
            }
            "DOCTOR" -> {
                binding.specialistInCardView.visibility = View.VISIBLE
                binding.bioCardView.visibility = View.VISIBLE
            }
        }
    }
}