package com.sagarclientapp.ui.checkout.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sagarclientapp.api.Response
import com.sagarclientapp.model.BookingDetailResponse
import com.sagarclientapp.model.DriverStatusResponse
import com.sagarclientapp.model.SchoolDriverStatusResponse
import com.sagarclientapp.repository.MyRideChildRepository
import com.sagarclientapp.repository.MyRideRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyRideChildViewModel @Inject constructor(private val myRideChildRepository: MyRideChildRepository) :
    ViewModel() {


    var driverStatusResponse = MutableLiveData<Response<SchoolDriverStatusResponse>>()



    fun childDriverStatus(
        authorization: String,
        ride_id: String,
        child_id: String,
    ) {
        viewModelScope.launch {
            var response = myRideChildRepository.childDriverStatus(
                authorization = authorization,
                ride_id = ride_id,
                child_id=child_id
                )
            driverStatusResponse.postValue(Response.Loading(null))
            driverStatusResponse.postValue(response)
        }
    }


}