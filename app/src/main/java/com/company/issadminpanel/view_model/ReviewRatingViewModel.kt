package com.company.issadminpanel.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.company.issadminpanel.model.ReviewRatingModel
import com.company.issadminpanel.repository.ReviewRatingRepository

class ReviewRatingViewModel(private val repository: ReviewRatingRepository) : ViewModel() {
    private val _reviewListLiveData = MutableLiveData<ArrayList<ReviewRatingModel>?>()
    val reviewListLiveData: LiveData<ArrayList<ReviewRatingModel>?>
        get() = _reviewListLiveData

    fun requestReviewList() {
        repository.getReviewList(_reviewListLiveData)
    }
}