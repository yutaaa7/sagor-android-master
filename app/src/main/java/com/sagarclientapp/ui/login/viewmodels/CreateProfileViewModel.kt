package com.sagarclientapp.ui.login.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sagarclientapp.api.Response
import com.sagarclientapp.model.GetProfileResponse
import com.sagarclientapp.model.LoginResponse
import com.sagarclientapp.model.UpdateNotificationStatusResponse
import com.sagarclientapp.repository.CreateProfileRepository
import com.sagarclientapp.repository.LoginNotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateProfileViewModel @Inject constructor(private val createProfileRepository: CreateProfileRepository) :
    ViewModel() {

    var updateProfileResponse = MutableLiveData<Response<UpdateNotificationStatusResponse>>()
    var getProfileResponse = MutableLiveData<Response<GetProfileResponse>>()


    fun updateProfile(
        user_id: String,
        name: String,
        email: String,
        gender: String,
        authorization: String
    ) {
        viewModelScope.launch {
            var response = createProfileRepository.updateProfile(
                user_id = user_id,
                name = name,
                email = email,
                gender = gender,
                authorization = authorization
            )
            updateProfileResponse.postValue(Response.Loading(null))
            updateProfileResponse.postValue(response)
        }
    }

    fun getProfile(
        user_id: String,
        authorization: String
    ) {
        viewModelScope.launch {
            var response = createProfileRepository.getProfile(
                user_id = user_id,
                authorization = authorization
            )
            getProfileResponse.postValue(Response.Loading(null))
            getProfileResponse.postValue(response)
        }
    }


}