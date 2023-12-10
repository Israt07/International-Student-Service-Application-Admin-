package com.company.issadminpanel.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.company.issadminpanel.model.MapModel
import com.company.issadminpanel.repository.MapRepository

class MapViewModel(private val repository: MapRepository) : ViewModel() {
    private val _mapListLiveData = MutableLiveData<ArrayList<MapModel>?>()
    val mapListLiveData: LiveData<ArrayList<MapModel>?>
        get() = _mapListLiveData

    fun requestMapList() {
        repository.getMapList(_mapListLiveData)
    }
}