package com.company.issadminpanel.interfaces

import com.company.issadminpanel.model.MapModel

interface MapItemClickListener {
    fun onItemClick(currentMap: MapModel, clickedOn: String)
}