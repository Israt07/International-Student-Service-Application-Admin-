package com.company.issadminpanel.repository

import androidx.lifecycle.MutableLiveData
import com.company.issadminpanel.model.HousingBookingModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ManageBookingRepository {
    fun getBookingList(liveData: MutableLiveData<ArrayList<HousingBookingModel>?>) {
        Firebase.database.reference.child("services").child("housing").child("bookings").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val itemList = ArrayList<HousingBookingModel>()
                    for (dataSnapshot in snapshot.children) {
                        val itemModel = dataSnapshot.getValue(HousingBookingModel::class.java)
                        if (itemModel?.booking_status != "rejected") {
                            itemList.add(itemModel!!)
                        }
                    }
                    liveData.postValue(itemList)
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