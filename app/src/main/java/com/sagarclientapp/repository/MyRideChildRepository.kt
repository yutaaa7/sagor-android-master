package com.sagarclientapp.repository

import com.sagarclientapp.api.ApiService
import com.sagarclientapp.api.Response
import com.sagarclientapp.api.ResponseHandler
import com.sagarclientapp.model.BookingDetailResponse
import com.sagarclientapp.model.DriverStatusResponse
import com.sagarclientapp.model.SchoolDriverStatusResponse
import com.sagarclientapp.utils.AppConstants
import javax.inject.Inject

class MyRideChildRepository @Inject constructor(private val apiService: ApiService) {
    private val responseHandler: ResponseHandler by lazy {
        ResponseHandler()
    }


    suspend fun childDriverStatus(
        authorization: String,
        ride_id: String,
        child_id: String,
    ): Response<SchoolDriverStatusResponse> {
        return try {
            responseHandler.handleSuccess(
                apiService.childDriverStatus(
                    authorization = authorization,
                    ride_id = ride_id,
                    child_id=child_id
                    ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }


}