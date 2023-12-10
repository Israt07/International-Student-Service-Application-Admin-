package com.company.issadminpanel.model

data class ChatModel(
    val chat_id: String? = null,
    var student_id: String? = null,
    val doctor_id: String? = null,
    val student_name: String? = null,
    var doctor_name: String? = null,
    val message: String? = null,
    val sender_id: String? = null
)