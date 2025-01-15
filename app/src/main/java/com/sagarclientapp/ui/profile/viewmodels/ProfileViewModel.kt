package com.sagarclientapp.ui.profile.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sagarclientapp.api.Response
import com.sagarclientapp.model.GetLanguageStatusResponse
import com.sagarclientapp.model.GetNotificationStatusResponse
import com.sagarclientapp.model.UpdateNotificationStatusResponse
import com.sagarclientapp.repository.LoginNotificationRepository
import com.sagarclientapp.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val profileRepository: ProfileRepository) :
    ViewModel() {

    var notificationUpdateResponse = MutableLiveData<Response<UpdateNotificationStatusResponse>>()
    var getNotificationStatusResponse = MutableLiveData<Response<GetNotificationStatusResponse>>()
    var getLanguageStatusResponse = MutableLiveData<Response<GetLanguageStatusResponse>>()
    var deleteAccountResponse = MutableLiveData<Response<UpdateNotificationStatusResponse>>()
    var languageUpdateResponse = MutableLiveData<Response<UpdateNotificationStatusResponse>>()


    fun updateNotificationStatus(
        user_id: String,
        device_token: String,
        notification_status: String,
        authorization: String
    ) {
        viewModelScope.launch {
            var response = profileRepository.updateNotificationStatus(
                user_id = user_id,
                device_token = device_token,
                notification_status = notification_status,
                authorization = authorization
            )
            notificationUpdateResponse.postValue(Response.Loading(null))
            notificationUpdateResponse.postValue(response)
        }
    }

    fun getNotificationStatus(
        user_id: String,
        device_token: String,
        authorization: String
    ) {
        viewModelScope.launch {
            var response = profileRepository.getNotificationStatus(
                user_id = user_id,
                device_token = device_token,
                authorization = authorization
            )
            getNotificationStatusResponse.postValue(Response.Loading(null))
            getNotificationStatusResponse.postValue(response)
        }
    }
    fun getLanguageStatus(
        user_id: String,
        device_token: String,
        authorization: String
    ) {
        viewModelScope.launch {
            var response = profileRepository.getLanguageStatus(
                user_id = user_id,
                device_token = device_token,
                authorization = authorization
            )
            getLanguageStatusResponse.postValue(Response.Loading(null))
            getLanguageStatusResponse.postValue(response)
        }
    }

    fun deleteAccount(

        authorization: String
    ) {
        viewModelScope.launch {
            var response = profileRepository.deleteAccount(

                authorization = authorization
            )
            deleteAccountResponse.postValue(Response.Loading(null))
            deleteAccountResponse.postValue(response)
        }
    }

    fun logout(

        authorization: String
    ) {
        viewModelScope.launch {
            var response = profileRepository.logout(

                authorization = authorization
            )
            deleteAccountResponse.postValue(Response.Loading(null))
            deleteAccountResponse.postValue(response)
        }
    }

    fun languageUpdate(
        user_id: String,
        device_token: String,
        lang_id: String,
        authorization: String
    ) {
        viewModelScope.launch {
            var response = profileRepository.languageUpdate(
                user_id = user_id,
                device_token = device_token,
                lang_id = lang_id,
                authorization = authorization
            )
            languageUpdateResponse.postValue(Response.Loading(null))
            languageUpdateResponse.postValue(response)
        }
    }

}