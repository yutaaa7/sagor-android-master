package com.sagarclientapp.repository

import com.sagarclientapp.api.ApiService
import com.sagarclientapp.api.Response
import com.sagarclientapp.api.ResponseHandler
import com.sagarclientapp.model.GetLanguageStatus
import com.sagarclientapp.model.GetLanguageStatusResponse
import com.sagarclientapp.model.GetNotificationStatusResponse
import com.sagarclientapp.model.UpdateNotificationStatusResponse
import com.sagarclientapp.utils.AppConstants
import javax.inject.Inject

class ProfileRepository @Inject constructor(private val apiService: ApiService) {
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

    suspend fun getNotificationStatus(
        user_id: String,
        device_token: String,
        authorization: String
    ): Response<GetNotificationStatusResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.getNotificationStatus(
                    user_id = user_id,
                    device_type = AppConstants.DEVICE_TYPE,
                    device_token = device_token,
                    authorization = authorization
                ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }

    suspend fun getLanguageStatus(
        user_id: String,
        device_token: String,
        authorization: String
    ): Response<GetLanguageStatusResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.getLanguageStatus(
                    user_id = user_id,
                    device_type = AppConstants.DEVICE_TYPE,
                    device_token = device_token,
                    authorization = authorization
                ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }

    suspend fun deleteAccount(
        authorization: String
    ): Response<UpdateNotificationStatusResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.deleteAccount(

                    authorization = authorization
                ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }

    suspend fun logout(
        authorization: String
    ): Response<UpdateNotificationStatusResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.logout(

                    authorization = authorization
                ),
                AppConstants.USER_LOGIN
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