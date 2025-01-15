package com.sagarclientapp.model

import com.google.gson.annotations.SerializedName

data class ConfirmBookingResponse(

	@field:SerializedName("data")
	val data: List<ConfirmBookingDataItem>? = mutableListOf(),

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class ConfirmBookingDataItem(

	@field:SerializedName("coupon_code")
	val couponCode: String? = null,

	@field:SerializedName("discount_price")
	val discountPrice: Any? = null,

	@field:SerializedName("created_by")
	val createdBy: Int? = null,

	@field:SerializedName("booking_id")
	val bookingId: String? = null,

	@field:SerializedName("price_unit")
	val priceUnit: String? = null,


	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("price")
	val price: String? = null,

	@field:SerializedName("vehicle_cat_id")
	val vehicleCatId: Int? = null,


	@field:SerializedName("payment")
	val payment: ConfirmBookingPayment? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("trip_mode")
	val tripMode: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class ConfirmBookingPayment(

	@field:SerializedName("trackId")
	val trackId: String? = null,

	@field:SerializedName("payzahRefrenceCode")
	val payzahRefrenceCode: String? = null,

	@field:SerializedName("transactionNumber")
	val transactionNumber: String? = null,

	@field:SerializedName("udf5")
	val udf5: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("udf3")
	val udf3: String? = null,

	@field:SerializedName("knetPaymentId")
	val knetPaymentId: String? = null,

	@field:SerializedName("udf4")
	val udf4: String? = null,

	@field:SerializedName("udf1")
	val udf1: String? = null,

	@field:SerializedName("udf2")
	val udf2: String? = null,

	@field:SerializedName("deleted_at")
	val deletedAt: Any? = null,

	@field:SerializedName("booking_id")
	val bookingId: Int? = null,

	@field:SerializedName("payment_type")
	val paymentType: Int? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("paymentDate")
	val paymentDate: String? = null,

	@field:SerializedName("trackingNumber")
	val trackingNumber: String? = null,

	@field:SerializedName("paymentStatus")
	val paymentStatus: String? = null
)

