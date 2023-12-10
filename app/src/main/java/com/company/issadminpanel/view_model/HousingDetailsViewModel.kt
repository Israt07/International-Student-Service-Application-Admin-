package com.company.issadminpanel.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.company.issadminpanel.model.HousingImageModel
import com.company.issadminpanel.model.HousingModel
import com.company.issadminpanel.repository.HousingDetailsRepository

class HousingDetailsViewModel(private val repository: HousingDetailsRepository) : ViewModel() {
    private val _toastMessageLiveData = MutableLiveData<String?>()
    val toastMessageLiveData: LiveData<String?>
        get() = _toastMessageLiveData

    fun resetToastMessage() {
        _toastMessageLiveData.postValue(null)
    }

    private val _housingDetailsLiveData = MutableLiveData<HousingModel?>()
    val housingDetailsLiveData: LiveData<HousingModel?>
        get() = _housingDetailsLiveData

    fun requestHousingDetails(title: String) {
        repository.getHousingDetails(title, _housingDetailsLiveData)
    }

    private val _housingImagesLiveData = MutableLiveData<ArrayList<HousingImageModel>?>()
    val housingImagesLiveData: LiveData<ArrayList<HousingImageModel>?>
        get() = _housingImagesLiveData

    fun requestHousingImages(title: String) {
        repository.getHousingImages(title, _housingImagesLiveData)
    }

    fun updateHousingDetails(housing: HousingModel?) {
        repository.updateHousingDetails(housing, _toastMessageLiveData)
    }
}