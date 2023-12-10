package com.company.issadminpanel.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.company.issadminpanel.model.CommunityImageModel
import com.company.issadminpanel.model.CommunityModel
import com.company.issadminpanel.repository.CommunityDetailsRepository

class CommunityDetailsViewModel(private val repository: CommunityDetailsRepository) : ViewModel() {
    private val _toastMessageLiveData = MutableLiveData<String?>()
    val toastMessageLiveData: LiveData<String?>
        get() = _toastMessageLiveData

    fun resetToastMessage() {
        _toastMessageLiveData.postValue(null)
    }

    private val _communityDetailsLiveData = MutableLiveData<CommunityModel?>()
    val communityDetailsLiveData: LiveData<CommunityModel?>
        get() = _communityDetailsLiveData

    fun requestCommunityDetails(title: String) {
        repository.getCommunityDetails(title, _communityDetailsLiveData)
    }

    private val _communityImagesLiveData = MutableLiveData<ArrayList<CommunityImageModel>?>()
    val communityImagesLiveData: LiveData<ArrayList<CommunityImageModel>?>
        get() = _communityImagesLiveData

    fun requestCommunityImages(title: String) {
        repository.getCommunityImages(title, _communityImagesLiveData)
    }

    fun updateCommunityDetails(community: CommunityModel?) {
        repository.updateCommunityDetails(community, _toastMessageLiveData)
    }
}