package com.sagarclientapp.repository

import com.sagarclientapp.api.ApiService
import com.sagarclientapp.api.Response
import com.sagarclientapp.api.ResponseHandler
import com.sagarclientapp.model.CreateBookingResponse
import com.sagarclientapp.model.PaymentrModesResponse
import com.sagarclientapp.utils.AppConstants
import javax.inject.Inject

class PaymentMethodsRepository @Inject constructor(private val apiService: ApiService) {
    private val responseHandler: ResponseHandler by lazy {
        ResponseHandler()
    }

    suspend fun paymentsModes(
        authorization: String,
        device_type: String
    ): Response<PaymentrModesResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.paymentMethodsAPi(
                    authorization = authorization,
                    device_type = device_type
                ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }




}