package com.sagarclientapp.ui.checkout.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sagarclientapp.api.Response
import com.sagarclientapp.model.CreateBookingResponse
import com.sagarclientapp.repository.PromoCodeRepository
import com.sagarclientapp.repository.SelectRideWithTypeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PromoCodeViewModel @Inject constructor(private val promoCodeRepository: PromoCodeRepository) :
    ViewModel() {
    var applyCouponResponse = MutableLiveData<Response<CreateBookingResponse>>()

    fun applyCouponApi(

        authorization: String,
        userId: String,
        booking_id: String,
        coupon: String,
        price: String,

        ) {
        viewModelScope.launch {
            var response = promoCodeRepository.applyPromoCode(
                authorization = authorization,
                user_id = userId,
                booking_id = booking_id,
                coupon = coupon,
                price = price
            )
            applyCouponResponse.postValue(Response.Loading(null))
            applyCouponResponse.postValue(response)
        }
    }
}