package com.company.issadminpanel.interfaces

import com.company.issadminpanel.model.HandbookModel

interface HandbookItemClickListener {
    fun onItemClick(currentItem: HandbookModel, clickedOn: String)
}