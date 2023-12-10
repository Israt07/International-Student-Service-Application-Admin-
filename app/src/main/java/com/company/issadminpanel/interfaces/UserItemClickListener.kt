package com.company.issadminpanel.interfaces

import com.company.issadminpanel.model.UserModel

interface UserItemClickListener {
    fun onItemClick(currentItem: UserModel)
}