package com.company.issadminpanel.repository

import androidx.lifecycle.MutableLiveData
import com.company.issadminpanel.model.NotificationModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class NotificationRepository {
    fun getNotificationList(liveData: MutableLiveData<ArrayList<NotificationModel>?>) {
        Firebase.database.reference.child("notifications").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val notificationList = ArrayList<NotificationModel>()
                    for (dataSnapshot in snapshot.children) {
                        val notificationModel = dataSnapshot.getValue(NotificationModel::class.java)
                        notificationList.add(notificationModel!!)
                    }
                    liveData.postValue(notificationList)
                } else {
                    liveData.postValue(null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                liveData.postValue(null)
            }
        })
    }
}