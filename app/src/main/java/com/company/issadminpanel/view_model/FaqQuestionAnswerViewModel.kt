package com.company.issadminpanel.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.company.issadminpanel.model.FaqModel
import com.company.issadminpanel.repository.FaqQuestionAnswerRepository

class FaqQuestionAnswerViewModel(private val repository: FaqQuestionAnswerRepository) : ViewModel() {
    private val _faqListLiveData = MutableLiveData<ArrayList<FaqModel>?>()
    val faqListLiveData: LiveData<ArrayList<FaqModel>?>
        get() = _faqListLiveData

    fun requestFaqList(title: String) {
        repository.getFaqList(title, _faqListLiveData)
    }
}