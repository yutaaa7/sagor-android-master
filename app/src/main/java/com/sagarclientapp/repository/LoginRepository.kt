package com.sagarclientapp.repository

import com.sagarclientapp.api.ApiService
import com.sagarclientapp.api.Response
import com.sagarclientapp.api.ResponseHandler
import com.sagarclientapp.model.CountryListResponse
import com.sagarclientapp.model.GetPhoneNumberResponse
import com.sagarclientapp.model.LoginResponse
import com.sagarclientapp.utils.AppConstants
import retrofit2.http.Field
import javax.inject.Inject

class LoginRepository @Inject constructor(private val apiService: ApiService) {
    private val responseHandler: ResponseHandler by lazy {
        ResponseHandler()
    }

    suspend fun login(
        country_code: String,
        mobile: String,
        device_token: String,
        type: String
    ): Response<LoginResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.login(
                    country_code = country_code,
                    mobile = mobile,
                    type = type,
                    device_type = AppConstants.DEVICE_TYPE,
                    device_token = device_token
                ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }

    suspend fun getPhoneNumber(
        user_id: String,
        authorization: String
    ): Response<GetPhoneNumberResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.getPhoneNumber(
                    user_id = user_id,
                    authorization = authorization
                ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }

    suspend fun editPhoneNumber(

        authorization: String,
        pre_country_code: String,
        pre_mobile: String,
        device_type: String,
        device_token: String,
        type: String,
        device_version: String,
        new_country_code: String,
        new_mobile: String,

    ): Response<LoginResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.editPhoneNumber(
                    authorization = authorization,
                    pre_country_code=pre_country_code,
                    pre_mobile=pre_mobile,
                    device_type=device_type,
                    device_token=device_token,
                    type=type,
                    device_version=device_version,
                    new_country_code=new_country_code,
                    new_mobile=new_mobile

                ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }

    suspend fun countryList(): Response<CountryListResponse> {

        return try {
            responseHandler.handleSuccess(apiService.countryList(), AppConstants.USER_LOGIN)
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }


}