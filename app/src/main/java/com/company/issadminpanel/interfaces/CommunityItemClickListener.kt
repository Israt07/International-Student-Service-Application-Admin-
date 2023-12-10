package com.company.issadminpanel.interfaces

import com.company.issadminpanel.model.CommunityModel

interface CommunityItemClickListener {
    fun onItemClick(currentItem: CommunityModel, clickedOn: String)
}