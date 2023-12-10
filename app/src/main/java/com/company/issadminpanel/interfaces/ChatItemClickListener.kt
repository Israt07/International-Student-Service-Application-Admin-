package com.company.issadminpanel.interfaces

import com.company.issadminpanel.model.ChatModel

interface ChatItemClickListener {
    fun onItemClick(currentItem: ChatModel)
}