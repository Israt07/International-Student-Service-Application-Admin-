package com.company.issadminpanel.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.company.issadminpanel.model.UserModel
import com.company.issadminpanel.repository.ReportRepository
import com.company.issadminpanel.repository.StudentListRepository
import com.company.issadminpanel.repository.UserRepository

class StudentListViewModel(private val repository: StudentListRepository) : ViewModel() {
    private val _userListLiveData = MutableLiveData<ArrayList<UserModel>?>()
    val userListLiveData: LiveData<ArrayList<UserModel>?>
        get() = _userListLiveData

    fun requestUserList() = repository.getUserList(_userListLiveData)
}