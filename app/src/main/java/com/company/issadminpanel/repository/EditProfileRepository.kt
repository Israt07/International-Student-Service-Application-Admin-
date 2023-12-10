package com.company.issadminpanel.repository

import androidx.lifecycle.MutableLiveData
import com.company.issadminpanel.model.UserModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class EditProfileRepository {
    fun getUserDetails(userId: String, liveData: MutableLiveData<UserModel?>) {
        Firebase.database.reference.child("users").child(userId).addValueEventListener(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userModel = snapshot.getValue(UserModel::class.java)
                liveData.postValue(userModel)
            }

            override fun onCancelled(error: DatabaseError) {
                liveData.postValue(null)
            }
        })
    }

    fun updateProfile(user: UserModel?, toastMessageLiveData: MutableLiveData<String?>) {
        Firebase.database.reference.child("users").child(user?.user_id.toString())
            .setValue(user).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    toastMessageLiveData.postValue("Updated")
                } else {
                    toastMessageLiveData.postValue(task.exception?.localizedMessage)
                }
            }
    }
}