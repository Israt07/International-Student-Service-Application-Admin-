package com.company.issadminpanel.view.chatActivity

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
import com.company.issadminpanel.adapter.ChatListAdapter
import com.company.issadminpanel.databinding.FragmentChatListBinding
import com.company.issadminpanel.interfaces.ChatItemClickListener
import com.company.issadminpanel.model.ChatModel
import com.company.issadminpanel.repository.ChatListRepository
import com.company.issadminpanel.utils.SharedPref
import com.company.issadminpanel.view_model.ChatListViewModel

class ChatListFragment : Fragment(), ChatItemClickListener {

    //Declaring variables
    private lateinit var binding: FragmentChatListBinding

    private lateinit var repository: ChatListRepository
    private lateinit var viewModel: ChatListViewModel

    private var chatList = ArrayList<ChatModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChatListBinding.inflate(inflater, container, false)
        SharedPref.init(requireContext())

        repository = ChatListRepository()
        viewModel = ViewModelProvider(this, ChatListViewModelFactory(repository))[ChatListViewModel::class.java]

        //request for data
        viewModel.requestChatList(SharedPref.read("USER_ID", "").toString())

        observerList()

        //back button click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        //set recyclerview adapter
        binding.chatListRecyclerview.adapter = ChatListAdapter(chatList,  this)

        return binding.root
    }

    private fun observerList() {
        viewModel.chatListLiveData.observe(viewLifecycleOwner) {
            chatList.clear()
            if (it != null) {
                //adding all the unique student value
                chatList.addAll(it.distinctBy { item -> item.student_id })

                chatList.reverse()

                binding.chatListRecyclerview.adapter?.notifyDataSetChanged()

                binding.nothingAvailableTextview.visibility = View.GONE
                binding.chatListRecyclerview.visibility = View.VISIBLE
            } else {
                binding.chatListRecyclerview.visibility = View.GONE
                binding.nothingAvailableTextview.visibility = View.VISIBLE
            }
            binding.progressbar.visibility = View.GONE
        }
    }

    override fun onItemClick(currentItem: ChatModel) {
        val bundle = bundleOf(
            "STUDENT_ID" to currentItem.student_id,
            "STUDENT_NAME" to currentItem.student_name
        )
        findNavController().navigate(R.id.action_chatListFragment_to_singleChatFragment,bundle)
    }
}




class ChatListViewModelFactory(private val repository: ChatListRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = ChatListViewModel(repository) as T
}