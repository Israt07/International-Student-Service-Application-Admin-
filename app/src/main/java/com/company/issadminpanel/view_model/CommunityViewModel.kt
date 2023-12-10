package com.company.issadminpanel.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.company.issadminpanel.model.CommunityModel
import com.company.issadminpanel.repository.CommunityRepository

class CommunityViewModel(private val repository: CommunityRepository) : ViewModel() {
    private val _communityListLiveData = MutableLiveData<ArrayList<CommunityModel>?>()
    val communityListLiveData: LiveData<ArrayList<CommunityModel>?>
        get() = _communityListLiveData

    fun requestCommunityList() {
        repository.getCommunityList(_communityListLiveData)
    }
}