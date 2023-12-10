package com.company.issadminpanel.repository

import androidx.lifecycle.MutableLiveData
import com.company.issadminpanel.model.AppointmentModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ViewAppointmentRepository {
    fun getAppointmentsList(lecturerName: String, liveData: MutableLiveData<ArrayList<AppointmentModel>?>) {
        Firebase.database.reference.child("appointments").orderByChild("lecturer").equalTo(lecturerName).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val appointmentList = ArrayList<AppointmentModel>()
                    for (dataSnapshot in snapshot.children) {
                        val appointmentModel = dataSnapshot.getValue(AppointmentModel::class.java)
                        if (appointmentModel?.appointment_status == "approved") {
                            appointmentList.add(appointmentModel)
                        }
                    }
                    liveData.postValue(appointmentList)
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