package com.company.issadminpanel.view.reportActivity

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
import com.company.issadminpanel.databinding.FragmentReportBinding
import com.company.issadminpanel.interfaces.UserItemClickListener
import com.company.issadminpanel.model.UserModel
import com.company.issadminpanel.repository.ReportRepository
import com.company.issadminpanel.view_model.ReportViewModel
import com.google.gson.Gson

class ReportFragment : Fragment(), UserItemClickListener {

    private lateinit var binding: FragmentReportBinding

    private lateinit var repository: ReportRepository
    private lateinit var viewModel: ReportViewModel

    private var userList = ArrayList<UserModel>()
    private lateinit var userAdapter: UserAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentReportBinding.inflate(inflater, container, false)

        repository = ReportRepository()
        viewModel = ViewModelProvider(this, ReportViewModelFactory(repository))[ReportViewModel::class.java]

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
            "DATA" to Gson().toJson(currentItem)
        )
        findNavController().navigate(R.id.action_reportFragment_to_makeReportFragment, bundle)
    }
}




class ReportViewModelFactory(private val repository: ReportRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = ReportViewModel(repository) as T
}