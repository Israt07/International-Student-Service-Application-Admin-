package com.company.issadminpanel.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.company.issadminpanel.model.AppointmentModel
import com.company.issadminpanel.repository.ManageAppointmentRepository

class ManageAppointmentViewModel(private val repository: ManageAppointmentRepository) : ViewModel() {
    private val _pendingAppointmentListLiveData = MutableLiveData<ArrayList<AppointmentModel>?>()
    val pendingAppointmentListLiveData: LiveData<ArrayList<AppointmentModel>?>
        get() = _pendingAppointmentListLiveData

    fun requestPendingAppointmentsList(lecturerName: String) {
        repository.getPendingAppointmentsList(lecturerName, _pendingAppointmentListLiveData)
    }
}