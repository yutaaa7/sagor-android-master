package com.sagarclientapp.ui.checkout.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sagarclientapp.api.Response
import com.sagarclientapp.model.CancelBookingReasonsResponse
import com.sagarclientapp.model.CreateBookingResponse
import com.sagarclientapp.repository.CancelRideRepository
import com.sagarclientapp.repository.MyRideRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CancelBookingViewModels @Inject constructor(private val cancelRideRepository: CancelRideRepository) :
    ViewModel() {

    var cancelBookingReasonsResponse = MutableLiveData<Response<CancelBookingReasonsResponse>>()
    var cancelBookingResponse = MutableLiveData<Response<CreateBookingResponse>>()

    fun cancelBookingReasonsApi(
        authorization: String,


        ) {
        viewModelScope.launch {
            var response = cancelRideRepository.cancelRideReasons(
                authorization = authorization,


                )
            cancelBookingReasonsResponse.postValue(Response.Loading(null))
            cancelBookingReasonsResponse.postValue(response)
        }
    }

    fun cancelBookingApi(
        authorization: String,
        booking_id: String,
        user_id: String,
        reason_id: String,

        ) {
        viewModelScope.launch {
            var response = cancelRideRepository.cancelBooking(
                authorization = authorization,
                booking_id = booking_id,
                user_id = user_id,
                reason_id = reason_id


            )
            cancelBookingResponse.postValue(Response.Loading(null))
            cancelBookingResponse.postValue(response)
        }
    }
}