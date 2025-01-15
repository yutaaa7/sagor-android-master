package com.sagarclientapp.ui.home.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sagarclientapp.api.Response
import com.sagarclientapp.model.BannerResponse
import com.sagarclientapp.model.HistoryResponse
import com.sagarclientapp.model.LastSearchLocationsResponse
import com.sagarclientapp.repository.HistoryRepository
import com.sagarclientapp.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TripsViewModel @Inject constructor(private val historyRepository: HistoryRepository) :
    ViewModel() {

    var historyResponse = MutableLiveData<Response<HistoryResponse>>()

    fun historyApi(
        authorization: String,
        user_id: String,
        booking_status: String
    ) {
        viewModelScope.launch {
            var response = historyRepository.history(

                authorization = authorization,
                user_id = user_id,
                booking_status=booking_status
            )
            historyResponse.postValue(Response.Loading(null))
            historyResponse.postValue(response)
        }
    }
    fun scheduledTripList(
        authorization: String,
        user_id: String,

    ) {
        viewModelScope.launch {
            var response = historyRepository.scheduledTripList(

                authorization = authorization,
                user_id = user_id,

            )
            historyResponse.postValue(Response.Loading(null))
            historyResponse.postValue(response)
        }
    }




}