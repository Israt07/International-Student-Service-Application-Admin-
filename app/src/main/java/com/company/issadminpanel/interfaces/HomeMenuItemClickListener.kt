package com.company.issadminpanel.interfaces

import com.company.issadminpanel.model.HomeMenuModel

interface HomeMenuItemClickListener {
    fun onItemClick(currentItem: HomeMenuModel)
}