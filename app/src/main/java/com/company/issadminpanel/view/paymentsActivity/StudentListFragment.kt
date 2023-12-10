package com.company.issadminpanel.view.paymentsActivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.company.issadminpanel.R
import com.company.issadminpanel.adapter.UserAdapter
import com.company.issadminpanel.databinding.FragmentStudentListBinding
import com.company.issadminpanel.interfaces.UserItemClickListener
import com.company.issadminpanel.model.UserModel
import com.company.issadminpanel.repository.StudentListRepository
import com.company.issadminpanel.view_model.StudentListViewModel

class StudentListFragment : Fragment(), UserItemClickListener {

    private lateinit var binding: FragmentStudentListBinding

    private lateinit var repository: StudentListRepository
    private lateinit var viewModel: StudentListViewModel

    private var userList = ArrayList<UserModel>()
    private lateinit var userAdapter: UserAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentStudentListBinding.inflate(inflater, container, false)

        repository = StudentListRepository()
        viewModel = ViewModelProvider(this, StudentListViewModelFactory(repository))[StudentListViewModel::class.java]

        //request for data
        viewModel.requestUserList()

        observerList()

        //back button click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        //set recyclerview adapter
        userAdapter = UserAdapter(userList,  this)

        binding.userRecyclerview.adapter = userAdapter

        //search
        binding.matricNumberEdittext.doOnTextChanged { text, _, _, _ ->
            if (text.toString() == "") {
                userAdapter.submitList(userList)
            } else {
                searchUser(text.toString())
            }
        }

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

    private fun searchUser(query: String) {
        val filteredMapList = userList.filter { it.matric_number?.contains(query, ignoreCase = true) == true }
        userAdapter.submitList(ArrayList(filteredMapList))
    }

    override fun onItemClick(currentItem: UserModel) {
        val bundle = bundleOf(
            "DATA" to currentItem.user_id
        )
        findNavController().navigate(R.id.action_studentListFragment_to_paymentsFragment, bundle)
    }
}




class StudentListViewModelFactory(private val repository: StudentListRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = StudentListViewModel(repository) as T
}