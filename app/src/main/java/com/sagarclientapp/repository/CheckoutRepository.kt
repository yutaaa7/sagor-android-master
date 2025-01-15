package com.sagarclientapp.repository

import com.sagarclientapp.api.ApiService
import com.sagarclientapp.api.Response
import com.sagarclientapp.api.ResponseHandler
import com.sagarclientapp.model.ConfirmBookingResponse
import com.sagarclientapp.model.CreateBookingResponse
import com.sagarclientapp.model.MakePaymentResponse
import com.sagarclientapp.model.PaymentrModesResponse
import com.sagarclientapp.utils.AppConstants
import javax.inject.Inject

class CheckoutRepository @Inject constructor(private val apiService: ApiService) {
    private val responseHandler: ResponseHandler by lazy {
        ResponseHandler()
    }

    suspend fun removePromoCode(

        authorization: String,
        user_id: String,
        booking_id: String,
        coupon: String,
        price: String,

        ): Response<CreateBookingResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.removeCoupon(
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

    suspend fun makePayment(
        authorization: String,
        amount: String,
        udf1: String,
        udf2: String,
        udf3: String,
        udf4: String,
        udf5: String,
        payment_type: String,
    ): Response<MakePaymentResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.makePayment(
                    authorization = authorization,
                    amount = amount,
                    udf1 = udf1,
                    udf2 = udf2,
                    udf3 = udf3,
                    udf4 = udf4,
                    udf5 = udf5,
                    payment_type = payment_type
                ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
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


    suspend fun confirmBooking(
        authorization: String,
        user_id: String,
        booking_id: String,
        payment_method: String,
        payment_type: String,
        payzahRefrenceCode: String,
        trackId: String,
        knetPaymentId: String,
        transactionNumber: String,
        trackingNumber: String,
        paymentDate: String,
        paymentStatus: String,
        udf1: String,
        udf2: String,
        udf3: String,
        udf4: String,
        coupon_code: String,

    ): Response<ConfirmBookingResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.confirmPayment(
                    authorization = authorization,
                    user_id = user_id,
                    booking_id=booking_id,
                    payment_method=payment_method,
                    payment_type=payment_type,
                    payzahRefrenceCode=payzahRefrenceCode,
                    trackId=trackId,
                    knetPaymentId=knetPaymentId,
                    transactionNumber=transactionNumber,
                    trackingNumber=trackingNumber,
                    paymentDate=paymentDate,
                    paymentStatus=paymentStatus,
                    udf1=udf1,
                    udf2=udf2,
                    udf3=udf3,
                    udf4=udf4,
                    coupon_code=coupon_code
                    ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }


}