package com.company.issadminpanel.model

data class UserModel(
    val user_id: String? = null,
    var name: String? = null,
    var gender: String? = null,
    var matric_number: String? = null,
    val email: String? = null,
    var profile_pic_url: String? = null,
    val user_type: String? = null,
    var mobile_number: String? = null,
    var faculty: String? = null,
    var course: String? = null,
    var date_of_birth: String? = null,
    val country: String? = null,
    val passport_number: String? = null,
    var bio: String? = null,
    var specialist_in: String? = null
)