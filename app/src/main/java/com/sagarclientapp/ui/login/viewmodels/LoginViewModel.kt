package com.sagarclientapp.ui.login.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sagarclientapp.api.Response
import com.sagarclientapp.model.CountryListResponse
import com.sagarclientapp.model.GetPhoneNumberResponse
import com.sagarclientapp.model.LoginResponse
import com.sagarclientapp.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val loginRepository: LoginRepository) :
    ViewModel() {

    var loginResponse = MutableLiveData<Response<LoginResponse>>()
    var editPhoneNumberResponse = MutableLiveData<Response<LoginResponse>>()
    var getPhoneNumberResponse = MutableLiveData<Response<GetPhoneNumberResponse>>()
    var countryListResponse = MutableLiveData<Response<CountryListResponse>>()

    fun login(country_code: String, mobile: String, type: String, device_token: String) {
        viewModelScope.launch {
            var response = loginRepository.login(
                country_code = country_code,
                mobile = mobile,
                type = type,
                device_token = device_token
            )
            loginResponse.postValue(Response.Loading(null))
            loginResponse.postValue(response)
        }
    }

    fun getPhoneNumber( user_id: String,
                         authorization: String) {
        viewModelScope.launch {
            var response = loginRepository.getPhoneNumber(
                user_id=user_id,
                authorization = authorization
            )
            getPhoneNumberResponse.postValue(Response.Loading(null))
            getPhoneNumberResponse.postValue(response)
        }
    }

    fun editPhoneNumber(
        authorization: String,
        pre_country_code: String,
        pre_mobile: String,
        device_type: String,
        device_token: String,
        type: String,
        device_version: String,
        new_country_code: String,
        new_mobile: String,) {
        viewModelScope.launch {
            var response = loginRepository.editPhoneNumber(
                authorization = authorization,
                pre_country_code=pre_country_code,
                pre_mobile=pre_mobile,
                device_type=device_type,
                device_token=device_token,
                type=type,
                device_version=device_version,
                new_country_code=new_country_code,
                new_mobile=new_mobile
            )
            editPhoneNumberResponse.postValue(Response.Loading(null))
            editPhoneNumberResponse.postValue(response)
        }
    }

    fun countryListApi() {
        viewModelScope.launch {
            var response = loginRepository.countryList()
            countryListResponse.postValue(Response.Loading(null))
            countryListResponse.postValue(response)
        }
    }


}