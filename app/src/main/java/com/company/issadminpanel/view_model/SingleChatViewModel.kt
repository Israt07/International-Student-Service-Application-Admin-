package com.company.issadminpanel.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.company.issadminpanel.model.ChatModel
import com.company.issadminpanel.repository.SingleChatRepository

class SingleChatViewModel(private val repository: SingleChatRepository) : ViewModel() {
    private val _chatListLiveData = MutableLiveData<ArrayList<ChatModel>?>()
    val chatListLiveData: LiveData<ArrayList<ChatModel>?>
        get() = _chatListLiveData

    fun requestChatList(studentId: String) = repository.getChatList(studentId, _chatListLiveData)
}