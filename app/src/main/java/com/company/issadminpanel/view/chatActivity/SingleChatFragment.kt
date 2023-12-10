package com.company.issadminpanel.view.chatActivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.company.issadminpanel.adapter.SingleChatAdapter
import com.company.issadminpanel.databinding.FragmentSingleChatBinding
import com.company.issadminpanel.model.ChatModel
import com.company.issadminpanel.repository.SingleChatRepository
import com.company.issadminpanel.utils.NetworkManager
import com.company.issadminpanel.utils.SharedPref
import com.company.issadminpanel.utils.showErrorToast
import com.company.issadminpanel.utils.showWarningToast
import com.company.issadminpanel.view_model.SingleChatViewModel
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SingleChatFragment : Fragment() {

    //Declaring variables
    private lateinit var binding: FragmentSingleChatBinding

    private lateinit var repository: SingleChatRepository
    private lateinit var viewModel: SingleChatViewModel

    private var studentId = ""
    private var studentName = ""

    private var chatList = ArrayList<ChatModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSingleChatBinding.inflate(inflater, container, false)
        SharedPref.init(requireContext())

        studentId = arguments?.getString("STUDENT_ID").toString()
        studentName = arguments?.getString("STUDENT_NAME").toString()

        //set title text
        binding.titleTextview.text = studentName

        repository = SingleChatRepository()
        viewModel = ViewModelProvider(this, SingleChatViewModelFactory(repository))[SingleChatViewModel::class.java]

        //request for data
        viewModel.requestChatList(studentId)

        observerList()

        //back button click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        //set recyclerview adapter
        binding.chatListRecyclerview.adapter = SingleChatAdapter(chatList)

        //send button click event
        binding.sendButton.setOnClickListener { sendMessage() }

        return binding.root
    }

    private fun observerList() {
        viewModel.chatListLiveData.observe(viewLifecycleOwner) {
            chatList.clear()
            if (it != null) {
                chatList.addAll(it.filter { item -> item.doctor_id == SharedPref.read("USER_ID", "").toString() })

                binding.chatListRecyclerview.adapter?.notifyDataSetChanged()

                binding.chatListRecyclerview.visibility = View.VISIBLE

                //scroll to end of the screen
                binding.chatListRecyclerview.scrollToPosition(binding.chatListRecyclerview.adapter?.itemCount!! - 1)
            } else {
                binding.chatListRecyclerview.visibility = View.GONE
            }
            binding.progressbar.visibility = View.GONE
            binding.mainLayout.visibility = View.VISIBLE


        }
    }

    private fun sendMessage() {
        if (binding.messageEdittext.text.toString().trim().isEmpty()) {
            requireContext().showWarningToast("Enter message")
            return
        }

        if (NetworkManager.isInternetAvailable(requireContext())) {

            val chatId = Firebase.database.reference.child("chats").push().key.toString()

            val chatModel = ChatModel(chatId, studentId, SharedPref.read("USER_ID", "").toString(), studentName, SharedPref.read("USER_NAME", "").toString(), binding.messageEdittext.text.toString().trim(), SharedPref.read("USER_ID", "").toString())

            Firebase.database.reference.child("chats").child(chatId)
                .setValue(chatModel).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        binding.messageEdittext.setText("")
                    } else {
                        requireContext().showErrorToast("Something wrong.")
                    }
                }
        } else {
            requireContext().showWarningToast("No internet available")
        }
    }
}




class SingleChatViewModelFactory(private val repository: SingleChatRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = SingleChatViewModel(repository) as T
}