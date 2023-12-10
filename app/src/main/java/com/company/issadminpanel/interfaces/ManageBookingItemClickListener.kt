package com.company.issadminpanel.interfaces

import com.company.issadminpanel.model.HousingBookingModel

interface ManageBookingItemClickListener {
    fun onViewButtonClick(currentItem: HousingBookingModel)
}