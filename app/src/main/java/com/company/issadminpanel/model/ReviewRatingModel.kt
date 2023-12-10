package com.company.issadminpanel.model

data class ReviewRatingModel(
    val reviewer_id: String? = null,
    val reviewer_name: String? = null,
    val review_time: String? = null,
    val rating: String? = "0.0",
    val review: String? = null
)
