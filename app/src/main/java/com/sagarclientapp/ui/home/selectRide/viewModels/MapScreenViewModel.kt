package com.sagarclientapp.ui.home.selectRide.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sagarclientapp.api.Response
import com.sagarclientapp.model.LocationCategoryResponse
import com.sagarclientapp.model.UpdateNotificationStatusResponse
import com.sagarclientapp.repository.MapScreenRepository
import com.sagarclientapp.repository.SelectRideRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapScreenViewModel @Inject constructor(private val mapScreenRepository: MapScreenRepository) :
    ViewModel() {

    var addLastLocationSearchResponse =
        MutableLiveData<Response<UpdateNotificationStatusResponse>>()


    fun addLastLocationSearch(
        authorization: String,
        userId: String,
        address: String,
        lat: String,
        long: String,
        title: String,
    ) {
        viewModelScope.launch {
            var response = mapScreenRepository.addLastSearchLocation(

                authorization = authorization,
                userId = userId,
                address = address,
                lat = lat,
                long = long,
                title = title
            )
            addLastLocationSearchResponse.postValue(Response.Loading(null))
            addLastLocationSearchResponse.postValue(response)
        }
    }





}