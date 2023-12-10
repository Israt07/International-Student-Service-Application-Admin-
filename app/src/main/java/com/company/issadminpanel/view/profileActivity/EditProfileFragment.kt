package com.company.issadminpanel.view.profileActivity

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.company.issadminpanel.R
import com.company.issadminpanel.databinding.FragmentEditProfileBinding
import com.company.issadminpanel.model.UserModel
import com.company.issadminpanel.repository.EditProfileRepository
import com.company.issadminpanel.utils.LoadingDialog
import com.company.issadminpanel.utils.NetworkManager
import com.company.issadminpanel.utils.SharedPref
import com.company.issadminpanel.utils.loadImage
import com.company.issadminpanel.utils.showErrorToast
import com.company.issadminpanel.utils.showInfoToast
import com.company.issadminpanel.utils.showSuccessToast
import com.company.issadminpanel.utils.showWarningToast
import com.company.issadminpanel.view_model.EditProfileViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth

class EditProfileFragment : Fragment() {

    //declaring variables
    private lateinit var binding: FragmentEditProfileBinding

    private lateinit var repository: EditProfileRepository
    private lateinit var viewModel: EditProfileViewModel

    private lateinit var user: UserModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        SharedPref.init(requireContext())
        LoadingDialog.init(requireContext())

        repository = EditProfileRepository()
        viewModel = ViewModelProvider(this, EditProfileViewModelFactory(repository))[EditProfileViewModel::class.java]

        //request for data
        viewModel.requestUserDetails(FirebaseAuth.getInstance().currentUser!!.uid)

        observerList()

        //hiding update button
        binding.updateButton.visibility = View.GONE

        //back button click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        //profile pic click event
        binding.profilePicImageview.setOnClickListener {
            ImagePicker.with(this)
                .crop(5f, 5f)
                .compress(800)         //Final image size will be less than 0.8 MB(Optional)
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
        }

        //update button click event
        binding.updateButton.setOnClickListener { updateProfile() }

        return binding.root
    }

    private fun observerList() {
        viewModel.userDetailsLiveData.observe(viewLifecycleOwner) {
            it?.let {
                user = it
                if (it.profile_pic_url != null) {
                    binding.profilePicImageview.loadImage(it.profile_pic_url)
                    SharedPref.write("PROFILE_PIC_URL", it.profile_pic_url!!)
                } else {
                    binding.profilePicImageview.loadImage(R.drawable.user_profile_pic_placeholder_image)
                }
                binding.nameEdittext.setText(it.name)
                SharedPref.write("USER_NAME", it.name.toString())
                binding.mobileNumberEdittext.setText(it.mobile_number)
                binding.specialistInEdittext.setText(it.specialist_in)
                binding.bioEdittext.setText(it.bio)

                if (it.user_type == "LECTURER") {
                    binding.bioCardView.visibility = View.VISIBLE
                }
                if (it.user_type == "DOCTOR") {
                    binding.specialistInCardView.visibility = View.VISIBLE
                    binding.bioCardView.visibility = View.VISIBLE
                }
                binding.updateButton.visibility = View.VISIBLE
            }
        }

        viewModel.toastMessageLiveData.observe(viewLifecycleOwner) {
            it?.let {
                when (it) {
                    "Updated" -> {
                        requireContext().showSuccessToast(it)
                    }
                    else -> requireContext().showErrorToast(it)
                }

                viewModel.resetToastMessage()
            }

            LoadingDialog.dismiss()
        }
    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val fileUri = data?.data!!
                    binding.profilePicImageview.loadImage(fileUri)
                    user.profile_pic_url = fileUri.toString()
                }
                ImagePicker.RESULT_ERROR -> {
                    requireContext().showErrorToast(ImagePicker.getError(data))
                }
                else -> {
                    requireContext().showInfoToast("Cancelled")
                }
            }
        }

    private fun updateProfile() {
        if (binding.nameEdittext.text.toString().trim().isEmpty()) {
            binding.nameEdittext.error = "Enter your name"
            binding.nameEdittext.requestFocus()
            return
        }
        if (binding.nameEdittext.text.toString().trim().contains("0") or binding.nameEdittext.text.toString().trim().contains("1") or binding.nameEdittext.text.toString().trim().contains("2") or binding.nameEdittext.text.toString().trim().contains("3") or binding.nameEdittext.text.toString().trim().contains("4") or binding.nameEdittext.text.toString().trim().contains("5") or binding.nameEdittext.text.toString().trim().contains("6") or binding.nameEdittext.text.toString().trim().contains("7") or binding.nameEdittext.text.toString().trim().contains("8") or binding.nameEdittext.text.toString().trim().contains("9")) {
            binding.nameEdittext.error = "Number not allowed"
            binding.nameEdittext.requestFocus()
            return
        }
        if (binding.mobileNumberEdittext.text.toString().trim().isEmpty()) {
            binding.mobileNumberEdittext.error = "Enter mobile number"
            binding.mobileNumberEdittext.requestFocus()
            return
        }

        if (user.user_type == "LECTURER" || user.user_type == "DOCTOR") {
            if (binding.bioEdittext.text.toString().trim().isEmpty()) {
                binding.bioEdittext.error = "Enter experience"
                binding.bioEdittext.requestFocus()
                return
            } else {
                user.bio = binding.bioEdittext.text.toString().trim()
            }
        }

        if (user.user_type == "DOCTOR") {
            if (binding.specialistInEdittext.text.toString().trim().isEmpty()) {
                binding.specialistInEdittext.error = "Enter Details"
                binding.specialistInEdittext.requestFocus()
                return
            } else {
                user.specialist_in = binding.specialistInEdittext.text.toString().trim()
            }
        }

        user.name = binding.nameEdittext.text.toString().trim()
        user.mobile_number = binding.mobileNumberEdittext.text.toString().trim()

        if (NetworkManager.isInternetAvailable(requireContext())) {
            LoadingDialog.setText("Updating profile...")
            LoadingDialog.show()

            viewModel.updateProfile(user)
        } else {
            requireContext().showWarningToast("No internet available")
        }
    }
}





class EditProfileViewModelFactory(private val repository: EditProfileRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = EditProfileViewModel(repository) as T
}