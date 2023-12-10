package com.company.issadminpanel.view.usersActivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.company.issadminpanel.R
import com.company.issadminpanel.databinding.FragmentUserDetailsBinding
import com.company.issadminpanel.model.UserModel
import com.company.issadminpanel.repository.UserDetailsRepository
import com.company.issadminpanel.view_model.UserDetailsViewModel

class UserDetailsFragment : Fragment() {

    //Declaring variables
    private lateinit var binding: FragmentUserDetailsBinding

    private lateinit var repository: UserDetailsRepository
    private lateinit var viewModel: UserDetailsViewModel

    private lateinit var user: UserModel
    private var userId = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUserDetailsBinding.inflate(inflater, container, false)

        userId = arguments?.getString("USER_ID").toString()

        repository = UserDetailsRepository()
        viewModel = ViewModelProvider(this, UserDetailsViewModelFactory(repository))[UserDetailsViewModel::class.java]

        //request for data
        viewModel.requestUserDetails(userId)

        observerList()

        //back button click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        //edit button click event
        binding.editButton.setOnClickListener {
            val bundle = bundleOf(
                "USER_ID" to user.user_id,
                "NAME" to user.name,
                "GENDER" to user.name,
                "MATRIC_NUMBER" to user.matric_number,
                "EMAIL" to user.email,
                "PROFILE_PIC_URL" to user.profile_pic_url,
                "USER_TYPE" to user.user_type,
                "MOBILE_NUMBER" to user.mobile_number,
                "FACULTY" to user.faculty,
                "COURSE" to user.course,
                "DATE_OF_BIRTH" to user.date_of_birth,
                "COUNTRY" to user.country,
                "PASSPORT_NUMBER" to user.passport_number
            )
            findNavController().navigate(R.id.action_userDetailsFragment_to_editUserFragment, bundle)
        }

        return binding.root
    }

    private fun observerList() {
        viewModel.userDetailsLiveData.observe(viewLifecycleOwner) {
            it?.let {
                user = it
                binding.nameTextview.text = it.name
                binding.emailTextview.text = it.email
                binding.genderTextview.text = it.gender
                binding.mobileNumberTextview.text = it.mobile_number
                binding.matricNumberTextview.text = it.matric_number
                binding.facultyTextview.text = it.faculty
                binding.courseTextview.text = it.course
                binding.dateOfBirthTextview.text = it.date_of_birth
                binding.countryTextview.text = it.country
                binding.passportNumberTextview.text = it.passport_number

                binding.progressbar.visibility = View.GONE
                binding.mainLayout.visibility = View.VISIBLE
                binding.editButton.visibility = View.VISIBLE
            }
        }
    }
}





class UserDetailsViewModelFactory(private val repository: UserDetailsRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = UserDetailsViewModel(repository) as T
}