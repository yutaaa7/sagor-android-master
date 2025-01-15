package com.sagarclientapp.repository

import com.sagarclientapp.api.ApiService
import com.sagarclientapp.api.Response
import com.sagarclientapp.api.ResponseHandler
import com.sagarclientapp.model.CountryListResponse
import com.sagarclientapp.model.GetProfileResponse
import com.sagarclientapp.model.LoginResponse
import com.sagarclientapp.model.UpdateNotificationStatusResponse
import com.sagarclientapp.utils.AppConstants
import javax.inject.Inject

class CreateProfileRepository @Inject constructor(private val apiService: ApiService) {
    private val responseHandler: ResponseHandler by lazy {
        ResponseHandler()
    }

    suspend fun updateProfile(
        user_id: String,
        name: String,
        email: String,
        gender: String,
        authorization: String
    ): Response<UpdateNotificationStatusResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.updateProfile(
                    user_id = user_id,
                    name = name,
                    email = email,
                    gender = gender,
                    authorization = authorization

                ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }

    suspend fun getProfile(
        user_id: String,
        authorization: String
    ): Response<GetProfileResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.getUserProfile(
                    userId = user_id,
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