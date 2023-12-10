package com.company.issadminpanel.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.company.issadminpanel.model.VisaRenewModel
import com.company.issadminpanel.repository.VisaRenewRepository

class VisaRenewViewModel(private val repository: VisaRenewRepository) : ViewModel() {
    private val _visaListLiveData = MutableLiveData<ArrayList<VisaRenewModel>?>()
    val visaListLiveData: LiveData<ArrayList<VisaRenewModel>?>
        get() = _visaListLiveData

    fun requestVisaList() {
        repository.getVisaList(_visaListLiveData)
    }
}