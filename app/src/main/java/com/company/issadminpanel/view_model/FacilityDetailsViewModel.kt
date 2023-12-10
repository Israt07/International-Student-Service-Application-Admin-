package com.company.issadminpanel.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.company.issadminpanel.model.FacilityImageModel
import com.company.issadminpanel.model.FacilityModel
import com.company.issadminpanel.repository.FacilityDetailsRepository

class FacilityDetailsViewModel(private val repository: FacilityDetailsRepository) : ViewModel() {
    private val _toastMessageLiveData = MutableLiveData<String?>()
    val toastMessageLiveData: LiveData<String?>
        get() = _toastMessageLiveData

    fun resetToastMessage() {
        _toastMessageLiveData.postValue(null)
    }

    private val _facilityDetailsLiveData = MutableLiveData<FacilityModel?>()
    val facilityDetailsLiveData: LiveData<FacilityModel?>
        get() = _facilityDetailsLiveData

    fun requestFacilityDetails(title: String) {
        repository.getFacilityDetails(title, _facilityDetailsLiveData)
    }

    private val _facilityImagesLiveData = MutableLiveData<ArrayList<FacilityImageModel>?>()
    val facilityImagesLiveData: LiveData<ArrayList<FacilityImageModel>?>
        get() = _facilityImagesLiveData

    fun requestFacilityImages(title: String) {
        repository.getFacilityImages(title, _facilityImagesLiveData)
    }

    fun updateFacilityDetails(facility: FacilityModel?) {
        repository.updateFacilityDetails(facility, _toastMessageLiveData)
    }
}