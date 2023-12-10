package com.company.issadminpanel.model

data class PaymentModel(
    val id: String? = null,
    val user_id: String? = null,
    var fee_name: String? = null,
    var debit_rm: String? = null,
    var credit_rm: String? = null,
    val credit_date: String? = null,
    var status: String? = null
)
