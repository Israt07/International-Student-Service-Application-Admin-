package com.company.issadminpanel.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.company.issadminpanel.model.UserModel
import com.company.issadminpanel.repository.UserRepository

class UserViewModel(private val repository: UserRepository) : ViewModel() {
    private val _userListLiveData = MutableLiveData<ArrayList<UserModel>?>()
    val userListLiveData: LiveData<ArrayList<UserModel>?>
        get() = _userListLiveData

    fun requestUserList(userType: String?) = repository.getUserList(userType, _userListLiveData)
}