package com.company.issadminpanel.view.profileActivity

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.company.issadminpanel.R
import com.company.issadminpanel.databinding.FragmentProfileBinding
import com.company.issadminpanel.repository.ProfileRepository
import com.company.issadminpanel.utils.SharedPref
import com.company.issadminpanel.utils.loadImage
import com.company.issadminpanel.utils.showSuccessToast
import com.company.issadminpanel.view_model.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    //declaring variables
    private lateinit var binding: FragmentProfileBinding

    private lateinit var repository: ProfileRepository
    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        SharedPref.init(requireContext())

        repository = ProfileRepository()
        viewModel = ViewModelProvider(this, ProfileViewModelFactory(repository))[ProfileViewModel::class.java]

        //request for data
        viewModel.requestUserDetails(FirebaseAuth.getInstance().currentUser!!.uid)

        observerList()

        //back button click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        //edit profile icon click event
        binding.editProfileIcon.setOnClickListener { findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment) }

        //logout button click event
        binding.logoutButton.setOnClickListener { logout() }

        return binding.root
    }

    private fun observerList() {
        viewModel.userDetailsLiveData.observe(viewLifecycleOwner) {
            it?.let {
                if (it.profile_pic_url != null) {
                    binding.profilePicImageview.loadImage(it.profile_pic_url)
                    SharedPref.write("PROFILE_PIC_URL", it.profile_pic_url!!)
                } else {
                    binding.profilePicImageview.loadImage(R.drawable.user_profile_pic_placeholder_image)
                }

                //set details
                binding.userNameTextview.text = it.name
                SharedPref.write("USER_NAME", it.name.toString())
                binding.emailTextview.text = it.email
                binding.matricNumberTextview.text = it.matric_number
                binding.mobileNumberTextview.text = it.mobile_number
                if (it.user_type == "LECTURER") {
                    binding.facultySectionLayout.visibility = View.VISIBLE
                    binding.bioSectionLayout.visibility = View.VISIBLE
                }
                if (it.user_type == "DOCTOR") {
                    binding.specialistInSectionLayout.visibility = View.VISIBLE
                    binding.bioSectionLayout.visibility = View.VISIBLE
                }
                binding.facultyTextview.text = it.faculty
                binding.bioTextview.text = it.bio
                binding.specialistInTextview.text = it.specialist_in
                binding.countryTextview.text = it.country
            }
        }
    }

    private fun logout() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.app_name)
        builder.setIcon(R.mipmap.ic_launcher)
        builder.setMessage("Do you really want to logout")
        builder.setPositiveButton("Yes") { _, _ ->
            FirebaseAuth.getInstance().signOut()
            requireContext().showSuccessToast("Logged out")
            findNavController().navigate(R.id.action_profileFragment_to_splashFragment)
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }
}




class ProfileViewModelFactory(private val repository: ProfileRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = ProfileViewModel(repository) as T
}