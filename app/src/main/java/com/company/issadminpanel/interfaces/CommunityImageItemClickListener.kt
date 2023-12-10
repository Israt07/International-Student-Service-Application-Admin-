package com.company.issadminpanel.interfaces

import com.company.issadminpanel.model.CommunityImageModel

interface CommunityImageItemClickListener {
    fun onImageItemClick(currentItem: CommunityImageModel, clickedOn: String)
}