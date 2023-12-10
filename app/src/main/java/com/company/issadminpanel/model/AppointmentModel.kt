package com.company.issadminpanel.model

data class AppointmentModel(
    val appointment_id: String? = null,
    val user_id: String? = null,
    var name: String? = null,
    val matric_number: String? = null,
    val email: String? = null,
    val faculty: String? = null,
    val lecturer: String? = null,
    var date: String? = null,
    var time: String? = null,
    var appointment_status: String? = null,
    var rejection_reason: String? = null
)
