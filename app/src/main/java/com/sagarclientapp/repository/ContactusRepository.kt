package com.sagarclientapp.repository

import com.sagarclientapp.api.ApiService
import com.sagarclientapp.api.Response
import com.sagarclientapp.api.ResponseHandler
import com.sagarclientapp.model.UpdateNotificationStatusResponse
import com.sagarclientapp.utils.AppConstants

import okhttp3.MultipartBody
import okhttp3.RequestBody

import javax.inject.Inject

class ContactusRepository @Inject constructor(private val apiService: ApiService) {
    private val responseHandler: ResponseHandler by lazy {
        ResponseHandler()
    }

    suspend fun contactUs(

        authorization: String,
        user_id: RequestBody,
        message: RequestBody,
        files: MutableList<MultipartBody.Part>,


        ): Response<UpdateNotificationStatusResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.contactUs(

                    authorization = authorization,
                    user_id=user_id,
                    message,files
                ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }
 /*suspend fun contactUsWithoutFile(

        authorization: String,
        user_id: RequestBody,
        message:RequestBody

        ): Response<UpdateNotificationStatusResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.contactUs(

                    authorization = authorization,
                    user_id=user_id,
                    message
                ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }
*/

}