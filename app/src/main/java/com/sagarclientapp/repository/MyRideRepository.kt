package com.sagarclientapp.repository

import com.sagarclientapp.api.ApiService
import com.sagarclientapp.api.Response
import com.sagarclientapp.api.ResponseHandler
import com.sagarclientapp.model.BookingDetailResponse
import com.sagarclientapp.model.CreateBookingResponse
import com.sagarclientapp.model.DriverStatusResponse
import com.sagarclientapp.model.UpdateNotificationStatusResponse
import com.sagarclientapp.utils.AppConstants
import javax.inject.Inject

class MyRideRepository @Inject constructor(private val apiService: ApiService) {
    private val responseHandler: ResponseHandler by lazy {
        ResponseHandler()
    }

    suspend fun bookingDetail(
        authorization: String,
        booking_id: String,
    ): Response<BookingDetailResponse> {
        return try {
            responseHandler.handleSuccess(
                apiService.bookingDetails(
                    authorization = authorization,
                    booking_id = booking_id,

                    ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }
    suspend fun driverStatus(
        authorization: String,
        booking_id: String,
    ): Response<DriverStatusResponse> {
        return try {
            responseHandler.handleSuccess(
                apiService.driverStatus(
                    authorization = authorization,
                    booking_id = booking_id,

                    ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }

    suspend fun RescheduledBookingOneWay(
        authorization: String,
        booking_id: String,
        pickup_date: String,
        pickup_time: String,
        pickup_time_unit: String,
        trip_mode: String,
    ): Response<BookingDetailResponse> {
        return try {
            responseHandler.handleSuccess(
                apiService.RescheduledBookingForOneWay(
                    authorization = authorization,
                    booking_id = booking_id,
                    pickup_date = pickup_date,
                    pickup_time = pickup_time,
                    pickup_time_unit = pickup_time_unit,
                    trip_mode = trip_mode
                ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }
}