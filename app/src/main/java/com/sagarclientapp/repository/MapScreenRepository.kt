package com.sagarclientapp.repository

import com.sagarclientapp.api.ApiService
import com.sagarclientapp.api.Response
import com.sagarclientapp.api.ResponseHandler
import com.sagarclientapp.model.PagesResponse
import com.sagarclientapp.model.UpdateNotificationStatusResponse
import com.sagarclientapp.utils.AppConstants
import javax.inject.Inject

class MapScreenRepository @Inject constructor(private val apiService: ApiService) {
    private val responseHandler: ResponseHandler by lazy {
        ResponseHandler()
    }

    suspend fun addLastSearchLocation(

        authorization: String,
        userId: String,
        address: String,
        lat: String,
        long: String,
        title: String,

        ): Response<UpdateNotificationStatusResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.addLastSearchLocation(

                    authorization = authorization,
                    user_id = userId,
                    address = address,
                    lat = lat,
                    long = long,
                    title = title
                ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }



}