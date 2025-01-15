package com.sagarclientapp.ui.home.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sagarclientapp.api.Response
import com.sagarclientapp.model.BannerResponse
import com.sagarclientapp.model.ChildListingResponse
import com.sagarclientapp.model.DirectionsResponse
import com.sagarclientapp.model.GetProfileResponse
import com.sagarclientapp.model.LastSearchLocationsResponse
import com.sagarclientapp.model.LocationCategoryResponse
import com.sagarclientapp.model.UpdateNotificationStatusResponse
import com.sagarclientapp.repository.HomeRepository
import com.sagarclientapp.repository.SelectRideRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val homeRepository: HomeRepository) :
    ViewModel() {

    var getProfileResponse = MutableLiveData<Response<GetProfileResponse>>()

    var lastSearchedLocationListResponse = MutableLiveData<Response<LastSearchLocationsResponse>>()
    var BannerListResponse = MutableLiveData<Response<BannerResponse>>()
    var childListResponse = MutableLiveData<Response<ChildListingResponse>>()
    var onGoingTripResponse = MutableLiveData<Response<UpdateNotificationStatusResponse>>()
    var updateFcmTokenResponse = MutableLiveData<Response<UpdateNotificationStatusResponse>>()

    fun getChildrenList(

        authorization: String,
        parent_id: String,
    ) {
        viewModelScope.launch {
            var response = homeRepository.getChildListing(
                authorization = authorization,
                parent_id = parent_id
            )
            childListResponse.postValue(Response.Loading(null))
            childListResponse.postValue(response)
        }
    }
    fun getProfile(
        user_id: String,
        authorization: String
    ) {
        viewModelScope.launch {
            var response = homeRepository.getProfile(
                user_id = user_id,
                authorization = authorization
            )
            getProfileResponse.postValue(Response.Loading(null))
            getProfileResponse.postValue(response)
        }
    }
    fun getLastSearchedLocationList(
        authorization: String
    ) {
        viewModelScope.launch {
            var response = homeRepository.getLastSearchedLocation(

                authorization = authorization
            )
            lastSearchedLocationListResponse.postValue(Response.Loading(null))
            lastSearchedLocationListResponse.postValue(response)
        }
    }

    fun updateFcmToken(
        authorization: String,
        country_code: String,
        mobile: String,
        device_type: String,
        device_token: String,
        device_version: String,
        fcm_token: String,
    ) {
        viewModelScope.launch {
            var response = homeRepository.updateFcmToken(
                authorization = authorization,
                country_code = country_code,
                mobile = mobile,
                device_type = device_type,
                device_token = device_token,
                device_version = device_version,
                fcm_token = fcm_token,
            )
            updateFcmTokenResponse.postValue(Response.Loading(null))
            updateFcmTokenResponse.postValue(response)
        }
    }


    fun getBannerList(
        authorization: String
    ) {
        viewModelScope.launch {
            var response = homeRepository.getBannerList(

                authorization = authorization
            )
            BannerListResponse.postValue(Response.Loading(null))
            BannerListResponse.postValue(response)
        }
    }

    fun onGoingTrips(
        authorization: String,
        user_id:String,
        booking_status:String,
    ) {
        viewModelScope.launch {
            var response = homeRepository.onGoingTrips(

                authorization = authorization,
                user_id = user_id,
                booking_status = booking_status,
            )
            onGoingTripResponse.postValue(Response.Loading(null))
            onGoingTripResponse.postValue(response)
        }
    }


}