package com.sagarclientapp.repository

import com.sagarclientapp.api.ApiService
import com.sagarclientapp.api.Response
import com.sagarclientapp.api.ResponseHandler
import com.sagarclientapp.model.CancelBookingReasonsResponse
import com.sagarclientapp.model.CreateBookingResponse
import com.sagarclientapp.model.PagesResponse
import com.sagarclientapp.utils.AppConstants
import javax.inject.Inject

class CancelRideRepository @Inject constructor(private val apiService: ApiService) {
    private val responseHandler: ResponseHandler by lazy {
        ResponseHandler()
    }

    suspend fun cancelRideReasons(
        authorization: String
    ): Response<CancelBookingReasonsResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.cancelRideReasons(
                    authorization = authorization
                ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }

    suspend fun cancelBooking(
        authorization: String,
        booking_id: String,
        user_id: String,
        reason_id: String,
    ): Response<CreateBookingResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.cancelBooking(
                    authorization = authorization,
                    booking_id = booking_id,
                    user_id = user_id,
                    reason_id = reason_id
                ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }


}