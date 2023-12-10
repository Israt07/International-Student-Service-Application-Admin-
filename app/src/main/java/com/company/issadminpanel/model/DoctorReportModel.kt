package com.company.issadminpanel.model

data class DoctorReportModel(
    val report_id: String? = null,
    var student_name: String? = null,
    var student_id: String? = null,
    var student_age: String? = null,
    var student_gender: String? = null,
    var student_mobile_number: String? = null,
    var date_of_report: String? = null,
    var medical_history: String? = null,
    var current_symptoms: String? = null,
    var medication_name: String? = null,
    var dosage: String? = null,
    var frequency: String? = null,
    var test_name: String? = null,
    var results: String? = null,
    var diagnosis: String? = null,
    var prescription: String? = null,
    var doctors_note: String? = null,
    var doctor_name: String? = null,
    var doctor_id: String? = null
)
