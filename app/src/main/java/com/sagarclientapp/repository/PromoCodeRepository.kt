package com.sagarclientapp.repository

import com.sagarclientapp.api.ApiService
import com.sagarclientapp.api.Response
import com.sagarclientapp.api.ResponseHandler
import com.sagarclientapp.model.CreateBookingResponse
import com.sagarclientapp.model.PagesResponse
import com.sagarclientapp.utils.AppConstants
import javax.inject.Inject

class PromoCodeRepository @Inject constructor(private val apiService: ApiService) {
    private val responseHandler: ResponseHandler by lazy {
        ResponseHandler()
    }

    suspend fun applyPromoCode(
        authorization: String,
        user_id: String,
        booking_id: String,
        coupon: String,
        price: String,
        ): Response<CreateBookingResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.applyCoupon(
                    authorization = authorization,
                    user_id = user_id,
                    booking_id = booking_id,
                    coupon = coupon,
                    price = price
                ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }




}