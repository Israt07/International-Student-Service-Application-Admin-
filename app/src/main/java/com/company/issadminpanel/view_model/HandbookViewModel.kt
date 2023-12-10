package com.company.issadminpanel.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.company.issadminpanel.model.HandbookModel
import com.company.issadminpanel.repository.HandbookRepository

class HandbookViewModel(private val repository: HandbookRepository) : ViewModel() {
    private val _handbookListLiveData = MutableLiveData<ArrayList<HandbookModel>?>()
    val handbookListLiveData: LiveData<ArrayList<HandbookModel>?>
        get() = _handbookListLiveData

    fun requestHandbookList() {
        repository.getHandbookList(_handbookListLiveData)
    }
}