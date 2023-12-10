package com.company.issadminpanel.view.usersActivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.company.issadminpanel.R
import com.company.issadminpanel.adapter.UserAdapter
import com.company.issadminpanel.databinding.FragmentUsersBinding
import com.company.issadminpanel.interfaces.UserItemClickListener
import com.company.issadminpanel.model.UserModel
import com.company.issadminpanel.repository.UserRepository
import com.company.issadminpanel.view_model.UserViewModel

class UsersFragment : Fragment(), UserItemClickListener {

    //Declaring variables
    private lateinit var binding: FragmentUsersBinding

    private lateinit var repository: UserRepository
    private lateinit var viewModel: UserViewModel

    private var userList = ArrayList<UserModel>()

    private lateinit var userTypeList: Array<String>
    private var userType = "STUDENT"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUsersBinding.inflate(inflater, container, false)

        //set details
        userTypeList = arrayOf("STUDENT", "ADMIN", "LECTURER", "DOCTOR")

        val userTypeItemAdapter = ArrayAdapter(requireContext(), R.layout.single_dropdown_item, userTypeList)
        binding.userTypeSelectAutoCompleteTextview.setAdapter(userTypeItemAdapter)

        binding.userTypeSelectAutoCompleteTextview.setOnClickListener {
            binding.userTypeSelectAutoCompleteTextview.setText("")
        }

        repository = UserRepository()
        viewModel = ViewModelProvider(this, UserViewModelFactory(repository))[UserViewModel::class.java]

        //request for data
        viewModel.requestUserList(userType)
        binding.userTypeSelectAutoCompleteTextview.doOnTextChanged { text, _, _, _ ->
            userType = text.toString()
            viewModel.requestUserList(userType)
        }

        observerList()

        //back button click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        //set recyclerview adapter
        binding.userRecyclerview.adapter = UserAdapter(userList,  this)

        return binding.root
    }

    private fun observerList() {
        viewModel.userListLiveData.observe(viewLifecycleOwner) {
            userList.clear()
            if (it != null) {
                userList.addAll(it)

                binding.userRecyclerview.adapter?.notifyDataSetChanged()

                binding.noUserAvailableTextview.visibility = View.GONE
                binding.userRecyclerview.visibility = View.VISIBLE
            } else {
                binding.userRecyclerview.visibility = View.GONE
                binding.noUserAvailableTextview.visibility = View.VISIBLE
            }
            binding.progressbar.visibility = View.GONE
        }
    }

    override fun onItemClick(currentItem: UserModel) {
        val bundle = bundleOf(
            "USER_ID" to currentItem.user_id
        )
        findNavController().navigate(R.id.action_usersFragment_to_userDetailsFragment, bundle)
    }
}



class UserViewModelFactory(private val repository: UserRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = UserViewModel(repository) as T
}