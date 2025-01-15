package com.sagarclientapp.ui.login.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sagarclientapp.api.Response
import com.sagarclientapp.model.CountryListResponse
import com.sagarclientapp.model.LoginResponse
import com.sagarclientapp.model.UpdateNotificationStatusResponse
import com.sagarclientapp.repository.LoginNotificationRepository
import com.sagarclientapp.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(private val loginNotificationRepository: LoginNotificationRepository) :
    ViewModel() {

    var notificationUpdateResponse = MutableLiveData<Response<UpdateNotificationStatusResponse>>()


    fun updateNotificationStatus(
        user_id: String,
        device_token: String,
        notification_status: String,
        authorization: String
    ) {
        viewModelScope.launch {
            var response = loginNotificationRepository.updateNotificationStatus(
                user_id = user_id,
                device_token = device_token,
                notification_status = notification_status,
                authorization = authorization
            )
            notificationUpdateResponse.postValue(Response.Loading(null))
            notificationUpdateResponse.postValue(response)
        }
    }


}