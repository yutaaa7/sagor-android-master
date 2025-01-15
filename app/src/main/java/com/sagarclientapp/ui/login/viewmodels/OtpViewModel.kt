package com.sagarclientapp.ui.login.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sagarclientapp.api.Response
import com.sagarclientapp.model.LoginResponse
import com.sagarclientapp.model.UpdateNotificationStatusResponse
import com.sagarclientapp.model.VerifyOtpResponse
import com.sagarclientapp.repository.LoginRepository
import com.sagarclientapp.repository.OtpScreenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtpViewModel @Inject constructor(private val otpScreenRepository: OtpScreenRepository) :
    ViewModel() {

    var loginResponse = MutableLiveData<Response<LoginResponse>>()
    var resendOtpResponse = MutableLiveData<Response<LoginResponse>>()
    var verifyOtpResponse = MutableLiveData<Response<VerifyOtpResponse>>()
    var verifyOtpForEditPhoneResponse = MutableLiveData<Response<VerifyOtpResponse>>()
    var languageUpdateResponse = MutableLiveData<Response<UpdateNotificationStatusResponse>>()

    fun resendOtp(country_code: String, mobile: String, device_token: String) {
        viewModelScope.launch {
            var response = otpScreenRepository.resendOtp(country_code, mobile, device_token)
            loginResponse.postValue(Response.Loading(null))
            loginResponse.postValue(response)
        }
    }

    fun verifyOtp(
        country_code: String,
        mobile: String,
        otp: String,
        device_type: String,
        device_token: String
    ) {
        viewModelScope.launch {
            var response =
                otpScreenRepository.verifyOtp(country_code, mobile, otp, device_type, device_token)
            verifyOtpResponse.postValue(Response.Loading(null))
            verifyOtpResponse.postValue(response)
        }
    }

    fun verifyOtpForEditPhoneNo(
        authorization: String,
        country_code: String,
        mobile: String,
        otp: String,
        device_type: String,
        device_token: String,
        device_version: String
    ) {
        viewModelScope.launch {
            var response =
                otpScreenRepository.verifyOtpForEditMobileNo(
                    authorization,
                    country_code,
                    mobile,
                    otp,
                    device_type,
                    device_token,
                    device_version)
            verifyOtpForEditPhoneResponse.postValue(Response.Loading(null))
            verifyOtpForEditPhoneResponse.postValue(response)
        }
    }

    fun languageUpdate(
        user_id: String,
        device_token: String,
        lang_id: String,
        authorization: String
    ) {
        viewModelScope.launch {
            var response = otpScreenRepository.languageUpdate(
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