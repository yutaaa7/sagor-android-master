package com.sagarclientapp.ui.profile.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sagarclientapp.api.Response
import com.sagarclientapp.model.PagesResponse
import com.sagarclientapp.model.SavedPlacesResponse
import com.sagarclientapp.model.UpdateNotificationStatusResponse
import com.sagarclientapp.repository.PageRepository
import com.sagarclientapp.repository.SavedPlacesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedPlacesViewModel @Inject constructor(private val savedPlacesRepository: SavedPlacesRepository) :
    ViewModel() {

    var savedPlacesListResponse = MutableLiveData<Response<SavedPlacesResponse>>()
    var addToWishListResponse = MutableLiveData<Response<UpdateNotificationStatusResponse>>()


    fun getSavedPlacesList(

        authorization: String
    ) {
        viewModelScope.launch {
            var response = savedPlacesRepository.getSavedPlacesList(

                authorization = authorization
            )
            savedPlacesListResponse.postValue(Response.Loading(null))
            savedPlacesListResponse.postValue(response)
        }
    }

    fun addToWishList(
        authorization: String,
        locationId: String,
        wishlistFlag: String
    ) {
        viewModelScope.launch {
            var response = savedPlacesRepository.addToWishList(

                authorization = authorization,
                locationId = locationId,
                wishlistFlag = wishlistFlag
            )
            addToWishListResponse.postValue(Response.Loading(null))
            addToWishListResponse.postValue(response)
        }
    }


}