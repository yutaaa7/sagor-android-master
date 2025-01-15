package com.sagarclientapp.ui.checkout.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sagarclientapp.api.Response
import com.sagarclientapp.model.BookingDetailResponse
import com.sagarclientapp.model.CreateBookingResponse
import com.sagarclientapp.model.DriverStatusResponse
import com.sagarclientapp.model.UpdateNotificationStatusResponse
import com.sagarclientapp.repository.CheckoutRepository
import com.sagarclientapp.repository.MyRideRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyRideViewModel @Inject constructor(private val myRideRepository: MyRideRepository) :
    ViewModel() {

    var bookingDetailResponse = MutableLiveData<Response<BookingDetailResponse>>()
    var rescheduledBookingResponse = MutableLiveData<Response<BookingDetailResponse>>()
    var driverStatusResponse = MutableLiveData<Response<DriverStatusResponse>>()

    fun bookingDetailApi(
        authorization: String,
        booking_id: String
    ) {
        viewModelScope.launch {
            var response = myRideRepository.bookingDetail(
                authorization = authorization,

                booking_id = booking_id,

                )
            bookingDetailResponse.postValue(Response.Loading(null))
            bookingDetailResponse.postValue(response)
        }
    }

    fun driverStatus(
        authorization: String,
        booking_id: String
    ) {
        viewModelScope.launch {
            var response = myRideRepository.driverStatus(
                authorization = authorization,

                booking_id = booking_id,

                )
            driverStatusResponse.postValue(Response.Loading(null))
            driverStatusResponse.postValue(response)
        }
    }

    fun rescheduledBokkingOneWayApi(
        authorization: String,
        booking_id: String,
        pickup_date: String,
        pickup_time: String,
        pickup_time_unit: String,
        trip_mode: String,
    ) {
        viewModelScope.launch {
            var response = myRideRepository.RescheduledBookingOneWay(
                authorization = authorization,
                booking_id = booking_id,
                pickup_date = pickup_date,
                pickup_time = pickup_time,
                pickup_time_unit = pickup_time_unit,
                trip_mode = trip_mode
            )
            rescheduledBookingResponse.postValue(Response.Loading(null))
            rescheduledBookingResponse.postValue(response)
        }
    }
}