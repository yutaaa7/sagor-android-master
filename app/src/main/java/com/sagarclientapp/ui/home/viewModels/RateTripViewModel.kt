package com.sagarclientapp.ui.home.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sagarclientapp.api.Response
import com.sagarclientapp.model.CountryListResponse
import com.sagarclientapp.model.UpdateNotificationStatusResponse
import com.sagarclientapp.repository.RateTripRepository
import com.sagarclientapp.repository.ReportAnIssueRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RateTripViewModel @Inject constructor(private val rateTripRepository: RateTripRepository) :
    ViewModel() {

    var rateTripResponse = MutableLiveData<Response<UpdateNotificationStatusResponse>>()

    fun rateTrip(
        authorization: String,
        user_id: String,
        booking_id: String,
        driver_id: String,
        rating: String,
        comment: String,
    ) {
        viewModelScope.launch {
            var response = rateTripRepository.rateTrip(
                authorization = authorization,
                user_id=user_id,
                booking_id=booking_id,
                driver_id=driver_id,
                rating=rating,
                comment=comment
            )
            rateTripResponse.postValue(Response.Loading(null))
            rateTripResponse.postValue(response)
        }
    }



}