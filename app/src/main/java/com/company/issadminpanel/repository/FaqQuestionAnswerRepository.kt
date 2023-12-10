package com.company.issadminpanel.repository

import androidx.lifecycle.MutableLiveData
import com.company.issadminpanel.model.FaqModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FaqQuestionAnswerRepository {
    fun getFaqList(title: String, liveData: MutableLiveData<ArrayList<FaqModel>?>) {
        Firebase.database.reference.child("faq").orderByChild("title").equalTo(title).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val itemList = ArrayList<FaqModel>()
                    for (dataSnapshot in snapshot.children) {
                        val itemModel = dataSnapshot.getValue(FaqModel::class.java)
                        itemList.add(itemModel!!)
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