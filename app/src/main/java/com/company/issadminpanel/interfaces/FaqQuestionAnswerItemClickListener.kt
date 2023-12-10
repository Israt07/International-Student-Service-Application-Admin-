package com.company.issadminpanel.interfaces

import com.company.issadminpanel.model.FaqModel

interface FaqQuestionAnswerItemClickListener {
    fun onItemClick(currentItem: FaqModel, clickedOn: String)
}