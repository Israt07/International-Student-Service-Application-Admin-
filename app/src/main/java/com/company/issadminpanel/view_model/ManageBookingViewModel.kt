package com.company.issadminpanel.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.company.issadminpanel.model.HousingBookingModel
import com.company.issadminpanel.repository.ManageBookingRepository

class ManageBookingViewModel(private val repository: ManageBookingRepository) : ViewModel() {
    private val _bookingListLiveData = MutableLiveData<ArrayList<HousingBookingModel>?>()
    val bookingListLiveData: LiveData<ArrayList<HousingBookingModel>?>
        get() = _bookingListLiveData

    fun requestBookingList() {
        repository.getBookingList(_bookingListLiveData)
    }
}