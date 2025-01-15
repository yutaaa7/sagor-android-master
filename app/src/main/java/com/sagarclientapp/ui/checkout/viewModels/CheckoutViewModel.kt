package com.sagarclientapp.ui.checkout.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sagarclientapp.api.Response
import com.sagarclientapp.model.ConfirmBookingResponse
import com.sagarclientapp.model.CreateBookingResponse
import com.sagarclientapp.model.MakePaymentResponse
import com.sagarclientapp.model.PaymentrModesResponse
import com.sagarclientapp.repository.CheckoutRepository
import com.sagarclientapp.repository.PromoCodeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(private val checkoutRepository: CheckoutRepository) :
    ViewModel() {
    var removeCouponResponse = MutableLiveData<Response<CreateBookingResponse>>()
    var makePaymentResponse = MutableLiveData<Response<MakePaymentResponse>>()
    var confirmBookingResponse = MutableLiveData<Response<ConfirmBookingResponse>>()

    var paymentMethodsResponse = MutableLiveData<Response<PaymentrModesResponse>>()

    fun paymentModesApi(
        authorization: String,
        device_type: String,


        ) {
        viewModelScope.launch {
            var response = checkoutRepository.paymentsModes(
                authorization = authorization,
                device_type = device_type,


                )
            paymentMethodsResponse.postValue(Response.Loading(null))
            paymentMethodsResponse.postValue(response)
        }
    }


    fun removeCouponApi(
        authorization: String,
        userId: String,
        booking_id: String,
        coupon: String,
        price: String,

        ) {
        viewModelScope.launch {
            var response = checkoutRepository.removePromoCode(
                authorization = authorization,
                user_id = userId,
                booking_id = booking_id,
                coupon = coupon,
                price = price
            )
            removeCouponResponse.postValue(Response.Loading(null))
            removeCouponResponse.postValue(response)
        }
    }

    fun makePaymentApi(
        authorization: String,
        amount: String,
        udf1: String,
        udf2: String,
        udf3: String,
        udf4: String,
        udf5: String,
        payment_type: String

    ) {
        viewModelScope.launch {
            var response = checkoutRepository.makePayment(
                authorization = authorization,
                amount = amount,
                udf1 = udf1,
                udf2 = udf2,
                udf3 = udf3,
                udf4 = udf4,
                udf5 = udf5,
                payment_type = payment_type
            )
            makePaymentResponse.postValue(Response.Loading(null))
            makePaymentResponse.postValue(response)
        }
    }

    fun confirmBookingApi(
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

    ) {
        viewModelScope.launch {
            var response = checkoutRepository.confirmBooking(
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
            )
            confirmBookingResponse.postValue(Response.Loading(null))
            confirmBookingResponse.postValue(response)
        }
    }
}