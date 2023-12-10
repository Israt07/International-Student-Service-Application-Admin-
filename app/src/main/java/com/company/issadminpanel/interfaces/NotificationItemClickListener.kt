package com.company.issadminpanel.interfaces

import com.company.issadminpanel.model.NotificationModel

interface NotificationItemClickListener {
    fun onItemClick(currentItem: NotificationModel, clickedOn: String)
}