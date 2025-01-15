package com.sagarclientapp.repository

import com.sagarclientapp.api.ApiService
import com.sagarclientapp.api.Response
import com.sagarclientapp.api.ResponseHandler
import com.sagarclientapp.model.LoginResponse
import com.sagarclientapp.model.UpdateNotificationStatusResponse
import com.sagarclientapp.model.VerifyOtpResponse
import com.sagarclientapp.utils.AppConstants
import javax.inject.Inject

class OtpScreenRepository @Inject constructor(private val apiService: ApiService) {
    private val responseHandler: ResponseHandler by lazy {
        ResponseHandler()
    }

    suspend fun resendOtp(
        country_code: String,
        mobile: String,
        device_token: String
    ): Response<LoginResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.resendOtp(
                    country_code,
                    mobile,
                    AppConstants.DEVICE_TYPE,
                    device_token
                ), AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }

    suspend fun verifyOtp(
        country_code: String,
        mobile: String,
        otp: String,
        device_type: String,
        device_token: String
    ): Response<VerifyOtpResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.verifyOtp(
                    country_code,
                    mobile,
                    otp,
                    device_type,
                    device_token
                ), AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }

    suspend fun verifyOtpForEditMobileNo(
        authorization: String,
        country_code: String,
        mobile: String,
        otp: String,
        device_type: String,
        device_token: String,
        device_version: String
    ): Response<VerifyOtpResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.verifyOtpForUpdatePhoneNumber(
                    authorization,
                    country_code,
                    mobile,
                    otp,
                    device_type,
                    device_token,
                    device_version
                ), AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }

    suspend fun languageUpdate(
        user_id: String,
        device_token: String,
        lang_id: String,
        authorization: String
    ): Response<UpdateNotificationStatusResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.languageUpdate(
                    user_id = user_id,
                    device_type = AppConstants.DEVICE_TYPE,
                    device_token = device_token,
                    lang_id = lang_id,
                    authorization = authorization
                ), AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }


}