package com.company.issadminpanel.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.company.issadminpanel.model.PaymentModel
import com.company.issadminpanel.repository.PaymentsRepository

class PaymentsViewModel(private val repository: PaymentsRepository) : ViewModel() {
    private val _paymentDetailsLiveData = MutableLiveData<ArrayList<PaymentModel>?>()
    val paymentDetailsLiveData: LiveData<ArrayList<PaymentModel>?>
        get() = _paymentDetailsLiveData

    fun requestPaymentDetails(userId: String) {
        repository.getPaymentDetails(userId, _paymentDetailsLiveData)
    }
}