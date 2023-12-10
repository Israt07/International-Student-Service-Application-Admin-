package com.company.issadminpanel.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.company.issadminpanel.model.NotificationModel
import com.company.issadminpanel.repository.NotificationRepository

class NotificationViewModel(private val repository: NotificationRepository) : ViewModel() {
    private val _notificationListLiveData = MutableLiveData<ArrayList<NotificationModel>?>()
    val notificationListLiveData: LiveData<ArrayList<NotificationModel>?>
        get() = _notificationListLiveData

    fun requestNotificationList() {
        repository.getNotificationList(_notificationListLiveData)
    }
}