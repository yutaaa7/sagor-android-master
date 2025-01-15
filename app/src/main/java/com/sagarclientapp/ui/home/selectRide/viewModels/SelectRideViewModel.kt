package com.sagarclientapp.ui.home.selectRide.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sagarclientapp.api.Response
import com.sagarclientapp.model.DirectionsResponse
import com.sagarclientapp.model.LastSearchLocationsResponse
import com.sagarclientapp.model.LocationCategoryResponse
import com.sagarclientapp.model.UpdateNotificationStatusResponse
import com.sagarclientapp.repository.LoginNotificationRepository
import com.sagarclientapp.repository.SelectRideRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectRideViewModel @Inject constructor(private val selectRideRepository: SelectRideRepository) :
    ViewModel() {

    var getLocationCategoryResponse = MutableLiveData<Response<LocationCategoryResponse>>()
    var lastSearchedLocationListResponse = MutableLiveData<Response<LastSearchLocationsResponse>>()
    var locationListByCategoryIdResponse = MutableLiveData<Response<LastSearchLocationsResponse>>()
    var addToWishListResponse = MutableLiveData<Response<UpdateNotificationStatusResponse>>()
    var getDistanceResponse = MutableLiveData<Response<DirectionsResponse>>()


    fun getLocationCategory(

        authorization: String
    ) {
        viewModelScope.launch {
            var response = selectRideRepository.getLocationCategory(

                authorization = authorization
            )
            getLocationCategoryResponse.postValue(Response.Loading(null))
            getLocationCategoryResponse.postValue(response)
        }
    }

    fun getLocationListByCategoryId(

        authorization: String,
        category_id: String
    ) {
        viewModelScope.launch {
            var response = selectRideRepository.getLocationListByCategoryId(

                authorization = authorization,
                category_id = category_id
            )
            locationListByCategoryIdResponse.postValue(Response.Loading(null))
            locationListByCategoryIdResponse.postValue(response)
        }
    }

    fun getLastSearchedLocationList(
        authorization: String
    ) {
        viewModelScope.launch {
            var response = selectRideRepository.getLastSearchedLocation(

                authorization = authorization
            )
            lastSearchedLocationListResponse.postValue(Response.Loading(null))
            lastSearchedLocationListResponse.postValue(response)
        }
    }

    fun addToWishList(
        authorization: String,
        locationId: String,
        wishlistFlag: String
    ) {
        viewModelScope.launch {
            var response = selectRideRepository.addToWishList(

                authorization = authorization,
                locationId = locationId,
                wishlistFlag = wishlistFlag
            )
            addToWishListResponse.postValue(Response.Loading(null))
            addToWishListResponse.postValue(response)
        }
    }

    fun getDistanceWithPoints(
        origin: String,
        destination: String,
        apiKey: String
    ) {
        viewModelScope.launch {
            var response = selectRideRepository.getDirections(
                origin, destination, apiKey
            )
            getDistanceResponse.postValue(Response.Loading(null))
            getDistanceResponse.postValue(response)
        }
    }


}