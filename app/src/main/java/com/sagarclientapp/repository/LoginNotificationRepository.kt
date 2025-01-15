package com.sagarclientapp.repository

import com.sagarclientapp.api.ApiService
import com.sagarclientapp.api.Response
import com.sagarclientapp.api.ResponseHandler
import com.sagarclientapp.model.CountryListResponse
import com.sagarclientapp.model.LoginResponse
import com.sagarclientapp.model.UpdateNotificationStatusResponse
import com.sagarclientapp.utils.AppConstants
import javax.inject.Inject

class LoginNotificationRepository @Inject constructor(private val apiService: ApiService) {
    private val responseHandler: ResponseHandler by lazy {
        ResponseHandler()
    }

    suspend fun updateNotificationStatus(
        user_id: String,
        device_token: String,
        notification_status: String,
        authorization: String
    ): Response<UpdateNotificationStatusResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.updateNotificationStatus(
                    user_id = user_id,
                    device_type = AppConstants.DEVICE_TYPE,
                    device_token = device_token,
                    notification_status = notification_status,
                    authorization = authorization
                ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }


}