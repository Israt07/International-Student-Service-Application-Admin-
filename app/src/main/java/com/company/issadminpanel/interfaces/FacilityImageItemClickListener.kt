package com.company.issadminpanel.interfaces

import com.company.issadminpanel.model.FacilityImageModel

interface FacilityImageItemClickListener {
    fun onImageItemClick(currentItem: FacilityImageModel, clickedOn: String)
}