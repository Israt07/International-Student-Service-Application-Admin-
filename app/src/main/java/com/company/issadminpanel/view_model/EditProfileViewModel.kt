package com.company.issadminpanel.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.company.issadminpanel.model.UserModel
import com.company.issadminpanel.repository.EditProfileRepository

class EditProfileViewModel(private val repository: EditProfileRepository) : ViewModel() {
    private val _toastMessageLiveData = MutableLiveData<String?>()
    val toastMessageLiveData: LiveData<String?>
        get() = _toastMessageLiveData

    fun resetToastMessage() {
        _toastMessageLiveData.postValue(null)
    }

    private val _userDetailsLiveData = MutableLiveData<UserModel?>()
    val userDetailsLiveData: LiveData<UserModel?>
        get() = _userDetailsLiveData

    fun requestUserDetails(userId: String) {
        repository.getUserDetails(userId, _userDetailsLiveData)
    }

    fun updateProfile(user: UserModel?) {
        repository.updateProfile(user, _toastMessageLiveData)
    }
}