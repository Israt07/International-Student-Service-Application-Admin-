package com.company.issadminpanel.interfaces

import com.company.issadminpanel.model.PaymentModel

interface PaymentItemClickListener {
    fun onItemClick(currentItem: PaymentModel)
}