package com.company.issadminpanel.interfaces

import com.company.issadminpanel.model.HousingImageModel

interface HousingImageItemClickListener {
    fun onImageItemClick(currentItem: HousingImageModel, clickedOn: String)
}