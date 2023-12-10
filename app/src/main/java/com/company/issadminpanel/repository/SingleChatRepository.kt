package com.company.issadminpanel.repository

import androidx.lifecycle.MutableLiveData
import com.company.issadminpanel.model.ChatModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SingleChatRepository {
    fun getChatList(studentId: String, liveData: MutableLiveData<ArrayList<ChatModel>?>) {
        Firebase.database.reference.child("chats").orderByChild("student_id").equalTo(studentId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val itemList = ArrayList<ChatModel>()
                    for (dataSnapshot in snapshot.children) {
                        val itemModel = dataSnapshot.getValue(ChatModel::class.java)
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