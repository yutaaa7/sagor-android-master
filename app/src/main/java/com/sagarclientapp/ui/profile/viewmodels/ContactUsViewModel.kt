package com.sagarclientapp.ui.profile.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sagarclientapp.api.Response
import com.sagarclientapp.model.UpdateNotificationStatusResponse
import com.sagarclientapp.repository.ContactusRepository
import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class ContactUsViewModel @Inject constructor(private val contactusRepository: ContactusRepository) :
    ViewModel() {

    var contactUsResponse = MutableLiveData<Response<UpdateNotificationStatusResponse>>()

    fun contactusApi(
        authorization: String,
        user_id: RequestBody,
        message: RequestBody,
        files: MutableList<MultipartBody.Part>,
    ) {
        viewModelScope.launch {
            var response = contactusRepository.contactUs(
                authorization = authorization,
                user_id=user_id,
                message,files
            )
            contactUsResponse.postValue(Response.Loading(null))
            contactUsResponse.postValue(response)
        }
    }



}