package com.sagarclientapp.ui.checkout.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sagarclientapp.api.Response
import com.sagarclientapp.model.CancelBookingReasonsResponse
import com.sagarclientapp.model.CreateBookingResponse
import com.sagarclientapp.model.PaymentrModesResponse
import com.sagarclientapp.repository.CancelRideRepository
import com.sagarclientapp.repository.PaymentMethodsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentMethodsViewModel @Inject constructor(private val paymentMethodsRepository: PaymentMethodsRepository) :
    ViewModel() {

    var paymentMethodsResponse = MutableLiveData<Response<PaymentrModesResponse>>()

    fun paymentModesApi(
        authorization: String,
        device_type: String,


        ) {
        viewModelScope.launch {
            var response = paymentMethodsRepository.paymentsModes(
                authorization = authorization,
                device_type = device_type,


                )
            paymentMethodsResponse.postValue(Response.Loading(null))
            paymentMethodsResponse.postValue(response)
        }
    }


}