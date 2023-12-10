package com.company.issadminpanel.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.company.issadminpanel.model.AppointmentModel
import com.company.issadminpanel.repository.ViewAppointmentRepository

class ViewAppointmentViewModel(private val repository: ViewAppointmentRepository) : ViewModel() {
    private val _appointmentListLiveData = MutableLiveData<ArrayList<AppointmentModel>?>()
    val appointmentListLiveData: LiveData<ArrayList<AppointmentModel>?>
        get() = _appointmentListLiveData

    fun requestAppointmentsList(lecturerName: String) {
        repository.getAppointmentsList(lecturerName, _appointmentListLiveData)
    }
}