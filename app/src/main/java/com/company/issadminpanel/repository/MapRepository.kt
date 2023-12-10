package com.company.issadminpanel.repository

import androidx.lifecycle.MutableLiveData
import com.company.issadminpanel.model.MapModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MapRepository {
    fun getMapList(liveData: MutableLiveData<ArrayList<MapModel>?>) {
        Firebase.database.reference.child("maps").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val mapList = ArrayList<MapModel>()
                    for (dataSnapshot in snapshot.children) {
                        val mapModel = dataSnapshot.getValue(MapModel::class.java)
                        mapList.add(mapModel!!)
                    }
                    liveData.postValue(mapList)
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